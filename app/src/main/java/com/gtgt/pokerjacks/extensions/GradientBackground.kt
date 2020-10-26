package com.gtgt.pokerjacks.extensions

import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.res.ResourcesCompat

fun setGradientBackground(view: View, startColor: String, endColor: String, cornerRadius: Int = 0){
    val gd = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
    )
    gd.cornerRadius = cornerRadius.toFloat()

    view.setBackgroundDrawable(gd)
}

fun changeImageColor(
    startColor: String,
    endColor: String,
    originalBitmap: Bitmap
): Bitmap {
    val width = originalBitmap.width
    val height = originalBitmap.height
    val updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(updatedBitmap)
    canvas.drawBitmap(originalBitmap, 0f, 0f, null)

    val paint = Paint()
    val shader =
        LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            Color.parseColor(startColor),
            Color.parseColor(endColor),
            Shader.TileMode.CLAMP
        )
    paint.shader = shader
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)

    return updatedBitmap
}

fun View.changeDrawableGradientColor(startColor: String, endColor: String, drawable: Int) {
    val background = ResourcesCompat.getDrawable(resources, drawable, null) as GradientDrawable
    val colors =
        intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
    background.colors = colors
    background.orientation = GradientDrawable.Orientation.TOP_BOTTOM
    background.gradientType = GradientDrawable.LINEAR_GRADIENT
    background.mutate()
    this.background = background
}

fun View.changeDrawableColor(color: String, drawable: Int) {
    val background = ResourcesCompat.getDrawable(resources, drawable, null) as GradientDrawable
    background.setColor(Color.parseColor(color))
    background.orientation = GradientDrawable.Orientation.TOP_BOTTOM
    background.gradientType = GradientDrawable.LINEAR_GRADIENT
    background.mutate()
    this.background = background
}