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
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.*
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wang17.myclock.R
import com.wang17.myclock.callback.CloudCallback
import com.wang17.myclock.database.Position
import com.wang17.myclock.database.Setting
import com.wang17.myclock.database.Setting.KEYS
import com.wang17.myclock.database.utils.DataContext
import com.wang17.myclock.model.DateTime
import com.wang17.myclock.model.Lunar
import com.wang17.myclock.model.StockInfo
import com.wang17.myclock.utils.LightSensorUtil
import com.wang17.myclock.utils._CloudUtils
import com.wang17.myclock.utils._SinaStockUtils
import com.wang17.myclock.utils._Utils
import kotlinx.android.synthetic.main.activity_fullscreen.*
import kotlinx.android.synthetic.main.activity_fund_monitor.*
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlinx.android.synthetic.main.activity_fullscreen.imageView_warning as imageView_warning1
import kotlinx.android.synthetic.main.activity_fullscreen.image_volumn as image_volumn1
import kotlinx.android.synthetic.main.activity_fullscreen.textView_log as textView_log1

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity(), SensorEventListener {
    val mHideHandler: Handler
    var mainReciver: MainReciver
    var isNock: Boolean
    var isLoaded: Boolean
    var uiThreadHandler: Handler
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
        uiThreadHandler = Handler()
        mHideHandler = Handler()
        timer = Timer()
        lightLevel = 0f
    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        uiThreadHandler = Handler()
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
            this.finish()
            startActivity(Intent(this,FundMonitorActivity::class.java))
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

    private fun startTimer() {
        timer.schedule(object : TimerTask() {
            override fun run() {
/*
                runOnUiThread {
                    if(isFundMonitor){
                        pc_second.visibility = View.INVISIBLE
                    }else{
                        pc_second.visibility = View.VISIBLE
                        tv_day.setTextColor(Color.WHITE)
                    }
                }

                if (isFundMonitor) {
                    _SinaStockUtils.getStockInfoList(positions, object : _SinaStockUtils.OnLoadStockInfoListListener {
                        override fun onLoadFinished(stockInfoList: List<StockInfo>, totalProfit: Double, averageProfit: Double, time: String) {
                            try {
                                val size = stockInfoList.size
                                e("$size , $totalProfit , $averageProfit")
                                if (stockInfoList.size == 0) {
                                    return
                                }
                                runOnUiThread(Runnable {
                                    tv_day.text = DecimalFormat("0.00").format(averageProfit * 100)
                                    if (averageProfit > 0) {
                                        tv_day.setTextColor(Color.RED)
                                    } else if (averageProfit == 0.0) {
                                        tv_day.setTextColor(Color.WHITE)
                                    } else {
                                        tv_day.setTextColor(Color.CYAN)
                                    }

                                    val aa = time.split(":")
                                    val now = DateTime()
                                    val tradeTime = DateTime(now.year, now.month, now.day, aa[0].toInt(), aa[1].toInt(), aa[2].toInt())
                                    if (now.timeInMillis - tradeTime.timeInMillis > 10000) {
                                        imageView_warning.visibility = View.VISIBLE
                                    } else {
                                        imageView_warning.visibility = View.INVISIBLE
                                    }

                                })
                                var speakMsg = ""
                                //region 股票平均盈利
                                val msgS = DecimalFormat("0.00").format(averageProfit * 100)
                                Log.e("wangsc", "averageTotalProfitS: $msgS")
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
                                _Utils.printException(this@FullscreenActivity, e)

                                textView_log.visibility = View.VISIBLE
                                textView_log.text = e.message

//                                timer.cancel()
                                Log.e("wangsc", e.message)
                                e.printStackTrace()
                            }
                        }
                    })
                }
*/
                uiThreadHandler.post {
                    try {
                        val now = DateTime()
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
                            val weekday = now[Calendar.DAY_OF_WEEK]
                            if (weekday != 7 && weekday != 1) {
                                if (now.hour == 8 && now.minite == 55) {
                                    _Utils.ling(this@FullscreenActivity, R.raw.morning)
                                } else if (now.hour == 14 && now.minite == 55) {
                                    _Utils.ling(this@FullscreenActivity, R.raw.bye)
                                }
                            }
                        }
                    } catch (e: Exception) {

                        textView_log.visibility = View.VISIBLE
                        textView_log.text = e.message

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
                    -1 -> {
                    }
                    -2 -> {
                    }
                    -3 -> {
                    }
                    -4 -> {
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
        uiThreadHandler.post { // 计时计日
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