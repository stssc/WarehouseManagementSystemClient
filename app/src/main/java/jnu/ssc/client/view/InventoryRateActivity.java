package jnu.ssc.client.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import jnu.ssc.client.R;
import jnu.ssc.client.controller.NetworkProxy;
import jnu.ssc.client.model.InventoryTask;
import jnu.ssc.client.model.InventoryTaskDetail;
import jnu.ssc.client.model.Staff;

public class InventoryRateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_rate);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //查询被分配到任务的员工的盘点任务、预计盘点数量和盘点进度百分比
        final HashMap<String,Double> staffInventoryRate=new HashMap<>();
        final HashMap<String,Integer> staffInventoryAmount=new HashMap<>();
        final HashMap<String,InventoryTask[]> staffInventoryTask=new HashMap<>();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] staffs= NetworkProxy.queryInventoryStaff();
                    for (String staff:staffs){
                        InventoryTask[] inventoryTask=NetworkProxy.queryInventoryTaskSummary(staff);
                        staffInventoryTask.put(staff,inventoryTask);
                        int inventoryAmount=NetworkProxy.queryInventoryAmount(staff);
                        staffInventoryAmount.put(staff,inventoryAmount);
                        double inventoryRate=NetworkProxy.queryInventoryRate(staff);
                        staffInventoryRate.put(staff,inventoryRate);
                    }
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

        //显示
        StringBuilder showText= new StringBuilder();
        Set<String> staffs=staffInventoryRate.keySet();
        for (String staffId : staffs) {
            showText.append("职工号：").append(staffId).append("\n");
            showText.append("负责盘点范围：").append(staffInventoryTask.get(staffId)[0].getShelf()).append("货架").append(staffInventoryTask.get(staffId)[0].getPosition()).append("货位 到 ").append(staffInventoryTask.get(staffId)[1].getShelf()).append("货架").append(staffInventoryTask.get(staffId)[1].getPosition()).append("货位\n");
            showText.append("预计盘点数量：").append(staffInventoryAmount.get(staffId)).append("\n");
            showText.append("当前盘点进度：").append(staffInventoryRate.get(staffId)*100).append("%\n\n");
        }
        ((TextView)findViewById(R.id.process_show)).setText(showText);
    }
}
