package jnu.ssc.client.view;

import java.util.HashMap;

class FunctionFactory {
    static final int STAFF=1;
    private static HashMap<String,String> map=new HashMap<>();

    static String[] getFunctionList(int role) {
        if (role==STAFF){
            map.put("库存查询","Query");
            map.put("订单拣货","Pick");
            map.put("库存盘点","Inventory");
            return new String[]{"库存查询", "订单拣货", "库存盘点"};
        }
        return null;
    }

    static String getActivityName(String functionName){
        return map.get(functionName);
    }

}