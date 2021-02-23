package com.gtgt.pokerjacks.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.CountDownTimer
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.gtgt.pokerjacks.extensions.dip
import com.gtgt.pokerjacks.extensions.log
import com.gtgt.pokerjacks.extensions.timeDiffWithServer
import com.gtgt.pokerjacks.ui.game.models.PlayerTurn
import kotlin.math.round

class AnimateArcView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var size = 0f
    private val p = Paint()
    private val pb = Paint()
    private val tp = TextPaint()
    private lateinit var rectF: RectF
    private var sweepAngle = 0f
    private var remainingTimeInSec = 0L
    var animateCounter: CountDownTimer? = null

    var isGraceTime = false

    var normalColor = Color.parseColor("#ffdf01")
    var graceColor = Color.parseColor("#bb2205")

    var drawColor = normalColor
        set(value) {
            field = value
            setDrawColor()
        }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        log("width", size)
        tp.textSize = dip(size / 8)
        tp.isAntiAlias = true
        tp.textAlign = Paint.Align.CENTER

        pb.color = Color.parseColor("#3e536f")
        pb.isDither = true
        pb.strokeWidth = size / 8
        pb.style = Paint.Style.STROKE
        pb.strokeCap = Paint.Cap.ROUND

        setDrawColor()
    }

    private fun setDrawColor() {
        if (size > 0) {
            rectF = RectF(10f, 10f, size - 10, size - 10)

            p.color = drawColor
            p.isDither = true
            p.strokeWidth = size / 11
            p.style = Paint.Style.STROKE
            p.strokeCap = Paint.Cap.ROUND

            tp.color = drawColor
        }
    }

    fun startAnim(playerTurn: PlayerTurn, onFinished: (() -> Unit)? = null) {
        drawColor = normalColor
        startAnim(playerTurn.player_action_timer - (System.currentTimeMillis() - timeDiffWithServer)) {
            drawColor = graceColor
            startAnim(
                playerTurn.player_grace_timer - (System.currentTimeMillis() - timeDiffWithServer),
                true
            ) {
                visibility = GONE
                onFinished?.invoke()
            }
        }
    }

    fun startAnim(
        millisInFuture: Long,
        isGraceTime: Boolean = false,
        onFinished: (() -> Unit)? = null
    ) {
        remainingTimeInSec = millisInFuture / 1000
        this.isGraceTime = isGraceTime

        stopAnim()
        visibility = VISIBLE
        sweepAngle = -365f

        animateCounter = object : CountDownTimer(millisInFuture, millisInFuture / 365) {
            override fun onFinish() {
                sweepAngle = 0f
                invalidate()
                onFinished?.invoke()
            }

            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInSec = round(millisUntilFinished / 1000.0).toLong()
//                log("remainingTimeInSec", remainingTimeInSec)
                sweepAngle++
                invalidate()
            }
        }.start()
    }

    fun stopAnim() {
        visibility = GONE
        animateCounter?.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            canvas.drawArc(rectF, 0f, 360f, false, pb)

            canvas.drawArc(rectF, 270f, -sweepAngle, false, p)

            val xPos = size / 2f
            val yPos = (size / 2f - (tp.descent() + tp.ascent()) / 2f)


            canvas.drawText(remainingTimeInSec.toString(), xPos, yPos, tp)

        } catch (ex: Exception) {

        }
    }
}