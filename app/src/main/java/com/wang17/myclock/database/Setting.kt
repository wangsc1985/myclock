package com.wang17.myclock.database

import com.wang17.myclock.model.DateTime

/**
 * Created by 阿弥陀佛 on 2015/6/30.
 */
class Setting(var name: Any, var value: Any) {


    val boolean: Boolean
        get() = java.lang.Boolean.parseBoolean(value.toString())
    val int: Int
        get() = string.toInt()
    val long: Long
        get() = string.toLong()
    val dateTime: DateTime
        get() = DateTime(long)
    val float: Float
        get() = string.toFloat()
    val double: Double
        get() = string.toDouble()

    val string: String
        get() = value.toString()

    enum class KEYS {
        wx_request_code, wx_sex_date,battery,speaker_screen_off,is_stock_speak
    }
}