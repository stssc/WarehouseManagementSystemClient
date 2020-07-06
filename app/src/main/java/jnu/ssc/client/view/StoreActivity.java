package jnu.ssc.client.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import jnu.ssc.client.R;
import jnu.ssc.client.controller.NetworkProxy;
import jnu.ssc.client.model.Space;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        @SuppressLint("HandlerLeak") final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                Space space=(Space)msg.obj;
                ((TextView)findViewById(R.id.text_result)).setText("入库成功！\n货架："+space.getShelf()+"\n货位："+space.getPosition());
            }
        };

        //新品入库
        findViewById(R.id.button_store).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Space space=NetworkProxy.getAnEmptySpace();
                            Message msg=handler.obtainMessage();
                            msg.obj=space;
                            handler.sendMessage(msg);
                            String clotheId=((EditText)findViewById(R.id.text_amount)).getText().toString();
                            int amount=Integer.valueOf(((EditText)findViewById(R.id.edit_amount)).getText().toString());
                            NetworkProxy.storeANewClothes(clotheId,space.getShelf(),space.getPosition(),amount);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }
}
