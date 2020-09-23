package com.wang17.myclock.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.wang17.myclock.model.DateTime
import java.io.*
import java.util.*

/**
 * Created by 阿弥陀佛 on 2016/10/18.
 */
object _Utils {
    /**
     * 隐藏虚拟按键，并且全屏
     */
    fun hideBottomUIMenu(activity: Activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            val v = activity.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = activity.window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        }
    }

    fun isAppRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = "com.wangsc.lovehome"
        val appProcesses = activityManager.runningAppProcesses
        if (appProcesses == null) {
            Log.e("wangsc", "null")
            return false
        }
        for (appProcess in appProcesses) {
            Log.e("wangsc", appProcess.processName)
            if (appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    /**
     * 模拟点击HOME按钮
     *
     * @param context
     */
    fun clickHomeButton(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private lateinit var textToSpeech: TextToSpeech //创建自带语音对象
    fun speaker(context: Context, msg: String, pitch: Float, speech: Float) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setPitch(pitch) //方法用来控制音调
                textToSpeech.setSpeechRate(speech) //用来控制语速

                //判断是否支持下面语言
                val result = textToSpeech.setLanguage(Locale.CHINA)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "SIMPLIFIED_CHINESE数据丢失或不支持", Toast.LENGTH_SHORT).show()
                } else {
                    textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null) //输入中文，若不支持的设备则不会读出来
                }
            }
        }
    }

    fun ling(context: Context?, raw: Int) {
        val soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 5)
        soundPool.load(context, raw, 1)
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status -> soundPool.play(1, 1f, 1f, 0, 0, 1f) }
    }

    /**
     * 行房节欲期
     *
     * @param birthday
     * @return
     */
    fun getTargetInMillis(birthday: DateTime): Long {
        val now = DateTime()
        var age = now.year - birthday.year + 1
        if (now.month < birthday.month) {
            age -= 1
        }
        var day = 100.0
        if (age < 18) {
            day = -1.0
        } else if (age >= 18 && age < 20) {
            day = 3.0
        } else if (age >= 20 && age < 30) {
            day = 4 + (age - 20) * 0.4
        } else if (age >= 30 && age < 40) {
            day = 8 + (age - 30) * 0.8
        } else if (age >= 40 && age < 50) {
            day = 16 + (age - 40) * 0.5
        } else if (age >= 50 && age < 60) {
            day = 21 + (age - 50) * 0.9
        }
        return (day * 24 * 3600000).toLong()
    }

    fun getTargetInHours(birthday: DateTime): Int {
        return (getTargetInMillis(birthday) / 3600000).toInt()
    }

    fun printException(context: Context?, e: Exception) {
        if (e.stackTrace.size == 0) return
        var msg = ""
        for (ste in e.stackTrace) {
            if (context != null && ste.className.contains(context.packageName)) {
                msg += """
                    类名：
                    ${ste.className}
                    方法名：
                    ${ste.methodName}
                    行号：${ste.lineNumber}
                    错误信息：
                    ${e.message}

                    """.trimIndent()
            }
        }
        try {
            AlertDialog.Builder(context!!).setMessage(msg).setCancelable(false).setPositiveButton("知道了", null).show()
        } catch (e1: Exception) {
        }
        try {
            error2file("运行错误", msg)
        } catch (e1: Exception) {
        }
    }

    fun log2file(filename: String?, item: String?, message: String?) {
        try {
            val logFile = File(_Session.ROOT_DIR, filename)
            val writer = BufferedWriter(FileWriter(logFile, true))
            writer.write(DateTime().toLongDateTimeString())
            writer.newLine()
            writer.write(item)
            writer.newLine()
            writer.write(message ?: "空")
            writer.newLine()
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmOverloads
    fun runlog2file(item: String?, message: String? = "") {
        try {
            val logFile = File(_Session.ROOT_DIR, "run.log")
            val writer = BufferedWriter(FileWriter(logFile, true))
            writer.write(DateTime().toLongDateTimeString())
            writer.newLine()
            writer.write(item)
            writer.newLine()
            writer.write(message ?: "空")
            writer.newLine()
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun error2file(item: String?, message: String?) {
        try {
            val logFile = File(_Session.ROOT_DIR, "error.log")
            val writer = BufferedWriter(FileWriter(logFile, true))
            writer.write(DateTime().toLongDateTimeString())
            writer.newLine()
            writer.write(item)
            writer.newLine()
            writer.write(message)
            writer.newLine()
            writer.newLine()
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //        new DataContext(context).addRunLog2File(new RunLog(item, message));
    }

    fun getFilesWithSuffix(path: String, suffix: String): Array<String> {
        val file = File(path)
        val files = file.list { dir, name -> if (name != null && name.endsWith(suffix)) true else false }
        return files ?: arrayOf()
    }

    /**
     * 判断耳机是否连接。
     *
     * @return
     */
    val isHeadsetExists: Boolean
        get() {
            val buffer = CharArray(1024)
            var newState = 0
            try {
                val file = FileReader("/sys/class/switch/h2w/state")
                val len = file.read(buffer, 0, 1024)
                newState = Integer.valueOf(String(buffer, 0, len).trim { it <= ' ' })
            } catch (e: FileNotFoundException) {
                Log.e("FMTest", "This kernel does not have wired headset support")
            } catch (e: Exception) {
                Log.e("FMTest", "", e)
            }
            return newState != 0
        }
    var mWakeLock: WakeLock? = null
    @SuppressLint("InvalidWakeLockTag")
    fun screenOn(context: Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "bright")
        mWakeLock!!.acquire(120000)
    }

    fun screenOff(context: Context) {
        val mDevicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mDevicePolicyManager.lockNow()
    }
}