package com.wang17.myclock.activity


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.*
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wang17.myclock.R
import com.wang17.myclock.SocketService
import com.wang17.myclock.callback.ClocknockEvent
import com.wang17.myclock.callback.CloudCallback
import com.wang17.myclock.callback.ExchangeEvent
import com.wang17.myclock.database.Position
import com.wang17.myclock.database.Setting
import com.wang17.myclock.database.Setting.KEYS
import com.wang17.myclock.database.utils.DataContext
import com.wang17.myclock.isAlarmRunning
import com.wang17.myclock.model.DateTime
import com.wang17.myclock.model.Lunar
import com.wang17.myclock.targetTimeInMillis
import com.wang17.myclock.utils.AudioUtils
import com.wang17.myclock.utils.LightSensorUtil
import com.wang17.myclock.utils._CloudUtils
import com.wang17.myclock.utils._Utils
import kotlinx.android.synthetic.main.activity_fullscreen.*
import kotlinx.android.synthetic.main.activity_fullscreen.image_volumn
import kotlinx.android.synthetic.main.activity_fullscreen.textView_log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity(), SensorEventListener {
    val mHideHandler: Handler
    var mainReciver: MainReciver
    var isNock: Boolean
    var isLoaded: Boolean
    var timer: Timer
    var lightLevel: Float
    var sexDate: DateTime? = null
    private var preAverageProfit = 0.0
//    var isFundMonitor = false

    private lateinit var positions: List<Position>
    lateinit var dataContext: DataContext
    lateinit var soundPool: SoundPool
    lateinit var sensorManager: SensorManager

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

    init {
        mainReciver = MainReciver()
        isNock = false
        isLoaded = false
        mHideHandler = Handler()
        timer = Timer()
        lightLevel = 0f
    }

    override fun onResume() {
        super.onResume()
        startTimer()
        _Utils.checkSocketService(this,8000)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun ExchangeActivity(event:ExchangeEvent){
        toFundMonitor()
    }

    fun toFundMonitor() {
        try {
            this.finish()
            startActivity(Intent(this, FundMonitorActivity::class.java))
        } catch (e: Exception) {
            runOnUiThread {
                textView_log.visibility = View.VISIBLE
                textView_log.text = e.message
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun clocknock(event:ClocknockEvent){
        isNock=!isNock
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        EventBus.getDefault().register(this)

        /**
         * 设置音量
         */
        val audioUtils = AudioUtils.getInstance(this)
        val volume = (audioUtils.mediaMaxVolume*0.8).toInt()
        if (audioUtils.mediaVolume < volume)
            audioUtils.mediaVolume = volume

        dataContext = DataContext(this)
        tv_time.setOnLongClickListener(OnLongClickListener {
            loadSexdateFromCloud()
            true
        })
        soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 5)
        soundPool.load(this, R.raw.second, 1)
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status -> isLoaded = true }
        /**
         * 初始化
         */
        refreshSexDays()
        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
        val battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        tv_battery.setText("$battery%")
        /**
         * 监听
         */
        val filter = IntentFilter()
        // 监控电量变化
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(mainReciver, filter)

        //
        sensorManager = LightSensorUtil.getSenosrManager(this)
        LightSensorUtil.registerLightSensor(sensorManager, this)
        /**
         *
         */
        frameLayout.setOnClickListener({ isNock = !isNock })
        frameLayout.setOnLongClickListener {
            toFundMonitor()
            true
        }
        tv_time.setOnClickListener(View.OnClickListener { isNock = !isNock })
        tv_markday.setOnClickListener(View.OnClickListener { isNock = !isNock })
        tv_day.setOnClickListener(View.OnClickListener { isNock = !isNock })
        tv_markday.setOnLongClickListener(OnLongClickListener { true })
        loadSexdateFromCloud()

        val latch = CountDownLatch(1)
        _CloudUtils.getPositions("0088", object : CloudCallback {
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

        var ispeak = dataContext.getSetting(Setting.KEYS.is_stock_speak, false).boolean
        if (ispeak) {
            image_volumn.visibility = View.VISIBLE
        } else {
            image_volumn.visibility = View.GONE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun startTimer() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                val now = DateTime()

                val startDate1 = DateTime()
                startDate1.set(Calendar.HOUR_OF_DAY, 9)
                startDate1.set(Calendar.MINUTE, 25)
                startDate1.set(Calendar.SECOND, 0)
                val endDate1 = DateTime()
                endDate1.set(Calendar.HOUR_OF_DAY, 11)
                endDate1.set(Calendar.MINUTE, 30)
                endDate1.set(Calendar.SECOND, 0)
                val startDate2 = DateTime()
                startDate2.set(Calendar.HOUR_OF_DAY, 13)
                startDate2.set(Calendar.MINUTE, 0)
                startDate2.set(Calendar.SECOND, 0)
                val endDate2 = DateTime()
                endDate2.set(Calendar.HOUR_OF_DAY, 15)
                endDate2.set(Calendar.MINUTE, 0)
                endDate2.set(Calendar.SECOND, 0)

                val weekday = now.get((Calendar.DAY_OF_WEEK))
                if (weekday != 7 && weekday != 1 && (now.timeInMillis > startDate1.timeInMillis && now.timeInMillis < endDate1.timeInMillis||now.timeInMillis > startDate2.timeInMillis && now.timeInMillis < endDate2.timeInMillis)) {
                    toFundMonitor()
                    return
                }

                if (isAlarmRunning && now.timeInMillis >= targetTimeInMillis) {
                    _Utils.ling(this@FullscreenActivity,R.raw.ding)
//                    _Utils.speaker(this@FullscreenActivity, "时间到", 1.0f, 1.0f)
                    isAlarmRunning = false
                }


                runOnUiThread {
                    try {
                        val lunar = Lunar(now)
                        val day = now.day.toString() + ""
//                        if (!isFundMonitor)
                        tv_day.text = day
                        tv_lunar_month.setText(lunar.chinaMonthString)
                        tv_lunar_day.setText(lunar.chinaDayString)
                        tv_time.text = now.toShortTimeString()
                        tv_week.setText(now.weekDayStr)
                        pc_second.setProgress(if (now.second == 0) 60 else now.second)
                        if (isNock && isLoaded) {
                            soundPool!!.play(1, 1f, 1f, 0, 0, 1f)
                        }
                        /**
                         * 颜色
                         */
                        if (lunar.day == 15 || lunar.day == 1) {
                            frameLayout.setBackgroundResource(R.color.a)
                        } else {
                            frameLayout.setBackgroundResource(R.color.b)
                        }
                        if (now[Calendar.DAY_OF_WEEK] == 6) {
                            tv_week.setTextColor(Color.RED)
                        } else {
                            tv_week.setTextColor(Color.WHITE)
                        }
                        /**
                         * 每月初一、八、十四、十五、十八、二十三、二十四、二十八、二十九、三十
                         */
                        /**
                         * 每月初一、八、十四、十五、十八、二十三、二十四、二十八、二十九、三十
                         */
                        if (lunar.day == 1 || lunar.day == 8 || lunar.day == 14 || lunar.day == 15 || lunar.day == 18 || lunar.day == 23 || lunar.day == 24 || lunar.day == 28 || lunar.day == 29 || lunar.day == 30) {
                            tv_lunar_day.setTextColor(Color.RED)
                        } else {
                            tv_lunar_day.setTextColor(Color.WHITE)
                        }
                        /**
                         * 整分获取戒期
                         */
                        if (now.second == 0) {
                            // 更新戒期
                            refreshSexDays()
                            if (now.minite == 0) {
                                // 整点报时：在光敏大于5，也就是非夜间时，整点报时。
                                if (lightLevel > 5) _Utils.speaker(this@FullscreenActivity, now.hour.toString() + "点", 1.0f, 1.0f)
                                loadSexdateFromCloud()
                            }
                            if (weekday != 7 && weekday != 1) {
                                if (now.hour == 8 && now.minite == 50) {
                                    if (weekday == 6) {
                                        _Utils.speaker(this@FullscreenActivity, "早上好，今天星期五！", 1.0f, 1.0f)
                                    } else {
                                        _Utils.ling(this@FullscreenActivity, R.raw.morning)
                                    }
                                } else if (now.hour == 14 && now.minite == 50) {
                                    if (weekday == 6) {
                                        _Utils.speaker(this@FullscreenActivity, "今天星期五，周一再见！", 1.0f, 1.0f)
                                    } else {
                                        _Utils.ling(this@FullscreenActivity, R.raw.bye)
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
runOnUiThread {
    textView_log.visibility = View.VISIBLE
    textView_log.text = e.message
}

//                        timer.cancel()
                        e.printStackTrace()
                    }
                }
            }
        }, 0, 1000)
    }

    private fun loadSexdateFromCloud() {
        _CloudUtils.getSetting("0088", KEYS.wx_sex_date.toString(), object : CloudCallback {
            override fun excute(code: Int, result: Any) {
                e(code)
                e(result)
                when (code) {
                    0 -> {
                        sexDate = DateTime(result.toString().toLong())
                        refreshSexDays()
                    }
                }
                Looper.prepare()
                Toast.makeText(this@FullscreenActivity, "更新完毕", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        })
    }

    private fun e(log: Any?) {
        Log.e("wangsc", log.toString())
    }

    /**
     * 设置行房天数。
     */
    private fun refreshSexDays() {
        runOnUiThread {
            var text = "0"
            var progress = 0
            val progressMax = 24 * 60
            if (sexDate != null) {
                val have = ((System.currentTimeMillis() - sexDate!!.timeInMillis) / 60000).toInt()
                val day = have / 60 / 24
                val minute = have % (24 * 60)
                progress = minute
                text = day.toString() + ""
            }
            tv_markday.text = text
            pc_religious.setMax(progressMax)
            pc_religious.setProgress(progress)
        }
    }

    override fun onStop() {
        super.onStop()

        timer.cancel()

        unregisterReceiver(mainReciver)
        LightSensorUtil.unregisterLightSensor(sensorManager, this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    private fun hide() {
        // Hide UI first
        val actionBar = supportActionBar
        actionBar?.hide()

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        delayedHide(100)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val light_strength = event.values[0]
            lightLevel = light_strength
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    inner class MainReciver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra("level", 0)
                    tv_battery.text = "$level%"
                }
            }
        }
    }

    companion object {
        private const val UI_ANIMATION_DELAY = 300
    }
}