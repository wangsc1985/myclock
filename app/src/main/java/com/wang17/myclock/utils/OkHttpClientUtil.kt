package com.wang17.myclock.utils

import android.util.Log
import com.wang17.myclock.callback.HttpCallback
import com.wang17.myclock.model.PostArgument
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * @Description
 * @ClassName OkHttpClientUtil
 * @Author
 * @Copyright
 */
object OkHttpClientUtil {
    fun postRequestByJson(url: String?, args: List<PostArgument>, callback: HttpCallback) {
        try {
            //创建OkHttpClient对象。
            val client = OkHttpClient()
            //创建表单请求体
            val JSON = MediaType.parse("application/json; charset=utf-8")
            val json = JSONObject()
            for (arg in args) {
                json.put(arg.name, arg.value)
            }
            val requestBody = RequestBody.create(JSON, json.toString())
            val request = Request.Builder().url(url)
                    .post(requestBody) //传递请求体
                    .build()

            //new call
            val call = client.newCall(request)
            //请求加入调度
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        //回调的方法执行在子线程。
                        val htmlStr = response.body()!!.string()
                        callback.excute(htmlStr)
                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun postRequest(url: String?, args: List<PostArgument>, callback: HttpCallback) {
        //创建OkHttpClient对象。
        val client = OkHttpClient()
        //创建表单请求体
        val formBody = FormBody.Builder()
        //创建Request 对象。
        for (arg in args) {
            Log.e("wangsc", "name : " + arg.name + " , value : " + arg.value)
            formBody.add(arg.name, arg.value)
        }
        val request = Request.Builder().url(url)
                .post(formBody.build()) //传递请求体
                .build()

        //new call
        val call = client.newCall(request)
        //请求加入调度
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    //回调的方法执行在子线程。
                    val htmlStr = response.body()!!.string()
                    callback.excute(htmlStr)
                }
            }
        })
    }

    fun getRequest(url: String, callback: HttpCallback) {
        //创建okHttpClient对象
        val mOkHttpClient = OkHttpClient()

        //创建一个Request
        val request = Request.Builder()
                .url(url)
                .build()
        //new call
        val call = mOkHttpClient.newCall(request)
        //请求加入调度
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    //回调的方法执行在子线程。
                    val htmlStr = response.body()!!.string()
                    callback.excute(htmlStr)
                }
            }
        })
    }
}