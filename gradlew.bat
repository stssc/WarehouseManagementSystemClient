package jnu.ssc.client.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

import jnu.ssc.client.R;
import jnu.ssc.client.model.NetworkCommunication;
import jnu.ssc.client.model.Pick;

public class PickActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //获取待拣货列表
        final Pick[][] picks = new Pick[1][1];
        try {
            Thread thread=new Thread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    try {
                        picks[0] = NetworkCommunication.assignPickTask();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();//强制转多线程为单线程，必须让主线程等网络子线程执行完才能获取到非空的picks[0]！！！
        } catch (Exception e) {
            e.printStackTrace();
        }
        //显示待拣货列表
        ListAdapter adapter = new ListAdap