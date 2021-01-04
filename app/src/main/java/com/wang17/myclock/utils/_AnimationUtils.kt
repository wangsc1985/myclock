package com.wang17.myclock.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.*
import com.wang17.myclock.R

/**
 * Created by Administrator on 2017/6/30.
 */
object _AnimationUtils {
    fun heartBeat(myView: View) {
        val scaleX = ObjectAnimator.ofFloat(myView, "scaleX", 1f, 1.3f, 1f)
        scaleX.duration = 800
        scaleX.start()
        val scaleY = ObjectAnimator.ofFloat(myView, "scaleY", 1f, 1.3f, 1f)
        scaleY.duration = 800
        scaleY.start()
    }
}