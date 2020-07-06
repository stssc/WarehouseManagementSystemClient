package jnu.ssc.client.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jnu.ssc.client.R;
import jnu.ssc.client.model.Clothes;
import jnu.ssc.client.controller.NetworkProxy;
import jnu.ssc.client.model.InventoryTaskDetail;

public class InventoryActivity extends AppCompatActivity implements Serializable{//因为内部类需要序列化传递给另一个Activity，搞得这个Activity也得跟着序列化

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //获取控件
        Spinner shelfSpinner=findViewById(R.id.spinner_shelf);
        final Spinner positionSpinner=findViewById(R.id.spinner_position);
        final EditText amountEdit=findViewById(R.id.edit_amount);
        final TextView clothesIdText=findViewById(R.id.text_clothes_id);
        Button inventoryButton=findViewById(R.id.button_inventory);
        Button inventoryOverButton=findViewById(R.id.button_inventory_over);

        //获取待盘点列表
        final Clothes[][] inventories = new Clothes[1][];
        try {
            Thread thread=new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    try {
                        inventories[0] = NetworkProxyAdapter.queryInventoryTask(LoginActivity.getUserId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();//强行转多线程为单线程，必须让主线程等网络子线程执行完才能获取到非空的inventories[0]！！！
        } catch (Exception e) {
            e.printStackTrace();
        }

        //获取盘点任务中的所有货架和对应的货位信息，以及货架+货位和货架+货位对应的服装ID和数量信息
        Set<String> shelfSet=new HashSet<>();
        Map<String,Set<Integer>> positionSet=new HashMap<>();
        final ShelfAndPosition[] inventoryKey=new ShelfAndPosition[inventories[0].length];
        final Map<String,IdAndAmount> inventoryMap =new HashMap<>();
        for (int i=0;i<inventories[0].length;i++){
            shelfSet.add(inventories[0][i].getShelf());
        }
        Iterator<String> shelfIterator=shelfSet.iterator();
        while (shelfIterator.hasNext()){
            String shelf=shelfIterator.next();
            positionSet.put(shelf,new HashSet<Integer>());
            int j=0;
            for (int i=0;i<inventories[0].length;i++){
                if (inventories[0][i].getShelf().equals(shelf)){
                    int position=inventories[0][i].getPosition();
                    positionSet.get(shelf).add(position);
                    inventoryKey[j++]=new ShelfAndPosition(shelf,position);
                    inventoryMap.put(new ShelfAndPosition(shelf,position).toString(),new IdAndAmount(inventories[0][i].getId(),inventories[0][i].getAmount()));
                }
            }
        }
        //转数组存储，适配下面的ArrayAdapter
        final String[] shelves=new String[shelfSet.size()];
        final Map<String,Integer[]> positions=new HashMap<>();
        shelfIterator=shelfSet.iterator();
        int i=0;
        while (shelfIterator.hasNext()){
            String shelf=shelfIterator.next();
            shelves[i++]=shelf;
            positions.put(shelf,new Integer[positionSet.get(shelf).size()]);
            Iterator<Integer> positionIterator=positionSet.get(shelf).iterator();
            int j=0;
            while (positionIterator.hasNext()){
                positions.get(shelf)[j++]=positionIterator.next();
            }
        }

        //初始化下拉列表（左边选货架，右边选货位）
        //记录所选的货架和货位
        final String[] shelfSelected = new String[1];
        final int[] positionSelected = new int[1];
        //先初始化货架列表
        ArrayAdapter<String> shelfAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shelves);
        shelfSpinner.setAdapter(shelfAdapter);
        //再根据选择的货架初始化货位列表
        shelfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shelfSelected[0] =shelves[position];
                ArrayAdapter<Integer> positionAdapter= new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, positions.get(shelfSelected[0]));
                positionSpinner.setAdapter(positionAdapter);

