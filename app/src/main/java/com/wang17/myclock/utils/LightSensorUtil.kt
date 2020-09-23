package com.wang17.myclock.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * @author bluewindtalker
 * @description 光线传感器工具
 * @date 2018/4/15-下午12:08
 */
object LightSensorUtil {
    fun getSenosrManager(context: Context): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    /**
     * 注册光线传感器监听器
     * @param sensorManager
     * @param listener
     */
    fun registerLightSensor(sensorManager: SensorManager?, listener: SensorEventListener?) {
        if (sensorManager == null || listener == null) {
            return
        }
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) // 获取光线传感器
        if (lightSensor != null) { // 光线传感器存在时
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL) // 注册事件监听
        }
    }

    /**
     * 反注册光线传感器监听器
     * @param sensorManager
     * @param listener
     */
    fun unregisterLightSensor(sensorManager: SensorManager?, listener: SensorEventListener?) {
        if (sensorManager == null || listener == null) {
            return
        }
        sensorManager.unregisterListener(listener)
    }
}