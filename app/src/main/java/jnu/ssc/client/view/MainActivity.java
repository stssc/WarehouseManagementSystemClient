package jnu.ssc.client.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import jnu.ssc.client.R;

public class MainActivity extends AppCompatActivity {
    private String[] names = FunctionFactory.getFunctionList(FunctionFactory.STAFF);//获取工作人员功能列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //显示工作人员功能列表
        ListAdapter adapter = new ListAdapter(
                MainActivity.this, R.layout.function_item, names);
        ListView functionList=findViewById(R.id.function_list);
        functionList.setAdapter(adapter);
        functionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name=names[position];
                try {
                    String packageName = getPackageName()+".view.";
                    Intent intent=new Intent(MainActivity.this, Class.forName(packageName+FunctionFactory.getActivityName(name)+"Activity"));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class ListAdapter extends ArrayAdapter<String> {
        private int resourceId;

        ListAdapter(Context context, int resource, String[] objects) {//将指定的数据对象以规定布局的形式加载到当前视图中
            //Context:当前上下文；resource:实例化视图时使用的布局文件的资源id；objects:要在ListView中展示的数据对象
            super(context, resource, objects);
            resourceId=resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent){//ListView每个子项滚动到屏幕内时调用，获取一个在数据集中指定位置position显示数据的视图
            String name=getItem(position);//获得当前子项的String实例
            View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);//用实例化视图时使用的布局文件的资源list_item为子项加载布局，false表示不为这个view添加父布局
            TextView functionName=view.findViewById(R.id.function_name);//获取list_item布局资源中的text_view
            functionName.setText(name);//将当前子项的实例的文字资源加载进布局资源中的TextView
            return view;//最后返回这个加载好数据的布局
        }

    }


}
