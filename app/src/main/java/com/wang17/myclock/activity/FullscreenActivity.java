package com.wang17.myclock.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wang17.myclock.R;
import com.wang17.myclock.database.MarkDay;
import com.wang17.myclock.database.utils.DataContext;
import com.wang17.myclock.model.DateTime;
import com.wang17.myclock.model.Lunar;
import com.wang17.myclock.plugin.PercentCircleView;
import com.wang17.myclock.plugin.PercentCircleView1;
import com.wang17.myclock.utils.LightSensorUtil;
import com.wang17.myclock.utils._Session;
import com.wang17.myclock.utils._Utils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements SensorEventListener {


    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();

    DataContext dataContext;

    ConstraintLayout root;
    TextView tvDay;
    TextView tvLunarDay;
    TextView tvWeek;
    TextView tvTime;
    TextView tvMonth;
    TextView tvBattery;
    TextView tvMarkday;
    TextView tvSensor;
    PercentCircleView pcReligious;
    PercentCircleView1 pcSecond;

    boolean isDaytime=true,isNock=false,isLoaded=false;

    SensorManager sensorManager;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private Handler uiThreadHandler;
    Timer timer;
    float lightLevel;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        uiThreadHandler = new Handler();

        dataContext = new DataContext(this);

        root = findViewById(R.id.frameLayout);
        tvDay = findViewById(R.id.tv_day);
        tvLunarDay = findViewById(R.id.tv_lunar_day);
        tvWeek = findViewById(R.id.tv_week);
        tvTime = findViewById(R.id.tv_time);
        tvMonth = findViewById(R.id.tv_lunar_month);
        tvBattery = findViewById(R.id.tv_battery);
        tvMarkday = findViewById(R.id.tv_markday);
        tvSensor = findViewById(R.id.tv_sensor);
        pcReligious = findViewById(R.id.pc_religious);
        pcSecond = findViewById(R.id.pc_second);

        tvTime.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addMarkDayDialog(new DateTime());
                return true;
            }
        });

        final SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundPool.load(this, R.raw.second, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isLoaded=true;
            }
        });

        /**
         *初始化
         */
        refreshSexDays();

        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        tvBattery.setText(battery + "%");

        pcSecond.setMax(60);

        /**
         * 监听
         */
        MainReciver mainReciver = new MainReciver();
        IntentFilter filter = new IntentFilter();
        // 监控电量变化
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mainReciver, filter);

        //
        sensorManager = LightSensorUtil.getSenosrManager(this);
        LightSensorUtil.registerLightSensor(sensorManager, this);

        /**
         *
         */
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNock=!isNock;
            }
        });
        tvMarkday.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DateTime now = new DateTime();
                            Lunar lunar = new Lunar(now);
                            String day = now.getDay() + "";
                            tvDay.setText(day);
                            tvMonth.setText(lunar.getChinaMonthString());
                            tvLunarDay.setText(lunar.getChinaDayString());
                            tvTime.setText(now.toShortTimeString());
                            tvWeek.setText(now.getWeekDayStr());
                            pcSecond.setProgress(now.getSecond()==0?60:now.getSecond());

                            if(isNock&&isLoaded){
                                soundPool.play(1, 1f, 1f, 0, 0, 1);
                            }

                            /**
                             * 颜色
                             */
                            if (lunar.getDay() == 15 || lunar.getDay() == 1) {
                                root.setBackgroundResource(R.color.a);
                            } else {
                                root.setBackgroundResource(R.color.b);
                            }
                            if (now.get(Calendar.DAY_OF_WEEK) == 6) {
                                tvWeek.setTextColor(Color.RED);
                            } else {
                                tvWeek.setTextColor(Color.WHITE);
                            }


                            /**
                             * 每月初一、八、十四、十五、十八、二十三、二十四、二十八、二十九、三十
                             */

                            if (lunar.getDay() == 1 || lunar.getDay() == 8 || lunar.getDay() == 14 || lunar.getDay() == 15 || lunar.getDay() == 18 || lunar.getDay() == 23
                                    || lunar.getDay() == 24 || lunar.getDay() == 28 || lunar.getDay() == 29 || lunar.getDay() == 30) {
                                tvLunarDay.setTextColor(Color.RED);
                            } else {
                                tvLunarDay.setTextColor(Color.WHITE);
                            }
                            /**
                             * 十秒访问一次网络
                             */
