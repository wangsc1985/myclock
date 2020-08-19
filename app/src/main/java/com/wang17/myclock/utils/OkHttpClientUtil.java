package com.wang17.myclock.utils;


import android.util.Log;

import androidx.annotation.NonNull;

import com.wang17.myclock.callback.HttpCallback;
import com.wang17.myclock.model.PostArgument;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Description
 * @ClassName OkHttpClientUtil
 * @Author
 * @Copyright
 */
@SuppressWarnings("all")
public class OkHttpClientUtil {

    public static void postRequestByJson(String url, List<PostArgument> args, final HttpCallback callback) {
        try {
        //创建OkHttpClient对象。
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        for (PostArgument arg:args) {
            json.put(arg.name,arg.value);
        }

        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
        Request request = new Request.Builder().url(url)
                .post(requestBody)//传递请求体
                .build();

        //new call
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //回调的方法执行在子线程。
                    String htmlStr = response.body().string();
                    callback.excute(htmlStr);
                }
            }
        });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void postRequest(String url, List<PostArgument> args, final HttpCallback callback) {
        //创建OkHttpClient对象。
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体

        FormBody.Builder formBody = new FormBody.Builder();
        //创建Request 对象。
        for(PostArgument arg : args){
            Log.e("wangsc","name : "+arg.name+" , value : "+arg.value);
            formBody.add(arg.name,arg.value);
        }
        Request request = new Request.Builder().url(url)
                .post(formBody.build())//传递请求体
                .build();

        //new call
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //回调的方法执行在子线程。
                    String htmlStr = response.body().string();
                    callback.excute(htmlStr);
                }
            }
        });
    }

    public static void getRequest(String url, final HttpCallback callback) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        //创建一个Request
        final Request request = new Request.Builder()
                .url(url)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //回调的方法执行在子线程。
                    String htmlStr = response.body().string();
                    callback.excute(htmlStr);
                }
            }
        });
    }

}