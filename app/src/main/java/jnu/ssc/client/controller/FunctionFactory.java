package jnu.ssc.client.controller;

import java.util.HashMap;

public class FunctionFactory {
    public static final int STAFF=1,ADMINISTRATOR=2;
    private static HashMap<String,String> map=new HashMap<>();

    public static String[] getFunctionList(int role) {
        if (role==STAFF){
            map.put("库存查询","Query");
            map.put("订单拣货","Pick");
            map.put("库存盘点","Inventory");
//            map.put("库存盘点","NewInventory");//本来想做成PickActivity那种样式的，天知道为什么每次一编辑那个弹出对话框的EditText所有按钮变灰、失效等效果就会失效！但莫名其妙PickActivity就不会这样！百思不得其解最后放弃了，真的是活活气死
            map.put("商品入库","Store");
            map.put("订单退货","Back");
            return new String[]{"库存查询", "订单拣货", "库存盘点","商品入库","订单退货"};
        }
        else if (role==ADMINISTRATOR){
            map.put("创建盘点任务","InventoryAssign");
            map.put("查看盘点进度","InventoryRate");
            return new String[]{"创建盘点任务","查看盘点进度"};
        }
        return null;
    }

    public static String getActivityName(String functionName){
        return map.get(functionName);
    }

}