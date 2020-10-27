package com.gtgt.pokerjacks.base

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.view.ViewGroup
import com.gtgt.pokerjacks.base.BaseActivity

abstract class ScreenOnActivity : BaseActivity() {
    protected var mActivityTopLevelView: ViewGroup? = null
//
    private val DISABLE_KEEP_SCREEN_ON = 0
    private val SCREEN_ON_TIME_MS = 1000 * 60 * 3
    private val TAG = "ScreenOnActivity"

    override fun onResume() {
        super.onResume()
//        Log.d(TAG, "onResume")
        setScreenOn(true)
    }

    override fun onPause() {
        super.onPause()
//        Log.d(TAG, "onPause")
        setScreenOn(false)
    }

    override fun onUserInteraction() {

        super.onUserInteraction()
//        Log.d(TAG, "onUserInteraction")
        setScreenOn(true)
    }

    private fun setScreenOn(enabled: Boolean) {
        // Remove any previous delayed messages
//        Log.d(TAG, "setScreenOn to $enabled")
//        mHandler.removeMessages(DISABLE_KEEP_SCREEN_ON)

        if (enabled) {
            // Send a new delayed message to disable the screen on
            // NOTE: After we call setKeepScreenOn(false) the screen will still stay on for
            // the system SCREEN_OFF_TIMEOUT. Thus, we subtract it out from our desired time.
            val systemScreenTimeout =
                Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 0)
            val totalDelay = SCREEN_ON_TIME_MS - systemScreenTimeout
            if (totalDelay > 0) {
                mActivityTopLevelView?.keepScreenOn = true
                /*Log.d(
                    TAG,
                    "Send delayed msg DISABLE_KEEP_SCREEN_ON with delay $totalDelay"
                )*/
                mHandler.sendEmptyMessageDelayed(DISABLE_KEEP_SCREEN_ON, totalDelay.toLong())
            }
        } else {
            mActivityTopLevelView?.keepScreenOn = false
        }
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == DISABLE_KEEP_SCREEN_ON) {
                setScreenOn(false)
            }
        }
    }
}