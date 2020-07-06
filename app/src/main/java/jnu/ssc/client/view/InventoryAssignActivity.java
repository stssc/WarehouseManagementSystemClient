package jnu.ssc.client.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jnu.ssc.client.R;
import jnu.ssc.client.controller.NetworkProxy;
import jnu.ssc.client.model.Staff;

public class InventoryAssignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_assign);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //添加参与本次盘点任务的职工
        final List<Staff> staffList=new CopyOnWriteArrayList<>();
        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据输入的职工号查询职工姓名
                final String staffId=((EditText)findViewById(R.id.edit_staff_id)).getText().toString();
                final String[] staffName = new String[1];
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Staff staff=NetworkProxy.queryStaff(staffId);
                            staffName[0] =staff.getName();
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
                //弹出对话框确认职工姓名是否正确，是则将已分配任务的职工信息显示在文本框中
                final TextView staffText=findViewById(R.id.text_staff);
                new AlertDialog.Builder(InventoryAssignActivity.this)
                        .setTitle("确定要给"+staffName[0]+"分配盘点任务吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Staff staff=new Staff();
                                staff.setId(staffId);
                                staff.setName(staffName[0]);
                                staffList.add(staff);
                                staffText.setText(staffText.getText()+staffId+"\t"+staffName[0]+"\n");
                            }
                        })
                        .create().show();
            }
        });

        //服务端生成并自动分配盘点任务给指定的职工，并跳转页面查看已分配任务
        findViewById(R.id.button_assign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            NetworkProxy.assignInventoryTask(staffList.toArray(new Staff[0]));
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
                Intent intent=new Intent(InventoryAssignActivity.this,InventoryRateActivity.class);
                startActivity(intent);
            }
        });
    }
}
