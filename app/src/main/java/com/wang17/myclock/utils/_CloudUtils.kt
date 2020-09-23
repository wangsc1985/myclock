package com.wang17.myclock.utils

import android.R.string
import android.content.Context
import android.util.Log
import com.wang17.myclock.callback.CloudCallback
import com.wang17.myclock.callback.HttpCallback
import com.wang17.myclock.database.Position
import com.wang17.myclock.database.Setting.KEYS
import com.wang17.myclock.database.utils.DataContext
import com.wang17.myclock.model.DateTime
import com.wang17.myclock.model.PostArgument
import org.json.JSONArray
import java.util.*


object _CloudUtils {
    private const val _TAG = "wangsc"
    private var newMsgCount = 0
    fun getNewMsg(context: Context?, callback: CloudCallback) {
        newMsgCount = 0

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", object : HttpCallback {
            override fun excute(html: String) {
                try {
                    val dataContext = DataContext(context)
                    val accessToken = _JsonUtils.getValueByKey(html, "access_token")
                            ?: throw Exception("获取access token失败。")

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=yipinshangdu-4wk7z&name=getNewMsg"
                    val args: MutableList<PostArgument> = ArrayList()
                    args.add(PostArgument("pwd", dataContext.getSetting(KEYS.wx_request_code, "0000").value))
                    OkHttpClientUtil.postRequestByJson(url, args, object : HttpCallback {
                        override fun excute(html: String) {
                            try {
//                                e(html);
                                val resp_data: Any? = _JsonUtils.getValueByKey(html, "resp_data")
                                //                                e(resp_data);
                                if (_JsonUtils.isContainsKey(resp_data.toString(), "data")) {
                                    val data = _JsonUtils.getValueByKey(resp_data.toString(), "data")

//                                    e(data);
                                    val jsonArray = JSONArray(data)
                                    var time: DateTime? = null
                                    for (i in jsonArray.length() - 1 downTo 0) {
                                        val jsonObject = jsonArray.getString(i)
                                        val sendTimeTS = _JsonUtils.getValueByKey(jsonObject, "sendTimeTS").toLong()
                                        val sendTime = _JsonUtils.getValueByKey(jsonObject, "sendTime")
                                        e(sendTime)
                                        time = DateTime(sendTimeTS)
                                    }
                                    if (jsonArray.length() == 0) {
                                        callback.excute(0, "")
                                    } else {
                                        callback.excute(1, time!!.toOffset2() + "\t" + jsonArray.length())
                                    }
                                } else if (_JsonUtils.isContainsKey(resp_data.toString(), "msg")) {
                                }
                            } catch (e: Exception) {
                                callback.excute(-2, e.message!!)
                            }
                        }
                    })
                } catch (e: Exception) {
                    callback.excute(-1, e.message!!)
                }
            }
        })
    }

    fun getPositions(pwd: String, callback: CloudCallback) {
        val result:MutableList<Position>  = ArrayList()
        newMsgCount = 0

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", object : HttpCallback {
            override fun excute(html: String) {
                try {
                    val accessToken = _JsonUtils.getValueByKey(html, "access_token")
                            ?: throw Exception("获取access token失败。")

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=yipinshangdu-4wk7z&name=getPositions"
                    val args: MutableList<PostArgument> = ArrayList()
                    args.add(PostArgument("pwd", pwd))
                    OkHttpClientUtil.postRequestByJson(url, args, object : HttpCallback {
                        override fun excute(html: String) {
                            try {
                                val resp_data: Any? = _JsonUtils.getValueByKey(html, "resp_data")
                                val data = _JsonUtils.getValueByKey(resp_data.toString(), "data")
                                val jsonArray = JSONArray(data)
                                for (i in 0 until jsonArray.length()) {

                                    val jsonObject = jsonArray.getString(i)

                                    val position = Position()
                                    position.name = _JsonUtils.getValueByKey(jsonObject, "name")
                                    position.code = _JsonUtils.getValueByKey(jsonObject,"code")
                                    position.amount = _JsonUtils.getValueByKey(jsonObject,"amount").toInt()
                                    position.cost = _JsonUtils.getValueByKey(jsonObject,"cost").toDouble()

                                    if (position.amount > 0) result.add(position)
                                }
                                callback.excute(0,result)
                            } catch (e: Exception) {
                                callback.excute(-2, e.message?:"")
                            }
                        }
                    })
                } catch (e: Exception) {
                    callback.excute(-1, e.message?:"")
                }
            }
        })
    }

    fun getUser(pwd: String, callback: CloudCallback) {
        newMsgCount = 0

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", object : HttpCallback {
            override fun excute(html: String) {
                try {
                    val accessToken = _JsonUtils.getValueByKey(html, "access_token")
                            ?: throw Exception("获取access token失败。")

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=yipinshangdu-4wk7z&name=getUser"
                    val args: MutableList<PostArgument> = ArrayList()
                    args.add(PostArgument("pwd", pwd))
                    OkHttpClientUtil.postRequestByJson(url, args, object : HttpCallback {
                        override fun excute(html: String) {
                            try {
                                val resp_data: Any? = _JsonUtils.getValueByKey(html, "resp_data")
                                val data = _JsonUtils.getValueByKey(resp_data.toString(), "data").toString()
                                val jsonArray = JSONArray(data)
                                if (jsonArray.length() > 0) {
                                    val jsonObject = jsonArray.getString(0)
                                    val name = _JsonUtils.getValueByKey(jsonObject, "name").toString()
                                    callback.excute(0, name)
                                } else {
                                    callback.excute(1, "访问码有误")
                                }
                            } catch (e: Exception) {
                                callback.excute(-2, e.message!!)
                            }
                        }
                    })
                } catch (e: Exception) {
                    callback?.excute(-1, e.message!!)
                }
            }
        })
    }

    fun getSetting(pwd: String, name: String, callback: CloudCallback) {
        newMsgCount = 0


        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", object : HttpCallback {
            override fun excute(html: String) {
                try {
                    val accessToken = _JsonUtils.getValueByKey(html, "access_token")
                            ?: throw Exception("获取access token失败。")

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=yipinshangdu-4wk7z&name=getSetting"
                    val args: MutableList<PostArgument> = ArrayList()
                    args.add(PostArgument("pwd", pwd))
                    args.add(PostArgument("name", name))
                    OkHttpClientUtil.postRequestByJson(url, args, object : HttpCallback {
                        override fun excute(html: String) {
                            try {
                                e(html)
                                val resp_data: Any = _JsonUtils.getValueByKey(html, "resp_data")
                                if (_JsonUtils.isContainsKey(resp_data, "value")) {
                                    val value = _JsonUtils.getValueByKey(resp_data, "value")
                                    callback.excute(0, value)
                                } else if (_JsonUtils.isContainsKey(resp_data, "msg")) {
                                    val code = _JsonUtils.getValueByKey(resp_data, "code").toInt()
                                    when (code) {
                                        0 -> callback.excute(-3, "操作码错误")
                                        1 -> callback.excute(-4, "不存在配置信息")
                                    }
                                }
                            } catch (e: Exception) {
                                callback.excute(-2, e.message!!)
                            }
                        }
                    })
                } catch (e: Exception) {
                    callback.excute(-1, e.message!!)
                }
            }
        })
    }

    fun addLocation(context: Context, pwd: String, latitude: Double, longitude: Double, address: String, callback: CloudCallback) {
        newMsgCount = 0

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", object : HttpCallback {
            override fun excute(html: String) {
                try {
                    val accessToken = _JsonUtils.getValueByKey(html, "access_token")
                            ?: throw Exception("获取access token失败。")

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=yipinshangdu-4wk7z&name=addLocation"
                    val args: MutableList<PostArgument> = ArrayList()
                    args.add(PostArgument("pwd", pwd))
                    args.add(PostArgument("date", System.currentTimeMillis()))
                    args.add(PostArgument("latitude", latitude))
                    args.add(PostArgument("longitude", longitude))
                    args.add(PostArgument("address", address))
                    OkHttpClientUtil.postRequestByJson(url, args, object : HttpCallback {
                        override fun excute(html: String) {
                            try {
                                e(html)
                                callback.excute(0, html)
                            } catch (e: Exception) {
                                callback.excute(-2, e.message!!)
                            }
                        }
                    })
                } catch (e: Exception) {
                    callback.excute(-1, e.message!!)
                }
            }
        })
    }

    fun getLocations(callback: CloudCallback?) {
        newMsgCount = 0

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", object : HttpCallback {
            override fun excute(html: String) {
                try {
                    val accessToken = _JsonUtils.getValueByKey(html, "access_token")
                            ?: throw Exception("获取access token失败。")

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    val url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=$accessToken&env=yipinshangdu-4wk7z&name=getLocations"
                    val args: List<PostArgument> = ArrayList()
                    OkHttpClientUtil.postRequestByJson(url, args, object : HttpCallback {
                        override fun excute(html: String) {
                            try {
                                e(html)
                                val resp_data: Any? = _JsonUtils.getValueByKey(html, "resp_data")
                                val data = _JsonUtils.getValueByKey(resp_data.toString(), "data").toString()
                                val jsonArray = JSONArray(data)
                                for (i in jsonArray.length() - 1 downTo 0) {
                                    val jsonObject = jsonArray.getString(i)
                                    val address = _JsonUtils.getValueByKey(jsonObject, "address").toString()
                                    val dateTime = _JsonUtils.getValueByKey(jsonObject, "dateTime").toString()
                                    e(dateTime)
                                }
                            } catch (e: Exception) {
                                callback?.excute(-2, e.message!!)
                            }
                        }
                    })
                } catch (e: Exception) {
                    callback?.excute(-1, e.message!!)
                }
            }
        })
    }

    private fun e(data: Any) {
        Log.e("wangsc", data.toString())
    }
}