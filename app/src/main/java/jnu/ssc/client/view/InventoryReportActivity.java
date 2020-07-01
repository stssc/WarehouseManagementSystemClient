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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;

import jnu.ssc.client.R;

public class InventoryReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);

        //隐藏标题栏
        ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.hide();
        }

        //获取盘点记录数据
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        assert bundle != null;
        final InventoryActivity.InventoryReport inventoryReport=(InventoryActivity.InventoryReport)bundle.getSerializable("report");
        assert inventoryReport != null;

        //初始化盘点记录列表
        ListAdapter adapter=new ListAdapter(InventoryReportActivity.this,R.layout.report_item,new Object[inventoryReport.shelfAndPositions.length],inventoryReport.shelfAndPositions,inventoryReport.inventoryMap,inventoryReport.resultMap);
        ListView reportList=findViewById(R.id.report_list);
        reportList.setAdapter(adapter);

    }

    private class ListAdapter extends ArrayAdapter<Object> {
        private int resourceId;
        private InventoryActivity.ShelfAndPosition[] shelfAndPositions;
        private Map<String, InventoryActivity.IdAndAmount> inventoryMap,resultMap;

        ListAdapter(Context context, int resource, Object[] chickenRibs,InventoryActivity.ShelfAndPosition[] shelfAndPositions,Map<String, InventoryActivity.IdAndAmount> inventoryMap,Map<String, InventoryActivity.IdAndAmount> resultMap) {//将指定的数据对象以规定布局的形式加载到当前视图中
            //Context:当前上下文；resource:实例化视图时使用的布局文件的资源id；chickenRibs顾名思义就是个鸡肋:)，只是用来告诉adapter我有几个list_item而已，没办法，谁让ListView垃圾的内部“优化”导致屏幕外的数据都不加载进来呢？一开始不加载，一滚动就变成空了:)，我还不如手动赋值，懒得用你的API了，有一说一，Android有些API是真的垃圾，越优化越垃圾，还不如程序员手动优化
            super(context, resource, chickenRibs);
            resourceId=resource;
            this.shelfAndPositions=shelfAndPositions;
            this.inventoryMap=inventoryMap;
            this.resultMap=resultMap;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent){//ListView每个子项滚动到屏幕内时调用，获取一个在数据集中指定位置position显示数据的视图
            InventoryActivity.ShelfAndPosition shelfAndPosition=shelfAndPositions[position];//获得当前子项的实例
            View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);//用实例化视图时使用的布局文件的资源list_item为子项加载布局，false表示不为这个view添加父布局
            TextView idText=view.findViewById(R.id.report_clothes_id);
            TextView infoText=view.findViewById(R.id.report_clothes_info);
            TextView differenceText=view.findViewById(R.id.difference_show);
            if (shelfAndPosition!=null){
                //显示服装ID
                idText.setText("服装ID："+inventoryMap.get(shelfAndPosition.toString()).id);
                //显示位置和盘点后的数量
                infoText.setText("货架："+shelfAndPosition.shelf+"\t货位："+shelfAndPosition.position+"\t盘点数量："+resultMap.get(shelfAndPosition.toString()).amount);
                //标记盘盈或盘亏
                if (resultMap.get(shelfAndPosition.toString()).amount>inventoryMap.get(shelfAndPosition.toString()).amount){
                    differenceText.setText("盘盈");
                    differenceText.setBackgroundColor(getColor(R.color.profit));
                }
                else if (resultMap.get(shelfAndPosition.toString()).amount<inventoryMap.get(shelfAndPosition.toString()).amount){
                    differenceText.setText("盘亏");
                    differenceText.setBackgroundColor(getColor(R.color.loss));
                }
            }
            return view;//最后返回这个加载好数据的布局
        }

    }

}
