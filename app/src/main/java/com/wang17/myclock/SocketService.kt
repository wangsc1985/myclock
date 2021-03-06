package com.wang17.myclock

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.wang17.myclock.callback.ClocknockEvent
import com.wang17.myclock.callback.ExchangeEvent
import com.wang17.myclock.model.DateTime
import com.wang17.myclock.utils._Utils
import org.greenrobot.eventbus.EventBus
import java.io.DataInputStream
import java.net.ServerSocket
import java.util.*

class SocketService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        startSocket()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private lateinit var serverSocket: ServerSocket
    fun startSocket() {
        Thread {
            try {
                serverSocket = ServerSocket(8000)
                while (true) {
                    var s = serverSocket.accept()
                    var dis = DataInputStream(s.getInputStream())
                    when (dis.readInt()) {
                        0 -> {

                        }
                        1 -> {
                            // 滴答声
                            EventBus.getDefault().post(ClocknockEvent())
                        }
                        2 -> {
                            //  开启stock activity
                            EventBus.getDefault().post(ExchangeEvent())
                        }
                        3 -> {
                            // 开始计时
                            clock(applicationContext)
                        }
                    }
                    dis.close()
                    s.close()
                }
            } catch (e: Exception) {
                _Utils.log2file("err", "socket运行异常", e.message)
            }
        }.start()
    }


    companion object{
        fun clock(context:Context){
            e("计时开始")
            var now = DateTime()
            now.add(Calendar.MINUTE, 30)
            targetTimeInMillis = now.timeInMillis
            isAlarmRunning = true
            _Utils.ling(context,R.raw.bi)
//                            _Utils.speaker(this, "计时开始", 1.0f, 1.0f)
        }
    }
}