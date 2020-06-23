package com.wang17.myclock.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.wang17.myclock.R;
import com.wang17.myclock.database.MarkDay;
import com.wang17.myclock.database.Setting;
import com.wang17.myclock.database.utils.DataContext;
import com.wang17.myclock.database.utils.DayItem;
import com.wang17.myclock.model.DateTime;
import com.wang17.myclock.utils._Session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by 阿弥陀佛 on 2016/10/18.
 */

public class _Utils {

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = "com.wangsc.lovehome";

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            Log.e("wangsc", "null");
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            Log.e("wangsc", appProcess.processName);
            if (appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 模拟点击HOME按钮
     *
     * @param context
     */
    public static void clickHomeButton(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static TextToSpeech textToSpeech = null;//创建自带语音对象

    public static void speaker(final Context context, final String msg) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setPitch(1.0f);//方法用来控制音调
                    textToSpeech.setSpeechRate(1.0f);//用来控制语速

                    //判断是否支持下面语言
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "SIMPLIFIED_CHINESE数据丢失或不支持", Toast.LENGTH_SHORT).show();
                    } else {
                        textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);//输入中文，若不支持的设备则不会读出来
                    }
                }
            }
        });
    }

    public static void ling(Context context,int raw) {
        SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundPool.load(context, raw, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1, 1f, 1f, 0, 0, 1);
            }
        });
    }

    /**
     * 行房节欲期
     *
     * @param birthday
     * @return
     */
    public static long getTargetInMillis(DateTime birthday) {
        DateTime now = new DateTime();
        int age = (now.getYear() - birthday.getYear()) + 1;
        if (now.getMonth() < birthday.getMonth()) {
            age -= 1;
        }
        double day = 100;
        if (age < 18) {
            day = -1;
        } else if (age >= 18 && age < 20) {
            day = 3;
        } else if (age >= 20 && age < 30) {
            day = 4 + (age - 20) * 0.4;
        } else if (age >= 30 && age < 40) {
            day = 8 + (age - 30) * 0.8;
        } else if (age >= 40 && age < 50) {
            day = 16 + (age - 40) * 0.5;
        } else if (age >= 50 && age < 60) {
            day = 21 + (age - 50) * 0.9;
        }
        return (long) (day * 24 * 3600000);
    }

    public static int getTargetInHours(DateTime birthday) {
        return (int) (getTargetInMillis(birthday) / 3600000);
    }


    public static void printException(Context context, Exception e) {
        if (e.getStackTrace().length == 0)
            return;
        String msg = "";
        for (StackTraceElement ste : e.getStackTrace()) {
            if (context != null && ste.getClassName().contains(context.getPackageName())) {
                msg += "类名：\n" + ste.getClassName()
                        + "\n方法名：\n" + ste.getMethodName()
                        + "\n行号：" + ste.getLineNumber()
                        + "\n错误信息：\n" + e.getMessage() + "\n";
            }
        }
        try {
            new AlertDialog.Builder(context).setMessage(msg).setCancelable(false).setPositiveButton("知道了", null).show();
        } catch (Exception e1) {
        }
        error2file("运行错误", msg);
    }

    public static void log2file(String filename, String item, String message) {
        try {
            File logFile = new File(_Session.ROOT_DIR, filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(new DateTime().toLongDateTimeString());
            writer.newLine();
            writer.write(item);
            writer.newLine();
            writer.write(message == null ? "空" : message);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runlog2file(String item) {
        runlog2file(item, "");
    }

    public static void runlog2file(String item, String message) {
        try {
            File logFile = new File(_Session.ROOT_DIR, "run.log");
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(new DateTime().toLongDateTimeString());
            writer.newLine();
            writer.write(item);
            writer.newLine();
            writer.write(message == null ? "空" : message);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void error2file(String item, String message) {
        try {
            File logFile = new File(_Session.ROOT_DIR, "error.log");
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(new DateTime().toLongDateTimeString());
            writer.newLine();
            writer.write(item);
            writer.newLine();
            writer.write(message);
            writer.newLine();
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        new DataContext(context).addRunLog2File(new RunLog(item, message));
    }

    public static String[] getFilesWithSuffix(String path, final String suffix) {
        File file = new File(path);
        String[] files = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name != null && name.endsWith(suffix))
                    return true;
                else
                    return false;
            }
        });
        return files == null ? new String[]{} : files;
    }


    /**
     * 判断耳机是否连接。
     *
     * @return
     */
    public static boolean isHeadsetExists() {
        char[] buffer = new char[1024];

        int newState = 0;

        try {
            FileReader file = new FileReader("/sys/class/switch/h2w/state");
            int len = file.read(buffer, 0, 1024);
            newState = Integer.valueOf((new String(buffer, 0, len)).trim());
        } catch (FileNotFoundException e) {
            Log.e("FMTest", "This kernel does not have wired headset support");
        } catch (Exception e) {
            Log.e("FMTest", "", e);
        }
        return newState != 0;
    }


    public static PowerManager.WakeLock mWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    public static void screenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "bright");
        mWakeLock.acquire(120000);
    }

    public static void screenOff(Context context) {
        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDevicePolicyManager.lockNow();
    }
}
