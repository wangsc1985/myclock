package com.wangsc.lib

import java.util.*

class MyClass {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {

            val now = DateTime()
            val startDate = DateTime()
            startDate.set(Calendar.HOUR_OF_DAY,9)
            startDate.set(Calendar.MINUTE,25)
            startDate.set(Calendar.SECOND,0)
            val endDate = DateTime()
            endDate.set(Calendar.HOUR_OF_DAY,15)
            endDate.set(Calendar.MINUTE,0)
            endDate.set(Calendar.SECOND,0)

            println(now.toLongDateTimeString())
            println(startDate.toLongDateTimeString())
            println(endDate.toLongDateTimeString())

            val weekday = now.get((Calendar.DAY_OF_WEEK))
            if (weekday != 7 && weekday != 1&&(now.timeInMillis>startDate.timeInMillis&&now.timeInMillis<endDate.timeInMillis)) {
                println("abc")
                return
            }
            println("ccc")
        }
    }
}