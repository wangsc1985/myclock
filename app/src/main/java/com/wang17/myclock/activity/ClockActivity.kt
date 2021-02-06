package com.wang17.myclock.activity


import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wang17.myclock.R
import com.wang17.myclock.callback.ClocknockEvent
import com.wang17.myclock.callback.CloudCallback
import com.wang17.myclock.callback.ExchangeEvent
import com.wang17.myclock.database.Position
import com.wang17.myclock.database.Setting.KEYS
import com.wang17.myclock.database.utils.DataContext
import com.wang17.myclock.isAlarmRunning
import com.wang17.myclock.model.DateTime
import com.wang17.myclock.model.GanZhi
import com.wang17.myclock.model.Lunar
import com.wang17.myclock.model.SolarTerm
import com.wang17.myclock.targetTimeInMillis
import com.wang17.myclock.utils.AudioUtils
import com.wang17.myclock.utils.LightSensorUtil
import com.wang17.myclock.utils._CloudUtils
import com.wang17.myclock.utils._Utils
import kotlinx.android.synthetic.main.activity_clock.*
import kotlinx.android.synthetic.main.activity_clock.image_volumn
import kotlinx.android.synthetic.main.activity_clock.layout_root
import kotlinx.android.synthetic.main.activity_clock.textView_log
import kotlinx.android.synthetic.main.activity_clock.tv_ganzhi
import kotlinx.android.synthetic.main.activity_fund_monitor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.DataInputStream
import java.io.EOFException
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.status bar and navigation/system bar) with user interaction.
 */
