package com.wang17.myclock.activity

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import android.os.*
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wang17.myclock.*
import com.wang17.myclock.callback.CloudCallback
import com.wang17.myclock.callback.ExchangeEvent
import com.wang17.myclock.database.Position
import com.wang17.myclock.database.Setting
import com.wang17.myclock.database.utils.DataContext
import com.wang17.myclock.model.DateTime
import com.wang17.myclock.model.StockInfo
import com.wang17.myclock.utils.*
import kotlinx.android.synthetic.main.activity_fund_monitor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlinx.android.synthetic.main.activity_fund_monitor.textView_log as textView_log1

class FundMonitorActivity : AppCompatActivity() {
    val mHideHandler: Handler

    private lateinit var positions: List<Position>
    private lateinit var mDataContext: DataContext
    private lateinit var mainReciver: MainReciver

    private var preClickTime: Long = 0
    private var preBatteryTime: Long = 0

    init {
        mHideHandler = Handler()
    }

    private val mHidePart2Runnable = Runnable {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
    private val mShowPart2Runnable = Runnable {
        val actionBar = supportActionBar
        actionBar?.show()
    }
    private val mHideRunnable = Runnable { hide() }

    override fun onDestroy() {
        e("**** fund monitor onDestroy")
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)

        /**
         * 设置音量
         */
        val audioUtils = AudioUtils.getInstance(this)
        if (audioUtils.mediaVolume < 7)
            audioUtils.mediaVolume = audioUtils.mediaMaxVolume

        /**
         * 设置全屏的俩段代码必须在setContentView(R.layout.main) 之前，不然会报错。
         */
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_fund_monitor)

        mDataContext = DataContext(this)
        preClickTime = System.currentTimeMillis()

        val latch = CountDownLatch(1)
        _CloudUtils.getPositions(this,"0088", object : CloudCallback {
            override fun excute(code: Int, result: Any) {
                when (code) {
                    0 -> {
                        positions = result as MutableList<Position>
                    }
                    -1 -> {
                        e(result.toString())
                    }
                    -2 -> {
                        e(result.toString())
                    }
                }
                latch.countDown()
            }
        })
        latch.await()


        /**
         * 初始化电量记录
         */
        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
        val battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        mDataContext.editSetting(Setting.KEYS.battery, battery)
        /**
         * 监听
         */
        mainReciver = MainReciver()
        val filter = IntentFilter()
        // 监控开关屏
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        // 监控电量变化
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(mainReciver, filter)

        var ispeak = mDataContext.getSetting(Setting.KEYS.is_stock_speak, false).boolean
        if (ispeak) {
            image_volumn.visibility = View.VISIBLE
        } else {
            image_volumn.visibility = View.GONE
        }

        layout_root.setOnClickListener {
//            SocketService.clock(this)
        }



        layout_root.setOnLongClickListener {
            this.finish()
            startActivity(Intent(this, FullscreenActivity::class.java))
            true
        }

        progressBar.max = 59
        progressBar2.max = 59

        imageView_warning.setOnClickListener {
//            SocketService.clock(this)
        }
        textView_time.setOnClickListener {
//            SocketService.clock(this)
        }
        textView_sz.setOnClickListener {
//            SocketService.clock(this)
        }

