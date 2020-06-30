package jnu.ssc.client.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jnu.ssc.client.R;
import jnu.ssc.client.model.Clothes;
import jnu.ssc.client.model.NetworkProxy;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //获取待盘点列表
        final Clothes[][] inventories = new Clothes[1][1];
        try {
            Thread thread=new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    try {
                        inventories[0] = NetworkProxy.assignInventoryTask();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();//强行转多线程为单线程，必须让主线程等网络子线程执行完才能获取到非空的inventories[0]！！！
        } catch (Exception e) {
            e.printStackTrace();
        }

        //获取盘点任务中的所有货架和货位信息
        Set<String> shelfSet=new HashSet<>();
        Set<Integer> positionSet=new HashSet<>();
        for (int i=0;i<inventories[0].length;i++){
            shelfSet.add(inventories[0][i].getShelf());
            positionSet.add(inventories[0][i].getPosition());
        }
        String[] shelfs=new String[shelfSet.size()];
        Iterator<String> shelfIterator=shelfSet.iterator();
        int i=0;
        while (shelfIterator.hasNext()){
            shelfs[i++]=shelfIterator.next();
        }
        Integer[] positions=new Integer[positionSet.size()];
        Iterator<Integer> positionIterator=positionSet.iterator();
        int j=0;
        while (positionIterator.hasNext()){
            positions[j++]=positionIterator.next();
        }

        //初始化下拉列表（左边选货架，右边选货位）
        Spinner shelfSpinner=findViewById(R.id.spinner_shelf);
        ArrayAdapter<String> shelfAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shelfs);
        shelfSpinner.setAdapter(shelfAdapter);
        Spinner positionSpinner=findViewById(R.id.spinner_position);
        ArrayAdapter<Integer> positionAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, positions);
        positionSpinner.setAdapter(positionAdapter);

        //还差两个按钮的点击时间，记得强行用一下原型模式（也还好，毕竟为了保证两个数据一致那直接复制是挺好的）

    }

}
