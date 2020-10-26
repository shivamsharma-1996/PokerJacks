package com.gtgt.pokerjacks.utils

import android.os.SystemClock
import android.view.View

abstract class OnOneClickListener(private val minClockInterval: Long) : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime > minClockInterval) {
            lastClickTime = currentTime
            onOneClick(v)
        }
    }

    abstract fun onOneClick(v: View)
}