class ClockActivity : AppCompatActivity(), SensorEventListener {
    private val MY_PERMISSIONS_REQUEST=100
    val mHideHandler: Handler
    var mainReciver: MainReciver
    var isNock: Boolean
    var isLoaded: Boolean
    lateinit var timer: Timer
    var lightLevel: Float
    var sexDate: DateTime? = null
    private var preAverageProfit = 0.0
    private lateinit var solarTermMap: TreeMap<DateTime, SolarTerm>
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
        lightLevel = 0f
    }

    override fun onResume() {
        e("fullscreen onResume")
        super.onResume()
        startTimer()
        _Utils.checkSocketService(this, 8000)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun ExchangeActivity(event: ExchangeEvent) {
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
    fun clocknock(event: ClocknockEvent) {
        isNock = !isNock
    }

    /**
     * 读取JAVA结构的二进制节气数据文件。
     *
     * @param resId 待读取的JAVA二进制文件。
     * @return
     */
    private fun loadJavaSolarTerms(resId:Int) :TreeMap<DateTime, SolarTerm>{
        var result = TreeMap<DateTime, SolarTerm>();
        try {
            var dis = DataInputStream(getResources().openRawResource(resId));

            var date = dis.readLong();
            var solar = dis.readInt();
            try {
                while (true) {
                    var cal = DateTime();
                    cal.setTimeInMillis(date);
                    var solarTerm = SolarTerm.Int2SolarTerm(solar);
                    result.put(cal, solarTerm);
                    date = dis.readLong();
                    solar = dis.readInt();
                }
            } catch ( ex: EOFException) {
                dis.close();
            }
        } catch ( e: Resources.NotFoundException) {
            Log.i("wangsc", e.message);
        } catch (e:Exception) {
            Log.i("wangsc", e.message);
        }
        // 按照KEY排序TreeMap
//        TreeMap<DateTime, SolarTerm> result = new TreeMap<DateTime, SolarTerm>(new Comparator<DateTime>() {
//            @Override
//            public int compare(DateTime lhs, DateTime rhs) {
//                return lhs.compareTo(rhs);
//            }
//        });
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        e("fullscreen on create")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
        EventBus.getDefault().register(this)

        //
        solarTermMap = loadJavaSolarTerms(R.raw.solar_java_50)
        if(requestPermissions()){
            initOnCreate()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initOnCreate() {
        /**
         * 设置音量
         */
//        val audioUtils = AudioUtils.getInstance(this)
//        val volume = (audioUtils.mediaMaxVolume * 0.8).toInt()
//        if (audioUtils.mediaVolume < volume)
//            audioUtils.mediaVolume = volume

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
//        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager?
//        e("battery manager : ${batteryManager}")
//        val battery = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
//        e("battery : ${battery}")
//        tv_battery.setText("$battery%")
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
        layout_root.setOnClickListener {
//            SocketService.clock(this)
        }
        layout_root.setOnLongClickListener {
            toFundMonitor()
            true
        }
        tv_time.setOnClickListener {
//            SocketService.clock(this)
        }
        tv_markday.setOnClickListener {
//            SocketService.clock(this)
        }
        tv_day.setOnClickListener {
//            SocketService.clock(this)
        }

        loadSexdateFromCloud()

        var ispeak = dataContext.getSetting(KEYS.is_stock_speak, false).boolean
        if (ispeak) {
            image_volumn.visibility = View.VISIBLE
        } else {
            image_volumn.visibility = View.GONE
        }

        abc()
    }

    override fun onDestroy() {
        e("fullscreen onDestroy")
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun abc() {
        var gz = GanZhi(DateTime(), solarTermMap)
        runOnUiThread {
            var res = StringBuffer()
            val str = "${gz.tianGanYear}${gz.diZhiYear} ${gz.tianGanMonth}${gz.diZhiMonth} ${gz.tianGanDay}${gz.diZhiDay} ${gz.tianGanHour}${gz.diZhiHour}"
            tv_ganzhi.text = str
            if (str.contains("子") && str.contains("午")) {
                res.append("子午相冲\t")
            }
            if (str.contains("丑") && str.contains("未")) {
                res.append("丑未相冲\t")
            }
            if (str.contains("寅") && str.contains("申")) {
                res.append("寅申相冲\t")
            }
            if (str.contains("卯") && str.contains("酉")) {
                res.append("卯酉相冲\t")
            }
            if (str.contains("辰") && str.contains("戌")) {
                res.append("辰戌相冲\t")
            }
            if (str.contains("巳") && str.contains("亥")) {
                res.append("巳亥相冲\t")
            }


            if (str.contains("子") && str.contains("未")) {
                res.append("子未相害\t")
            }
            if (str.contains("丑") && str.contains("午")) {
                res.append("丑午相害\t")
            }
            if (str.contains("寅") && str.contains("巳")) {
                res.append("寅巳相害\t")
            }
            if (str.contains("卯") && str.contains("辰")) {
                res.append("卯辰相害\t")
            }
            if (str.contains("申") && str.contains("亥")) {
                res.append("申亥相害\t")
            }
            if (str.contains("酉") && str.contains("戌")) {
                res.append("酉戌相害\t")
            }


//            子丑合，寅亥合，卯戌合，巳申合，午未合，辰酉合
            if (str.contains("子") && str.contains("丑")) {
                res.append("子丑合\t")
            }
            if (str.contains("寅") && str.contains("亥")) {
                res.append("寅亥合\t")
            }
            if (str.contains("卯") && str.contains("戌")) {
                res.append("卯戌合\t")
            }
            if (str.contains("巳") && str.contains("申")) {
                res.append("巳申合\t")
            }
            if (str.contains("午") && str.contains("未")) {
                res.append("午未合\t")
            }
            if (str.contains("辰") && str.contains("酉")) {
                res.append("辰酉合\t")
            }

//            申子辰合水，申生长刚开始,到子水最帝旺,辰土墓.
//            午寅戌合火，寅生长刚开始,到午火最帝旺,戌土墓.
//            巳酉丑合金，巳生长刚开始,到酉金最帝旺,丑土墓.
//            亥卯未合木，亥生长刚开始,到卯木最帝旺,未土墓
            if (str.contains("申") && str.contains("子")&& str.contains("辰")) {
                res.append("申子辰合水\t")
            }
            if (str.contains("寅") && str.contains("午")&& str.contains("戌")) {
                res.append("午寅戌合火\t")
            }
            if (str.contains("巳") && str.contains("酉")&& str.contains("丑")) {
                res.append("巳酉丑合金\t")
            }
            if (str.contains("亥") && str.contains("卯")&& str.contains("未")) {
                res.append("亥卯未合木\t")
            }


            if (str.contains("寅") && str.contains("巳")&& str.contains("申")) {
                res.append("寅巳申 无恩之刑\t")
            }
            if (str.contains("丑") && str.contains("戌")&& str.contains("未")) {
                res.append("丑戌未 持势之刑\t")
            }
//            if (str.contains("寅") && str.contains("巳")&& str.contains("申")) {
//                res.append("辰午酉亥 自刑")
//            }
            tv_info.text=res.toString()

        }
    }
    private fun startTimer() {
        try {
            timer = Timer()
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
//                    if (weekday != 7 && weekday != 1
//                            && (now.timeInMillis > startDate1.timeInMillis && now.timeInMillis < endDate1.timeInMillis
//                                    || now.timeInMillis > startDate2.timeInMillis && now.timeInMillis < endDate2.timeInMillis)) {
//                        toFundMonitor()
//                        return
//                    }

//                e("is alrm running : $isAlarmRunning")
                    try {
                        if (isAlarmRunning) {
                            runOnUiThread {
                                layout_root.setBackgroundResource(R.color.alarm_color)
                            }
                            if (now.timeInMillis >= targetTimeInMillis) {
//                            _Utils.ling(this@FullscreenActivity, R.raw.ding)
                                _Utils.speaker(this@ClockActivity, getString(R.string.speaker_alarm), 1.0f, 0.8f)
                                isAlarmRunning = false
                                runOnUiThread {
                                    layout_root.setBackgroundResource(R.color.b)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            textView_log.visibility = View.VISIBLE
                            textView_log.text = e.message
                        }
                    }


                    runOnUiThread {
                        try {
                            val lunar = Lunar(now)
                            val day = now.day.toString() + ""
//                        if (!isFundMonitor)

                            if(now.second==0&&now.minite==0){
                                abc()
                            }
                            tv_month.text = now.monthStr+"月"
                            if(tv_month.visibility==View.GONE){
                                tv_day.text = now.day.toString()
                            }else{
                                tv_day.text = now.dayStr
                            }
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
//                        if (lunar.day == 15 || lunar.day == 1) {
//                            layout_root.setBackgroundResource(R.color.a)
//                        } else {
//                            layout_root.setBackgroundResource(R.color.b)
//                        }
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
                                    if (lightLevel > 5) _Utils.speaker(this@ClockActivity, now.hour.toString() + "点", 1.0f, 1.0f)
                                    loadSexdateFromCloud()
                                }
                                if (weekday != 7 && weekday != 1) {
                                    if (now.hour == 8 && now.minite == 50) {
                                        if (weekday == 6) {
                                            _Utils.speaker(this@ClockActivity, "早上好，今天星期五！", 1.0f, 1.0f)
                                        } else {
                                            _Utils.ling(this@ClockActivity, R.raw.morning)
                                        }
                                    } else if (now.hour == 14 && now.minite == 50) {
                                        if (weekday == 6) {
                                            _Utils.speaker(this@ClockActivity, "今天星期五，周一再见！", 1.0f, 1.0f)
                                        } else {
                                            _Utils.ling(this@ClockActivity, R.raw.bye)
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
        }catch (e: Exception){
            runOnUiThread {
                textView_log.visibility = View.VISIBLE
                textView_log.text = e.message
            }
        }
    }

    private fun loadSexdateFromCloud() {
        _CloudUtils.getSetting(this, "0088", KEYS.wx_sex_date.toString(), object : CloudCallback {
            override fun excute(code: Int, result: Any) {
                e(code)
                e(result)
                when (code) {
                    0 -> {
                        sexDate = DateTime(result.toString().toLong())
                        refreshSexDays()
                    }
                }
                runOnUiThread {
                    Toast.makeText(this@ClockActivity, "更新完毕", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun e(log: Any) {
        Log.e("wangsc", log.toString())
        _Utils.runlog2file(log.toString(), null)
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
        e("fullscreen onStop")
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
                    e("电池电量：${level}")
                    tv_battery.text = "$level%"
                }
            }
        }
    }

    //region 动态权限申请
    var permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    var mNoPassedPermissionList: MutableList<String> = ArrayList()

    /**
     * 申请权限
     */
    fun requestPermissions(): Boolean {
        mNoPassedPermissionList.clear()
        for (i in permissions.indices) {
            if (ContextCompat.checkSelfPermission(this, permissions.get(i)) != PackageManager.PERMISSION_GRANTED) {
                com.wang17.myclock.e("权限名称 : ${permissions[i]} , 返回结果 : 未授权")
                mNoPassedPermissionList.add(permissions.get(i))
            }
        }
        if (mNoPassedPermissionList.isEmpty()) {
            return true
        } else {
            //请求权限方法
            val permissions = mNoPassedPermissionList.toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST)
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // 判断 是否仍然继续可以申请权限
                    val showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])
                    com.wang17.myclock.e("权限名称：${permissions[i]}，申请结果：${grantResults[i]}，是否可再次申请：${showRequestPermission}")
//                    if (!showRequestPermission) {
//                        AlertDialog.Builder(this).setMessage("有权限未授权，且被禁止申请，请手动授权。").setNegativeButton("知道了", DialogInterface.OnClickListener { dialog, which ->
//                            this.finish()
//                        }).show()
//                    }else{
                    AlertDialog.Builder(this).setMessage("授权失败").setNegativeButton("知道了", DialogInterface.OnClickListener { dialog, which ->
                        this.finish()
                    }).show()
//                    }
                    return
                }
            }

            initOnCreate()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //endregion


    companion object {
        private const val UI_ANIMATION_DELAY = 300
    }
}