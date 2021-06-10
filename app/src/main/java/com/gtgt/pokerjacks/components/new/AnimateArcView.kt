package com.gtgt.pokerjacks.components.new

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.log
import com.gtgt.pokerjacks.extensions.timeDiffWithServer
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlin.math.round

class AnimateArcView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var size = 0f
    private val p = Paint()
    private lateinit var rectF: RectF
    private var sweepAngle = 0f
    var animateCounter: CountDownTimer? = null
    var animateTimer: CountDownTimer? = null
    var isStopped = false

    var isGraceTime = false

    var startColor = Color.parseColor("#716200")
    var endColor = Color.parseColor("#e1c300")
        set(value) {
            field = value
            setGradient()
        }

    init {
        setBackgroundResource(R.drawable.vacant_slot_circle)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = layoutParams.width.toFloat()
        setGradient()
    }

    private fun setGradient() {
        if (size > 0) {
            rectF = RectF(0f, 0f, size, size)

            val gradient =
                if (isGraceTime) RadialGradient(
                    size / 2, size / 2, size / 2, Color.parseColor("#760000"),
                    Color.parseColor("#EC0000"), Shader.TileMode.CLAMP
                )
                else RadialGradient(
                    size / 2, size / 2, size / 2, startColor,
                    endColor, Shader.TileMode.CLAMP
                )
            p.isDither = true
            p.shader = gradient
        }
    }

    fun startAnim(millisInFuture: Long, isGraceTime: Boolean) {
        this.isGraceTime = isGraceTime

        stopAnim()
        visibility = VISIBLE
        sweepAngle = -365f

        animateCounter = object : CountDownTimer(millisInFuture, millisInFuture / 365) {
            override fun onFinish() {
                sweepAngle = 0f
//                visibility = GONE
//                onFinished?.invoke()
            }

            override fun onTick(millisUntilFinished: Long) {
                invalidate()
                sweepAngle++
            }
        }.start()
    }

    fun stopAnim() {
        visibility = GONE
        animateCounter?.cancel()
      //  animateTimer?.cancel()
        isStopped = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            canvas.drawArc(rectF, 270f, sweepAngle, true, p)
        } catch (ex: Exception) {

        }
    }

    fun animate(
        time: Long,
        graceTime: Long,
        animateArcView: AnimateArcView,
        tv: TextView,
        animateIndicator: Boolean,
        isGraceTime: Boolean
    ): CountDownTimer? {
        /*if (animateIndicator) {
            if (isGraceTime) {
                rootLayout.playerTurnIndication.setBackgroundColor(redColor)
                if (gameActivity.playSound) {
                    graceTimeSound.start()
                }
            } else {
                rootLayout.playerTurnIndication.setBackgroundColor(greenColor)
                if (graceTimeSound.isPlaying)
                    graceTimeSound.pause()
            }

            rootLayout.playerTurnIndication.visibility = VISIBLE
        } else {
            rootLayout.playerTurnIndication.visibility = GONE
        }*/

        if(animateTimer!=null){
            animateTimer!!.cancel()
        }
        val millisInFuture =
            (if (isGraceTime) graceTime else time) - (System.currentTimeMillis() - timeDiffWithServer)
        animateTimer = object :
            CustomCountDownTimer(millisInFuture, 1000) {
            override fun onStop() {
                tv.text = ""
                tv.visibility = GONE
                animateArcView.stopAnim()
//                rootLayout.playerTurnIndication.visibility = GONE
//                if (graceTimeSound.isPlaying)
//                    graceTimeSound.pause()
            }

            override fun onTick(millisUntilFinished: Long) {
                tv.text = (millisUntilFinished / 1000).toString()
                log("onTick", "onTick" + millisUntilFinished)
                if (animateIndicator) {
//                    rootLayout.playerTurnIndication.widthHeightRaw(
//                        (millisUntilFinished / millisInFuture.toDouble() * turnIndicationWidth).toInt()
//                    )
                }
            }

            override fun onFinish() {
                tv.text = ""
                tv.visibility = GONE
                animateArcView.stopAnim()
                log("onFinish", "onFinished")

                /*  rootLayout.playerTurnIndication.visibility = GONE

                  if (graceTimeSound.isPlaying)
                      graceTimeSound.pause()*/
                if (!isGraceTime && graceTime != 0L) {
                    animate(time, graceTime, animateArcView, tv, animateIndicator, true)
                    tv.visibility = VISIBLE
                }
            }
        }
        animateTimer?.start()
        animateArcView.startAnim(millisInFuture, isGraceTime)

        if (graceTime != 0L) {
            animateTimer = animateTimer
        }

        return animateTimer
    }
}