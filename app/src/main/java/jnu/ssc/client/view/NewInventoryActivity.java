package jnu.ssc.client.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

import jnu.ssc.client.R;
import jnu.ssc.client.controller.NetworkProxy;
import jnu.ssc.client.model.InventoryTask;
import jnu.ssc.client.model.InventoryTaskDetail;
import jnu.ssc.client.model.Pick;

public class NewInventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_inventory);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //获取盘点任务列表
        final InventoryTaskDetail[][] inventoryTasks=new InventoryTaskDetail[1][];
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inventoryTasks[0]= NetworkProxy.queryInventoryTask(LoginActivity.getUserId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //显示盘点任务列表
        ListAdapter adapter=new ListAdapter(NewInventoryActivity.this,R.layout.inventory_item,inventoryTasks[0]);
        ListView inventoryList=findViewById(R.id.inventory_list);
        inventoryList.setAdapter(adapter);

    }

    private class ListAdapter extends ArrayAdapter<InventoryTaskDetail> {
        private int resourceId;

        ListAdapter(Context context, int resource, InventoryTaskDetail[] objects) {//将指定的数据对象以规定布局的形式加载到当前视图中
            //Context:当前上下文；resource:实例化视图时使用的布局文件的资源id；objects:要在ListView中展示的数据对象
            super(context, resource, objects);
            resourceId=resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent){//ListView每个子项滚动到屏幕内时调用，获取一个在数据集中指定位置position显示数据的视图
            final InventoryTaskDetail inventoryTaskDetail=getItem(position);//获得当前子项的实例
            final View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);//用实例化视图时使用的布局文件的资源list_item为子项加载布局，false表示不为这个view添加父布局
            if (inventoryTaskDetail!=null){
                //显示盘点信息
                ((TextView)view.findViewById(R.id.inventory_clothes_id)).setText("服装ID："+inventoryTaskDetail.getClothesId());
                ((TextView)view.findViewById(R.id.inventory_clothes_info)).setText("货架："+inventoryTaskDetail.getShelf()+"\t货位："+inventoryTaskDetail.getPosition()+"\t数量："+inventoryTaskDetail.getAmount());
                final ToggleButton inventoryButton=view.findViewById(R.id.button_inventory1);
                //盘点按钮点击事件
                inventoryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editAmount = new EditText(NewInventoryActivity.this);
                        editAmount.setText(String.valueOf(inventoryTaskDetail.getAmount()));
                        editAmount.setGravity(Gravity.CENTER);//居中显示
                        new AlertDialog.Builder(NewInventoryActivity.this)
                                .setTitle("正在盘点")
                                .setView(editAmount)//这样有个缺点，就是EditText会顶着对话框的两边，因为直接把EditText作为整个View然后setView了
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //盘点按钮变灰，以后再点就没反应了
                                        inventoryButton.setBackgroundColor(R.color.picked_button);
                                        inventoryButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //do nothing
                                            }
                                        });
                                        //盘点勘误及页面更新
                                        if (inventoryTaskDetail.getAmount()!=Integer.valueOf(editAmount.getText().toString())){
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        NetworkProxy.inventoryResultUpdate(inventoryTaskDetail.getClothesId(),Integer.valueOf(editAmount.getText().toString()));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                            ((TextView)view.findViewById(R.id.inventory_clothes_info)).setText("货架："+inventoryTaskDetail.getShelf()+"\t货位："+inventoryTaskDetail.getPosition()+"\t数量："+editAmount.getText().toString());
                                        }
                                        //盘点完成
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    NetworkProxy.inventoryOver(LoginActivity.getUserId(),inventoryTaskDetail.getShelf(),inventoryTaskDetail.getPosition());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                }).create().show();
                    }
                });
            }
            return view;//最后返回这个加载好数据的布局
        }

    }

}
