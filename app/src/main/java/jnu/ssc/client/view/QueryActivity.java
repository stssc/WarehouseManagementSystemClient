package jnu.ssc.client.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import jnu.ssc.client.R;
import jnu.ssc.client.model.Clothes;
import jnu.ssc.client.model.NetworkProxy;

public class QueryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //显示库存查询结果
        @SuppressLint("HandlerLeak") final Handler handler=new Handler(){
            @Override
            public void handleMessage(@NotNull Message msg){
                Clothes clothes=(Clothes)msg.obj;
                if (clothes!=null){
                    String text="货架："+clothes.getShelf()+"\n货位："+clothes.getPosition()+"\n数量："+clothes.getAmount();
                    ((TextView)findViewById(R.id.text_query)).setText(text);
                }
            }
        };

        //库存查询
        findViewById(R.id.button_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        try {
                            String clothesId=((EditText)findViewById(R.id.edit_query)).getText().toString();
                            Clothes clothes= NetworkProxy.queryClothesInfo(clothesId);
                            Message msg=handler.obtainMessage();
                            msg.obj=clothes;
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
