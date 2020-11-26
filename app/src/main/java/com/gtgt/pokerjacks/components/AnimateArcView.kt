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

    var drawColor = Color.parseColor("#ffdf01")
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
        tp.color = Color.parseColor("#ffdf01")
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
        }
    }

    fun startAnim(millisInFuture: Long, isGraceTime: Boolean = false) {
        remainingTimeInSec = millisInFuture / 1000
        this.isGraceTime = isGraceTime

        stopAnim()
        visibility = VISIBLE
        sweepAngle = -365f

        animateCounter = object : CountDownTimer(millisInFuture, millisInFuture / 365) {
            override fun onFinish() {
                sweepAngle = 0f
                invalidate()
            }

            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInSec = round(millisUntilFinished / 1000.0).toLong()
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