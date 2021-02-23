package com.gtgt.pokerjacks.utils

import android.os.CountDownTimer

abstract class CustomCountDownTimer(val millisInFuture: Long, val countDownInterval: Long) :
    CountDownTimer(millisInFuture, countDownInterval) {

    fun stop() {
        cancel()
        onStop()
    }

    abstract fun onStop()
}