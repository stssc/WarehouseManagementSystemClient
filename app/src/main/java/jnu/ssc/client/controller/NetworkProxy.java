package jnu.ssc.client.controller;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import jnu.ssc.client.model.Back;
import jnu.ssc.client.model.Clothes;
import jnu.ssc.client.model.InventoryTask;
import jnu.ssc.client.model.InventoryTaskDetail;
import jnu.ssc.client.model.Pick;
import jnu.ssc.client.model.Space;
import jnu.ssc.client.model.Staff;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkProxy {
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Gson gson = new Gson();
    private static String address="http://192.168.31.186:8080";

    /*
    工作人员请求区
     */

    //登录
    public static boolean staffLogin(String id,String password) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("id",id)
                .add("password",password)
                .build();
        Request request=new Request.Builder()
                .url(address+"/staff/login")
                .post(requestBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return Boolean.valueOf(Objects.requireNonNull(response.body()).string());
    }

    //库存查询
    public static Clothes queryClothesInfo(String id) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .build();
        Request request = new Request.Builder()
                .url(address +"/staff/query")
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(), Clothes.class);
    }

    //订单拣货-分配拣货任务
    public static Pick[] assignPickTask() throws IOException {
        Request request = new Request.Builder()
                .url(address+"/staff/pick_task")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return gson.fromJson(response.body().string(), Pick[].class);
    }

    //订单拣货-拣货完成
    public static void pickOver(String orderId, String clothesId) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("orderId", orderId)
                .add("clothesId", clothesId)
                .build();
        Request request = new Request.Builder()
                .url(address+"/staff/pick_over")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).execute();
    }

    //库存盘点-查看盘点任务
    public static InventoryTaskDetail[] queryInventoryTask(String staffId) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("staffId",staffId)
                .build();
        Request request = new Request.Builder()
                .url(address+"/staff/inventory_task")
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(), InventoryTaskDetail[].class);
    }

    //库存盘点-盘点勘误
    public static void inventoryResultUpdate(String id, int amount) throws IOException {
//        Map<String,String> shelfMap=new HashMap<>();
//        shelfMap.put("shelf",shelf);
//        Map<String,Integer> positionMap=new HashMap<>();
//        positionMap.put("position",position);
//        Object[] requestParam=new Object[2];
//        requestParam[0]=shelfMap;
//        requestParam[1]=positionMap;
//        String requestParamJson=gson.toJson(requestParam);
//        RequestBody requestBody=RequestBody.create(requestParamJson,MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(address+"/staff/inventory_errata?id=" + id + "&amount=" + amount)
//                .post(requestBody)
                .build();
        okHttpClient.newCall(request).execute();
    }

    //库存盘点-盘点完成
    public static void inventoryOver(String staffId, String shelf, int position) throws IOException {
        Request request = new Request.Builder()
                .url(address+"/staff/inventory_over?staffId=" + staffId + "&shelf=" + shelf + "&position=" + position)
                .build();
        okHttpClient.newCall(request).execute();
    }

    //商品入库-查询空货位
    public static Space getAnEmptySpace() throws IOException {
        Request request=new Request.Builder()
                .url(address+"/staff/store_get")
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(),Space.class);
    }

    //商品入库-入库完成
    public static void storeANewClothes(String id,String shelf,int position,int amount) throws IOException {
        Request request=new Request.Builder()
                .url(address+"/staff/store_set?id="+id+"&shelf="+shelf+"&position="+position+"&amount="+amount)
                .build();
        okHttpClient.newCall(request).execute();
    }

    //订单退货-查询退货信息
    public static Back queryBackInfo(String orderId,String clothesId) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("orderId",orderId)
                .add("clothesId",clothesId)
                .build();
        Request request=new Request.Builder()
                .url(address+"/staff/back_get")
                .post(requestBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(),Back.class);
    }

    //订单退货-退货完成
    public static void backOver(String orderId,String clothesId) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("orderId",orderId)
                .add("clothesId",clothesId)
                .build();
        Request request=new Request.Builder()
                .url(address+"/staff/back_set")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).execute();
    }

    /*
    管理人员请求区
     */

    //登录
    public static boolean administratorLogin(String id,String password) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("id",id)
                .add("password",password)
                .build();
        Request request=new Request.Builder()
                .url(address+"/administrator/login")
                .post(requestBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return Boolean.valueOf(Objects.requireNonNull(response.body()).string());
    }

    //盘点管理-查询职工信息
    public static Staff queryStaff(String id) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("id",id)
                .build();
        Request request=new Request.Builder()
                .url(address+"/administrator/query_staff")
                .post(requestBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(),Staff.class);
    }

    //盘点管理-分配盘点任务
    public static void assignInventoryTask(Staff[] staffs) throws IOException {
        String staffsStr=gson.toJson(staffs);
        RequestBody requestBody=new FormBody.Builder()
                .add("staffs",staffsStr)
                .build();
        Request request=new Request.Builder()
                .url(address+"/administrator/inventory_assign")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).execute();
    }

    //盘点管理-查看已分配盘点任务的员工
    public static String[] queryInventoryStaff() throws IOException {
        Request request=new Request.Builder()
                .url(address+"/administrator/inventory_staff")
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(),String[].class);
    }

    //盘点管理-查看盘点任务摘要
    public static InventoryTask[] queryInventoryTaskSummary(String staffId) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("staffId",staffId)
                .build();
        Request request=new Request.Builder()
                .url(address+"/administrator/inventory_summary")
                .post(requestBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return gson.fromJson(response.body().string(),InventoryTask[].class);
    }

    //盘点管理-查看预计盘点数量
    public static int queryInventoryAmount(String staffId) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("staffId",staffId)
                .build();
        Request request=new Request.Builder()
                .url(address+"/administrator/inventory_amount")
                .post(requestBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return Integer.parseInt(Objects.requireNonNull(response.body()).string());
    }

    //盘点管理-查看盘点进度
    public static double queryInventoryRate(String staffId) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("staffId",staffId)
                .build();
        Request request=new Request.Builder()
                .url(address+"/administrator/inventory_rate")
                .post(requestBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return Double.valueOf(Objects.requireNonNull(response.body()).string());
    }

}