                //选完货架和货位后，显示后台数据库中服装ID和数量信息，如果数量有误，可重新编辑【注意这个点击事件一定要等选完货架货位之后才注册，否则如果在一开始构造函数里面就注册的话那还没选货架，shelvesSelected是个null会报错的】
                positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                        positionSelected[0] =positions.get(shelfSelected[0])[position];
                        IdAndAmount idAndAmount= inventoryMap.get(new ShelfAndPosition(shelfSelected[0], positionSelected[0]).toString());
                        amountEdit.setText(String.valueOf(idAndAmount.amount));
                        clothesIdText.setText(idAndAmount.id);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //由于需要生成盘点报表（记录盘点前后数据对比），重新请求服务器再获取一份消耗网络资源，且受服务端不同的盘点任务分配策略影响，要保证与前一份盘点任务清单一致还需消耗服务端缓存资源，故利用原型模式，先深复制一份上次请求的盘点列表为佳，由于数组查找不方便，所以直接复制转换好的Map<String,IdAndAmount>，注意不能直接Map<String,IdAndAmount> resultMap=inventoryMap;这样是浅复制，一个内部改变另一个也会跟着变，这样就看不出差异了
        final Map<String,IdAndAmount> resultMap=new HashMap<>(inventoryMap);

        //点击“单项盘点”，检测是否需要更新后台数据
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                final int resultAmount=Integer.parseInt(amountEdit.getText().toString());
                int inventoryAmount=inventoryMap.get(new ShelfAndPosition(shelfSelected[0],positionSelected[0]).toString()).amount;
                //盘点完成（新增，以便管理人员查看进度）
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            NetworkProxy.inventoryOver(LoginActivity.getUserId(),shelfSelected[0],positionSelected[0]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                if (resultAmount!=inventoryAmount){
                    //盘点结果勘误（客户端和服务端各一份）
                    //服务端直接单项更新
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //盘点勘误
                                NetworkProxy.inventoryResultUpdate(clothesIdText.getText().toString(),resultAmount);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //客户端存一份，等会生成报表用
                    resultMap.replace(new ShelfAndPosition(shelfSelected[0],positionSelected[0]).toString(),new IdAndAmount(clothesIdText.getText().toString(),resultAmount));
                }
                //不管是否需要勘误都提示成功
                Toast.makeText(InventoryActivity.this,"校验成功",Toast.LENGTH_LONG).show();
            }
        });

        //点击“盘点完成，查看报表”，跳转页面，将盘点的所有货架、各个货架对应的货位、货架货位对应的服装ID和盘点前后的数量等信息传递到报表Activity
        inventoryOverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InventoryActivity.this,InventoryReportActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("report",new InventoryReport(inventoryKey,inventoryMap,resultMap));
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

    }

    //货位+货架
    public class ShelfAndPosition implements Serializable{
        String shelf;
        int position;
        ShelfAndPosition(String shelf, int position){
            this.shelf=shelf;
            this.position=position;
        }
        //之所以要重写toString是因为Map的key如果是个对象的话，那put(new ShelfAndPosition(),xxx)和get(new ShelfAndPosition())的两个key并不是同一个key，因为new了所以引用不一样，所以得传成String型的key
        @Override
        public String toString(){
            return new Gson().toJson(this);
        }
    }

    //服装ID+数量
    public class IdAndAmount implements Serializable{//InventoryReport里面的IdAndAmount也得实现序列化
        String id;
        int amount;
        IdAndAmount(String id,int amount){
            this.id=id;
            this.amount=amount;
        }
    }

    //用于生成报表的所有信息，实现序列化接口以便传递给InventoryReportActivity
    public class InventoryReport implements Serializable{
        ShelfAndPosition[] shelfAndPositions;
        Map<String,IdAndAmount> inventoryMap,resultMap;

        public InventoryReport(ShelfAndPosition[] shelfAndPositions,Map<String,IdAndAmount> inventoryMap,Map<String,IdAndAmount> resultMap){
            this.shelfAndPositions=shelfAndPositions;
            this.inventoryMap=inventoryMap;
            this.resultMap=resultMap;
        }
    }

}

class NetworkProxyAdapter{//用于适配queryInventoryTask的返回类型InventoryTaskDetail[]和当前Activity需要的返回类型Clothes[]
    public static Clothes[] queryInventoryTask(final String staffId) throws InterruptedException {
        final InventoryTaskDetail[][] inventoryTaskDetails=new InventoryTaskDetail[1][];
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inventoryTaskDetails[0]=NetworkProxy.queryInventoryTask(staffId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        Clothes[] inventories=new Clothes[inventoryTaskDetails[0].length];
        for (int i=0;i<inventories.length;i++){
            inventories[i]=new Clothes();
            inventories[i].setId(inventoryTaskDetails[0][i].getClothesId());
            inventories[i].setShelf(inventoryTaskDetails[0][i].getShelf());
            inventories[i].setPosition(inventoryTaskDetails[0][i].getPosition());
            inventories[i].setAmount(inventoryTaskDetails[0][i].getAmount());
        }
        return inventories;
    }
}