        textView_totalProfit.setOnClickListener {
            var ispeak = image_volumn.visibility == View.VISIBLE
            ispeak = !ispeak

            mDataContext.editSetting(Setting.KEYS.is_stock_speak, ispeak)
            if (ispeak) {
                image_volumn.visibility = View.VISIBLE
            } else {
                image_volumn.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        e("**** fund monitor onResume")
        super.onResume()
        _Utils.checkSocketService(this, 8000)
        startTimer()
    }

    override fun onPause() {
        super.onPause()
        e("**** fund monitor onPause")
        stopTimer()
        // 解除监控开关屏
        if (mainReciver != null) unregisterReceiver(mainReciver)
    }

    private fun stopTimer() {
        timer.cancel()
        timer.purge()
    }

    private lateinit var timer: Timer
    private var preAverageProfit = 0.0
    private fun e(log: Any) {
        Log.e("wangsc",log.toString())
        _Utils.runlog2file(log.toString(),null)
    }


    var tag = true
    private fun startTimer() {
        try {
            timer = Timer()
            timer.schedule(object : TimerTask() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun run() {
                    try {
                        val now = DateTime()
                        /**
                         * 整点报时
                         */
                        if (now.second == 0) {
                            if (now.minite == 0) {
                                _Utils.speaker(this@FundMonitorActivity, now.hour.toString() + "点", 1.0f, 1.0f)
                            }
                        }

                        val weekday = now.get(Calendar.DAY_OF_WEEK)
                        if (weekday != 7 && weekday != 1) {
                            if (now.hour == 14 && now.minite == 50 && now.second == 0) {
                                if (weekday == 6) {
                                    _Utils.speaker(this@FundMonitorActivity, "今天星期五，周一再见！", 1.0f, 1.0f)
                                } else {
                                    _Utils.ling(this@FundMonitorActivity, R.raw.bye)
                                }
                            }
//                            else if (now.hour == 15) {
//                                toFullScreenActivity()
//                                return
//                            }
                        }

                        if (isAlarmRunning) {
                            runOnUiThread {
                                layout_root.setBackgroundResource(R.color.alarm_color)
                            }
                            if (now.timeInMillis >= targetTimeInMillis) {
//                                _Utils.ling(this@FundMonitorActivity, R.raw.ding)
                                _Utils.speaker(this@FundMonitorActivity, getString(R.string.speaker_alarm), 1.0f, 0.8f)
                                isAlarmRunning = false
                                runOnUiThread {
                                    layout_root.setBackgroundResource(R.color.b)
                                }
                            }
                        }

                        runOnUiThread {
                            textView_weekday.text = now.weekDayStr
                            if (now.get(Calendar.DAY_OF_WEEK) == 6) {
                                textView_weekday.setTextColor(Color.RED)
                            } else {
                                textView_weekday.setTextColor(Color.WHITE)
                            }
                            textView_time.text = now.toShortTimeString()
//                            if (now.second == 0) {
//                                tag = !tag
//                                progressBar.rotation += 180f
//                                progressBar2.rotation += 180f
//                            }
//                            if (tag) {
//                                progressBar.progress = 59 - now.second
//                                progressBar2.progress = now.second
//                            } else {
//                                progressBar.progress = now.second
//                                progressBar2.progress = 59 - now.second
//                            }
                        }

                        runOnUiThread {
                            try {
                                reflushBatteryNumber()

                                var info = StockInfo()
                                info.code = "000001"
                                info.exchange = "sh"

                                _SinaStockUtils.getStockInfo(info, object : _SinaStockUtils.OnLoadStockInfoListener {
                                    override fun onLoadFinished(info: StockInfo, time: String) {
                                        runOnUiThread {
                                            textView_sz.text = DecimalFormat("0.00").format(info.increase * 100)
                                            if (info.increase > 0) {
                                                textView_sz.setTextColor(Color.RED)
                                            } else if (info.increase == 0.0) {
                                                textView_sz.setTextColor(Color.WHITE)
                                            } else {
                                                textView_sz.setTextColor(Color.CYAN)
                                            }
                                        }
                                    }
                                })

                                _SinaStockUtils.getStockInfoList(positions, object : _SinaStockUtils.OnLoadStockInfoListListener {
                                    override fun onLoadFinished(stockInfoList: MutableList<StockInfo>, totalProfit: Double, averageProfit: Double, time: String) {
                                        try {
                                            val size = stockInfoList.size
//                                            e("$size , $totalProfit , $averageProfit")
                                            if (stockInfoList.size == 0) {
                                                return
                                            }

                                            runOnUiThread {
                                                textView_totalProfit.text = DecimalFormat("0.00").format(averageProfit * 100)
                                                if (averageProfit > 0) {
                                                    textView_totalProfit.setTextColor(Color.RED)
                                                } else if (averageProfit == 0.0) {
                                                    textView_totalProfit.setTextColor(Color.WHITE)
                                                } else {
                                                    textView_totalProfit.setTextColor(Color.CYAN)
                                                }

                                                val aa = time.split(":")
                                                val now = DateTime()
                                                val tradeTime = DateTime(now.year, now.month, now.day, aa[0].toInt(), aa[1].toInt(), aa[2].toInt())
                                                if (now.timeInMillis - tradeTime.timeInMillis > 10000) {
                                                    imageView_warning.visibility = View.VISIBLE
                                                    textView_sz.visibility = View.INVISIBLE
                                                } else {
                                                    imageView_warning.visibility = View.INVISIBLE
                                                    textView_sz.visibility = View.VISIBLE
                                                }
                                            }
                                            var speakMsg = ""
                                            //region 股票平均盈利
                                            val msgS = DecimalFormat("0.00").format(averageProfit * 100)
//                                            Log.e("wangsc", "averageTotalProfitS: $msgS")
                                            if (Math.abs(averageProfit - preAverageProfit) * 100 > (1.0 / size)) {
                                                preAverageProfit = averageProfit
                                                speakMsg += msgS
                                            }
                                            if (!speakMsg.isEmpty() && image_volumn.visibility == View.VISIBLE) {
                                                var pitch = 1.0f
                                                var speech = 1.2f
                                                if (averageProfit < 0) {
                                                    pitch = 0.1f
                                                    speech = 0.8f
                                                }
                                                _Utils.speaker(getApplicationContext(), speakMsg, pitch, speech)
                                            }
                                        } catch (e: Exception) {

                                            runOnUiThread {
                                                imageView_warning.visibility = View.VISIBLE
                                                textView_sz.visibility = View.INVISIBLE
                                                textView_log1.visibility = View.VISIBLE
                                                textView_log1.text = e.message
                                            }
//                                            timer.cancel()
                                            Log.e("wangsc", e.message)
                                            e.printStackTrace()
                                        }
                                    }
                                })
                            } catch (e: Exception) {
                                runOnUiThread {
                                    imageView_warning.visibility = View.VISIBLE
                                    textView_sz.visibility = View.INVISIBLE
                                    textView_log1.visibility = View.VISIBLE
                                    textView_log1.text = e.message
                                }
//                                timer.cancel()
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
//                        timer.cancel()

                        runOnUiThread {
                            imageView_warning.visibility = View.VISIBLE
                            textView_sz.visibility = View.INVISIBLE
                            textView_log1.visibility = View.VISIBLE
                            textView_log1.text = e.message
                        }
                        e.printStackTrace()
                    }
                }
            }, 0, 1000)
        } catch (e: Exception) {
//            timer.cancel()
            runOnUiThread {
                imageView_warning.visibility = View.VISIBLE
                textView_sz.visibility = View.INVISIBLE

                textView_log1.visibility = View.VISIBLE
                textView_log1.text = e.message
            }
            e.printStackTrace()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun ExchangeActivity(event: ExchangeEvent) {
        toFullScreenActivity()
    }

    private fun toFullScreenActivity() {
        try {
            this.finish()
            startActivity(Intent(this, FullscreenActivity::class.java))
            timer.cancel()
        } catch (e: Exception) {
            runOnUiThread {
                textView_log1.visibility = View.VISIBLE
                textView_log1.text = e.message
            }
        }
    }

    private fun hide() {
        // Hide UI first
        val actionBar = supportActionBar
        actionBar?.hide()

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, 300)
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        delayedHide(100)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun reflushBatteryNumber() {
        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
        val battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        textView_battery.text = "$battery%"
    }

    private fun parseItem(code: String): String {
        val item = StringBuffer()
        for (i in 0 until code.length) {
            val c = code[i]
            if (!Character.isDigit(c)) {
                item.append(c)
            } else {
                break
            }
        }
        return item.toString()
    }

    inner class MainReciver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF, Intent.ACTION_SCREEN_ON -> {
                    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    if (!pm.isScreenOn) {
                        _Utils.speaker(this@FundMonitorActivity.getApplicationContext(), mDataContext.getSetting(Setting.KEYS.speaker_screen_off, "关").string, 1.0f, 1.0f)
                    }
                }
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra("level", 0)
                    val dataLevel = mDataContext.getSetting(Setting.KEYS.battery, 0).int
                    if (dataLevel != level) {
                        val span = System.currentTimeMillis() - if (preBatteryTime == 0L) System.currentTimeMillis() else preBatteryTime
                        preBatteryTime = System.currentTimeMillis()
                        mDataContext.editSetting(Setting.KEYS.battery, level)
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            val audio = getSystemService(Service.AUDIO_SERVICE) as AudioManager
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    audio.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_SHOW_UI)
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    audio.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_SHOW_UI)
                    return true
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            _Utils.printException(this@FundMonitorActivity, e)
        }
        return super.onKeyDown(keyCode, event)
    }
}