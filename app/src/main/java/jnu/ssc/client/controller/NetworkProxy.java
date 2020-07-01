package jnu.ssc.client.controller;

import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jnu.ssc.client.R;
import jnu.ssc.client.model.Clothes;
import jnu.ssc.client.model.Pick;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkProxy {
    private static OkHttpClient okHttpClient=new OkHttpClient();
    private static Gson gson=new Gson();

    //库存查询
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Clothes queryClothesInfo(String id) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("id",id)
                .build();
        Request request=new Request.Builder()
                .url("http://192.168.31.186:8080/staff/query")
                .post(requestBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(),Clothes.class);
    }

    //订单拣货
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Pick[] assignPickTask() throws IOException {
        Request request=new Request.Builder()
                .url("http://192.168.31.186:8080/staff/pick_task")
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return gson.fromJson(response.body().string(),Pick[].class);
    }

    public static void pickOver(String orderId,String clothesId) throws IOException {
        RequestBody requestBody=new FormBody.Builder()
                .add("orderId",orderId)
                .add("clothesId",clothesId)
                .build();
        Request request=new Request.Builder()
                .url("http://192.168.31.186:8080/staff/pick_over")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).execute();
    }

    //库存盘点
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Clothes[] assignInventoryTask() throws IOException {
        Request request=new Request.Builder()
                .url("http://192.168.31.186:8080/staff/inventory_task")
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return gson.fromJson(Objects.requireNonNull(response.body()).string(),Clothes[].class);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        Request request=new Request.Builder()
                .url("http://192.168.31.186:8080/staff/inventory_errata?id="+id+"&amount="+amount)
//                .post(requestBody)
                .build();
        okHttpClient.newCall(request).execute();
    }

}