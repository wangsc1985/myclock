package com.wang17.myclock

import android.util.Log
import java.math.BigDecimal

var targetTimeInMillis: Long = 0
var isAlarmRunning: Boolean = false

val scale = 8
val roundMode = BigDecimal.ROUND_DOWN
fun Int.toMyDecimal(): BigDecimal {
    return this.toBigDecimal().setScale(scale,roundMode)
}

fun Double.toMyDecimal(): BigDecimal {
    return this.toBigDecimal().setScale(scale,roundMode)
}
fun Float.toMyDecimal(): BigDecimal {
    return this.toBigDecimal().setScale(scale,roundMode)
}
fun Long.toMyDecimal(): BigDecimal {
    return this.toBigDecimal().setScale(scale,roundMode)
}
fun String.toMyDecimal(): BigDecimal {
    return this.toBigDecimal().setScale(scale,roundMode)
}
fun BigDecimal.setMyScale(): BigDecimal {
    return this.setScale(scale,roundMode)
}

fun String.Companion.concat(vararg strings: Any): String {
    val sb = StringBuilder()
    for (str in strings) {
        sb.append(str.toString())
    }
    return sb.toString()
}


fun e(log:Any){
    Log.e("wangsc",log.toString())
}
