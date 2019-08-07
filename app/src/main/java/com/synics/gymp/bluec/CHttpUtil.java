package com.synics.gymp.bluec;

import android.util.Log;

import com.synics.gymp.iface.IDataLoadCallback;
import com.synics.gymp.iface.IHttpUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2016/10/27.
 */

public class CHttpUtil {
    private static IDataLoadCallback callView = null;
    private static int requestCode = 0;

    public static void Post(String url, String[] paramVal, IDataLoadCallback pcallView, int prequestCode) {
        callView = pcallView;
        requestCode = prequestCode;
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient client = httpBuilder.readTimeout(3, TimeUnit.MINUTES)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES) //设置超时
                .build();

        //OkHttpClient.connectTimeout
        Retrofit retrofit = new Retrofit.Builder()
                //这里建议：- Base URL: 总是以/结尾；- @Url: 不要以/开头
                .baseUrl("http://" + CConfig.SERVER_URL + CConfig.SERVER_APP)
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Oservable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        IHttpUtil httpUtil = retrofit.create(IHttpUtil.class);

        Map<String, String> vparams = new HashMap<>();
        if(paramVal!=null) {
            for (int i = 0; i < paramVal.length; i += 2) {
                vparams.put(paramVal[i], paramVal[i + 1]);
            }
        }
        if(!vparams.containsKey("osid"))
            vparams.put("osid", CGlobalData.osid + "");
        if(!vparams.containsKey("orgid"))
            vparams.put("orgid", CGlobalData.orgid);
        if(!vparams.containsKey("ssid"))
            vparams.put("ssid", CGlobalData.ssid + "");
        if(!vparams.containsKey("storeid"))
            vparams.put("storeid", CGlobalData.storeid);
        if(!vparams.containsKey("csid"))
            vparams.put("csid", CGlobalData.csid + "");
        if(!vparams.containsKey("counterid"))
            vparams.put("counterid", CGlobalData.counterid);
        if(!vparams.containsKey("mposid"))
            vparams.put("mposid", CConfig.SMPOSID);
        if(!vparams.containsKey("sid"))
            vparams.put("sid", CGlobalData.sid + "");
        if(!vparams.containsKey("cashierid"))
            vparams.put("cashierid", CGlobalData.cashierid);
        if(!vparams.containsKey("cashier"))
            vparams.put("cashier", CGlobalData.cashier);
        if(!vparams.containsKey("supplyid"))
            vparams.put("supplyid", CGlobalData.supplyid);
        if(!vparams.containsKey("erpcashid"))
            vparams.put("erpcashid", CGlobalData.erpcashid);

        Call<String> call = httpUtil.post(url, vparams);
        Log.e("superren", "url=" + url + vparams.toString());

        try {
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        //Log.i("wxl", "onResponse=" + response.body().toString());
                        //System.out.println(response.body().toString());
                        String msg = (response.body() == null) ? response.errorBody().toString() : response.body().toString().trim();

                        callView.dataLoadCallback(true, requestCode, (response.body() == null) ? -1 : 0, msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String msg = e.getMessage();
                        if (msg == null) msg = "无返回数据。";
                        callView.dataLoadCallback(true, requestCode, -1, msg);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i("wxl", "onFailure=" + t.getMessage());
                    String msg = t.getMessage();
                    if (msg == null) msg = "无返回数据。";
                    callView.dataLoadCallback(true, requestCode, -1, msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg == null) msg = "无返回数据。";
            callView.dataLoadCallback(true, requestCode, -1, msg);
        }
    }

    public static void PostJson(String url, String paramStr, IDataLoadCallback pcallView, int prequestCode) {
        callView = pcallView;
        requestCode = prequestCode;

        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient client = httpBuilder.readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES) //设置超时
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                //这里建议：- Base URL: 总是以/结尾；- @Url: 不要以/开头
                .baseUrl("http://" + CConfig.SERVER_URL + CConfig.SERVER_APP)
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Oservable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        IHttpUtil httpUtil = retrofit.create(IHttpUtil.class);

        RequestBody body =
                RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"),
                        paramStr);

        Call<String> call = httpUtil.post(url, body);

        try {
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.body() == null) {
                            Log.i("wxl", "异常返回数据=" + response.body().toString());
                            callView.dataLoadCallback(true, requestCode, -1, response.errorBody().toString());
                        } else {
                            String msg = response.body().toString().trim();
                            Log.i("wxl", "成功返回数据=" + msg);
                            callView.dataLoadCallback(true, requestCode, 0, msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String msg = e.getMessage();
                        if (msg == null) msg = "无返回数据。";
                        callView.dataLoadCallback(true, requestCode, -1, msg);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.i("wxl", "失败返回数据=" + t.getMessage());
                    String msg = t.getMessage();
                    if (msg == null) msg = "无返回数据。";
                    callView.dataLoadCallback(true, requestCode, -1, msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg == null) msg = "无返回数据。";
            callView.dataLoadCallback(true, requestCode, -1, msg);
        }
    }


}