//                            if (now.getSecond() % 10 == 0) {
//                                StockInfo stockInfo = new StockInfo();
//                                stockInfo.code = "000001";
//                                stockInfo.exchange = "sh";
//                                _SinaStockUtils.getStockInfo(stockInfo, new _SinaStockUtils.OnLoadStockInfoListener() {
//                                    @Override
//                                    public void onLoadFinished(final StockInfo info, String time) {
//                                        uiThreadHandler.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//
//                                                tvPrice.setText(new DecimalFormat("0.00").format(info.price));
////                                                tvIncrease.setText(new DecimalFormat("0.00%").format(info.increase));
//                                                if (info.increase > 0) {
//                                                    tvPrice.setTextColor(Color.RED);
////                                                    tvIncrease.setTextColor(Color.RED);
//                                                } else if (info.increase == 0) {
//                                                    tvPrice.setTextColor(Color.WHITE);
////                                                    tvIncrease.setTextColor(Color.WHITE);
//                                                } else {
//                                                    tvPrice.setTextColor(Color.CYAN);
////                                                    tvIncrease.setTextColor(Color.CYAN);
//                                                }
//                                            }
//                                        });
//                                    }
//                                });
//                            }

                            /**
                             * 整分获取戒期
                             */
                            if (now.getSecond() == 0) {
                                if (now.getMinite() == 0) {
                                    // 更新戒期
                                    refreshSexDays();
                                    // 整点报时
                                    if (lightLevel > 5)
                                        _Utils.speaker(FullscreenActivity.this, now.getHour() + "点");
                                }
                                int weekday = now.get(Calendar.DAY_OF_WEEK);
                                if (weekday != 7 && weekday != 1) {
                                    if (now.getHour() == 8 && now.getMinite() == 55) {
                                        _Utils.ling(FullscreenActivity.this,R.raw.morning);
                                    }
                                    else if (now.getHour() == 14 && now.getMinite() == 55) {
                                        _Utils.ling(FullscreenActivity.this,R.raw.bye);
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }

                    }
                });
            }
        }, 0, 1000);

    }


    public void addMarkDayDialog(DateTime dateTime) {

        try {
            View view = View.inflate(this, R.layout.inflate_dialog_date_picker, null);
            final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).setView(view).create();
            dialog.setTitle("设定最近时间");

            final int year = dateTime.getYear();
            int month = dateTime.getMonth();
            int maxDay = dateTime.getActualMaximum(Calendar.DAY_OF_MONTH);
            int day = dateTime.getDay();
            int hour = dateTime.getHour();

            String[] yearNumbers = new String[3];
            for (int i = year - 2; i <= year; i++) {
                yearNumbers[i - year + 2] = i + "年";
            }
            String[] monthNumbers = new String[12];
            for (int i = 0; i < 12; i++) {
                monthNumbers[i] = i + 1 + "月";
            }
            String[] dayNumbers = new String[31];
            for (int i = 0; i < 31; i++) {
                dayNumbers[i] = i + 1 + "日";
            }
            String[] hourNumbers = new String[24];
            for (int i = 0; i < 24; i++) {
                hourNumbers[i] = i + "点";
            }
            final NumberPicker npYear = (NumberPicker) view.findViewById(R.id.npYear);
            final NumberPicker npMonth = (NumberPicker) view.findViewById(R.id.npMonth);
            final NumberPicker npDay = (NumberPicker) view.findViewById(R.id.npDay);
            final NumberPicker npHour = (NumberPicker) view.findViewById(R.id.npHour);
            npYear.setMinValue(year - 2);
            npYear.setMaxValue(year);
            npYear.setValue(year);
            npYear.setDisplayedValues(yearNumbers);
            npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
            npMonth.setMinValue(1);
            npMonth.setMaxValue(12);
            npMonth.setDisplayedValues(monthNumbers);
            npMonth.setValue(month + 1);
            npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
            npDay.setMinValue(1);
            npDay.setMaxValue(31);
            npDay.setDisplayedValues(dayNumbers);
            npDay.setValue(day);
            npDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
            npHour.setMinValue(0);
            npHour.setMaxValue(23);
            npHour.setDisplayedValues(hourNumbers);
            npHour.setValue(hour);
            npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int y = npYear.getValue();
                    int m = npMonth.getValue() - 1;
                    int d = npDay.getValue();
                    int h = npHour.getValue();
                    DateTime selectedDateTime = new DateTime(y, m, d, h, 0, 0);
                    MarkDay markDay = new MarkDay(selectedDateTime, _Session.UUID_NULL, "");
                    dataContext.addMarkDay(markDay);

                    //
                    refreshSexDays();

                    dialog.dismiss();
                }
            });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置行房天数。
     */
    private void refreshSexDays() {
        DataContext dataContext = new DataContext(this);
        // 计时计日
        String text = "0";
        int progress = 0, progressMax = 24;

        UUID id = _Session.UUID_NULL;
        MarkDay lastMarkDay = dataContext.getLastMarkDay(id);
        if (lastMarkDay != null) {
            int have = (int) ((System.currentTimeMillis() - lastMarkDay.getDateTime().getTimeInMillis()) / 3600000);
            int day = have / 24;
            int hour = have % 24;
            progress = hour;
            text = day + "";
        }
        tvMarkday.setText(text);
        pcReligious.setMax(progressMax);
        pcReligious.setProgress(progress);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        LightSensorUtil.unregisterLightSensor(sensorManager, this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        delayedHide(100);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float light_strength = event.values[0];
            tvSensor.setText(new DecimalFormat("0").format(light_strength));
            lightLevel = light_strength;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class MainReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            switch (intent.getAction()) {
                case Intent.ACTION_BATTERY_CHANGED:
                    int level = intent.getIntExtra("level", 0);
                    tvBattery.setText(level + "%");
                    break;
            }
        }
    }
}