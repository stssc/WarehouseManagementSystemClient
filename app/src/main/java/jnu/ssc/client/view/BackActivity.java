package jnu.ssc.client.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import jnu.ssc.client.R;
import jnu.ssc.client.controller.NetworkProxy;
import jnu.ssc.client.model.Back;

public class BackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //根据输入的订单ID和服装ID进行退货处理
        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String orderId=((EditText)findViewById(R.id.edit_order_id)).getText().toString();
                final String clothesId=((EditText)findViewById(R.id.edit_clothes_id)).getText().toString();
                @SuppressLint("HandlerLeak") final Handler handler=new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        final Back back=(Back)msg.obj;
                        final View dialogView=getLayoutInflater().inflate(R.layout.dialog_back,null);
                        TextView shelfText=dialogView.findViewById(R.id.text_shelf);
                        shelfText.setText(shelfText.getText().toString()+back.getShelf());
                        TextView positionText=dialogView.findViewById(R.id.text_position);
                        positionText.setText(positionText.getText().toString()+back.getPosition());
                        TextView amountText=dialogView.findViewById(R.id.text_amount);
                        amountText.setText(amountText.getText().toString()+back.getBackAmount());
                        new AlertDialog.Builder(BackActivity.this)
                                .setTitle("正在退货")
                                .setView(dialogView)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditText amountEdit=dialogView.findViewById(R.id.edit_amount);
                                        int amount=Integer.valueOf(amountEdit.getText().toString());
                                        if (amount==back.getBackAmount()){//鲁棒性检测，目前系统可以支持部分退货，但不支持退货订单部分入库，也就是说，用户可以买3件退2件，但工作人员不可以先拆了包裹拿1件来重新入库，等会再拿1件来重新入库，你收到退货包裹你就一次性重新入库好么别乱七八糟的
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        NetworkProxy.backOver(back.getOrderId(),back.getClothesId());
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                            Toast.makeText(BackActivity.this,"退货成功！",Toast.LENGTH_LONG).show();
                                        }
                                        else{//错误输入处理
                                            Toast.makeText(BackActivity.this,"输入数量有误，请核对后重输",Toast.LENGTH_LONG).show();
                                        }
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    }
                };
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Back back=NetworkProxy.queryBackInfo(orderId,clothesId);
                            Message msg=handler.obtainMessage();
                            msg.obj=back;
                            handler.sendMessage(msg);
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
            }
        });

    }
}
