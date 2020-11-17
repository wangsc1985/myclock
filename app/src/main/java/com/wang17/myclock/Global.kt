package com.wang17.myclock

import android.util.Log


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
