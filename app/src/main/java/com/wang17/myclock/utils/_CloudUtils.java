package com.wang17.myclock.utils;

import android.content.Context;
import android.util.Log;

import com.wang17.myclock.callback.CloudCallback;
import com.wang17.myclock.callback.HttpCallback;
import com.wang17.myclock.database.Setting;
import com.wang17.myclock.database.utils.DataContext;
import com.wang17.myclock.model.DateTime;
import com.wang17.myclock.model.PostArgument;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class _CloudUtils {

    private static final String _TAG = "wangsc";
    private static int newMsgCount = 0;

    public static void getNewMsg(final Context context, final CloudCallback callback) {
        newMsgCount = 0;

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", new HttpCallback() {
            @Override
            public void excute(String html) {
                try {
                    DataContext dataContext = new DataContext(context);
                    Object accessToken = _JsonUtils.getValueByKey(html, "access_token");
                    if (accessToken == null) {
                        throw new Exception("获取access token失败。");
                    }

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    String url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=" + accessToken + "&env=yipinshangdu-4wk7z&name=getNewMsg";

                    List<PostArgument> args = new ArrayList<>();
                    args.add(new PostArgument("pwd", dataContext.getSetting(Setting.KEYS.wx_request_code, "0000").getString()));
                    OkHttpClientUtil.postRequestByJson(url, args, new HttpCallback() {
                        @Override
                        public void excute(String html) {
                            try {
//                                e(html);
                                Object resp_data = _JsonUtils.getValueByKey(html, "resp_data");
//                                e(resp_data);

                                if (_JsonUtils.isContainsKey(resp_data.toString(), "data")) {

                                    String data = _JsonUtils.getValueByKey(resp_data.toString(), "data").toString();

//                                    e(data);
                                    JSONArray jsonArray = new JSONArray(data);
                                    DateTime time = null;
                                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                        String jsonObject = jsonArray.getString(i);
                                        long sendTimeTS = Long.parseLong(_JsonUtils.getValueByKey(jsonObject, "sendTimeTS").toString());
                                        String sendTime = _JsonUtils.getValueByKey(jsonObject, "sendTime").toString();
                                        e(sendTime);
                                        time = new DateTime(sendTimeTS);
                                    }

                                    if (jsonArray.length() == 0) {
                                        callback.excute(0, "");
                                    } else {
                                        callback.excute(1, time.toOffset2() + "\t" + jsonArray.length());
                                    }
                                } else if (_JsonUtils.isContainsKey(resp_data.toString(), "msg")) {

                                }
                            } catch (Exception e) {
                                callback.excute(-2, e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    callback.excute(-1, e.getMessage());
                }
            }
        });
    }

    public static void getUser(final String pwd, final CloudCallback callback) {
        newMsgCount = 0;

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", new HttpCallback() {
            @Override
            public void excute(String html) {
                try {
                    Object accessToken = _JsonUtils.getValueByKey(html, "access_token");
                    if (accessToken == null) {
                        throw new Exception("获取access token失败。");
                    }

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    String url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=" + accessToken + "&env=yipinshangdu-4wk7z&name=getUser";

                    List<PostArgument> args = new ArrayList<>();
                    args.add(new PostArgument("pwd", pwd));
                    OkHttpClientUtil.postRequestByJson(url, args, new HttpCallback() {
                        @Override
                        public void excute(String html) {
                            try {
                                Object resp_data = _JsonUtils.getValueByKey(html, "resp_data");
                                String data = _JsonUtils.getValueByKey(resp_data.toString(), "data").toString();
                                JSONArray jsonArray = new JSONArray(data);

                                if (jsonArray.length() > 0) {
                                    String jsonObject = jsonArray.getString(0);
                                    String name = _JsonUtils.getValueByKey(jsonObject, "name").toString();
                                    if (callback != null)
                                        callback.excute(0, name);
                                } else {
                                    if (callback != null)
                                        callback.excute(1, "访问码有误");
                                }
                            } catch (Exception e) {
                                if (callback != null)
                                    callback.excute(-2, e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    if (callback != null)
                        callback.excute(-1, e.getMessage());
                }
            }
        });
    }


    public static void getSetting(final String pwd,final String name, final CloudCallback callback) {
        newMsgCount = 0;

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", new HttpCallback() {
            @Override
            public void excute(String html) {
                try {
                    Object accessToken = _JsonUtils.getValueByKey(html, "access_token");
                    if (accessToken == null) {
                        throw new Exception("获取access token失败。");
                    }

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    String url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=" + accessToken + "&env=yipinshangdu-4wk7z&name=getSetting";

                    List<PostArgument> args = new ArrayList<>();
                    args.add(new PostArgument("pwd", pwd));
                    args.add(new PostArgument("name", name));
                    OkHttpClientUtil.postRequestByJson(url, args, new HttpCallback() {
                        @Override
                        public void excute(String html) {
                            try {
                                e(html);
                                Object resp_data = _JsonUtils.getValueByKey(html, "resp_data");
                                if(_JsonUtils.isContainsKey(resp_data,"value")){
                                    String value = _JsonUtils.getValueByKey(resp_data, "value");
                                    if (callback != null)
                                        callback.excute(0, value);
                                }else if(_JsonUtils.isContainsKey(resp_data,"msg")){
                                    int code =Integer.parseInt( _JsonUtils.getValueByKey(resp_data,"code"));
                                    switch (code){
                                        case 0:
                                            if (callback != null)
                                                callback.excute(-3, "操作码错误");
                                            break;
                                        case 1:
                                            if (callback != null)
                                                callback.excute(-4, "不存在配置信息");
                                            break;
                                    }
                                }

                            } catch (Exception e) {
                                if (callback != null)
                                    callback.excute(-2, e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    if (callback != null)
                        callback.excute(-1, e.getMessage());
                }
            }
        });
    }

    public static void addLocation(Context context, final String pwd, final double latitude, final double longitude, final String address, final CloudCallback callback) {
        newMsgCount = 0;

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", new HttpCallback() {
            @Override
            public void excute(String html) {
                try {
                    Object accessToken = _JsonUtils.getValueByKey(html, "access_token");
                    if (accessToken == null) {
                        throw new Exception("获取access token失败。");
                    }

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    String url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=" + accessToken + "&env=yipinshangdu-4wk7z&name=addLocation";
                    List<PostArgument> args = new ArrayList<>();
                    args.add(new PostArgument("pwd", pwd));
                    args.add(new PostArgument("date", System.currentTimeMillis()));
                    args.add(new PostArgument("latitude", latitude));
                    args.add(new PostArgument("longitude", longitude));
                    args.add(new PostArgument("address", address));
                    OkHttpClientUtil.postRequestByJson(url, args, new HttpCallback() {
                        @Override
                        public void excute(String html) {
                            try {
                                e(html);

                                if (callback != null)
                                    callback.excute(0, html);

                            } catch (Exception e) {
                                if (callback != null)
                                    callback.excute(-2, e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    if (callback != null)
                        callback.excute(-1, e.getMessage());
                }
            }
        });
    }

    public static void getLocations(final CloudCallback callback) {
        newMsgCount = 0;

        // 获取accessToken
        OkHttpClientUtil.getRequest("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxbdf065bdeba96196&secret=d2834f10c0d81728e73a4fe4012c0a5d", new HttpCallback() {
            @Override
            public void excute(String html) {
                try {
                    Object accessToken = _JsonUtils.getValueByKey(html, "access_token");
                    if (accessToken == null) {
                        throw new Exception("获取access token失败。");
                    }

                    // 通过accessToken，env，云函数名，args 在微信小程序云端获取数据
                    String url = "https://api.weixin.qq.com/tcb/invokecloudfunction?access_token=" + accessToken + "&env=yipinshangdu-4wk7z&name=getLocations";

                    List<PostArgument> args = new ArrayList<>();
                    OkHttpClientUtil.postRequestByJson(url, args, new HttpCallback() {
                        @Override
                        public void excute(String html) {
                            try {
                                e(html);
                                Object resp_data = _JsonUtils.getValueByKey(html, "resp_data");
                                String data = _JsonUtils.getValueByKey(resp_data.toString(), "data").toString();
                                JSONArray jsonArray = new JSONArray(data);


                                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                    String jsonObject = jsonArray.getString(i);
                                    String address = _JsonUtils.getValueByKey(jsonObject, "address").toString();
                                    String dateTime = _JsonUtils.getValueByKey(jsonObject, "dateTime").toString();
                                    e(dateTime);

                                }
                            } catch (Exception e) {
                                if (callback != null)
                                    callback.excute(-2, e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    if (callback != null)
                        callback.excute(-1, e.getMessage());
                }
            }
        });
    }

    private static void e(Object data) {
        Log.e("wangsc", data.toString());
    }
}
