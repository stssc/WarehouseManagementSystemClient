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
        ListAdapter adapter = new ListAdapter(
                PickActivity.this, R.layout.pick_item, picks[0]);
        ListView pickList=findViewById(R.id.pick_list);
        pickList.setAdapter(adapter);

    }

    private class ListAdapter extends ArrayAdapter<Pick> {
        private int resourceId;

        ListAdapter(Context context, int resource, Pick[] objects) {//将指定的数据对象以规定布局的形式加载到当前视图中
            //Context:当前上下文；resource:实例化视图时使用的布局文件的资源id；objects:要在ListView中展示的数据对象
            super(context, resource, objects);
            resourceId=resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent){//ListView每个子项滚动到屏幕内时调用，获取一个在数据集中指定位置position显示数据的视图
            final Pick pick=getItem(position);//获得当前子项的Pick实例
            View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);//用实例化视图时使用的布局文件的资源list_item为子项加载布局，false表示不为这个view添加父布局
            if (pick!=null){
                //显示拣货信息
                ((TextView)view.findViewById(R.id.pick_order_id)).setText("订单号："+pick.getOrderId());
                ((TextView)view.findViewById(R.id.pick_clothes_id)).setText("服装ID："+pick.getClothesId());
                ((TextView)view.findViewById(R.id.pick_clothes_amount)).setText("待拣货数量："+pick.getAmount());
                ((TextView)view.findViewById(R.id.pick_clothes_position)).setText("货架："+pick.getShelf()+"\t货位："+pick.getPosition());
                ToggleButton pickButton=view.findViewById(R.id.button_pick);
                //拣货按钮点击事件
                pickButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editClothesId = new EditText(PickActivity.this);
                        editClothesId.setHint("请输入服装ID");
                        editClothesId.setGravity(Gravity.CENTER);//居中显示
                        new AlertDialog.Builder(PickActivity.this)
                                .setTitle("正在拣货")
                                .setView(editClothesId)//这样有个缺点，就是EditText会顶着对话框的两边，因为直接把EditText作为整个View然后setView了
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInt