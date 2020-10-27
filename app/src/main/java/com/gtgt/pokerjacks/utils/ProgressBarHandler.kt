package com.gtgt.pokerjacks.utils

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.ViewGroup

class ProgressBarHandler(private val activity: Activity) {
    var handler: Handler? = null
    val hideRunnable = Runnable { hide() }

    init {
        /*activity.runOnUiThreadIfRunning {
            handler = Handler()
        }*/
    }

    private lateinit var view: View
    val layout = activity.findViewById<View>(android.R.id.content).rootView as ViewGroup


    fun show() {
        /*activity.runOnUiThreadIfRunning {
            try {
                handler?.let { it.postDelayed(hideRunnable, 10000) }
                activity.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                view = activity.layoutInflater.inflate(R.layout.progressbar, null)
                layout.addView(view)
                view.visibility = View.VISIBLE
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }*/
    }

    fun hide() {
        /*activity.runOnUiThreadIfRunning {
            try {
//                handler?.removeCallbacks(hideRunnable)
                handler?.let { it.removeCallbacks(hideRunnable) }
                activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (::view.isInitialized)
                    layout.removeView(view)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }*/
    }
}