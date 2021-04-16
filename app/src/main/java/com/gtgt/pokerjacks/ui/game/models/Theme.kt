package com.gtgt.pokerjacks.ui.game.models

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.dpToPx
import com.gtgt.pokerjacks.extensions.dpToPxFloat

class Theme(
    val name: String,
    val bg: Int,
    val landscapeTable: Int,
    val portraitTable: Int,
    val gradientStart: Int,
    val gradientEnd: Int,
    val light: Int,
    val dark: Int,
    val btn1: Int,
    val btn2: Int,
    val secondaryTextColor: Int = Color.parseColor("#FFFFFF")
) {
    val bgDrawable get() = gradientBg(gradientStart, gradientEnd)
    val btnDrawable get() = gradientBg(btn1, btn2, 5)

    val iconSolidDrawable get() = gradientBg(btn1, btn2, 17)
    val darkDrawable get() = gradientBg(dark, dark, 5)
    val textDrawable
        get() = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT)
        ).apply {
            cornerRadius = dpToPxFloat(6)
            setStroke(
                dpToPx(1),
                light
            )
        }

    val popupDrawable
        get() = gradientBg(gradientStart, gradientEnd, 10)/*.apply {
            setStroke(
                dpToPx(1),
                Color.parseColor("#FFFFFF")
            )
        }*/
    val popupBtn
        get() = gradientBg(dark, dark, 10).apply {
            setStroke(
                dpToPx(1),
                Color.parseColor("#FFFFFF")
            )
        }
}

val themes = listOf(
    Theme(
        name = "blue",
        bg = R.drawable.blue_table_bg,
        landscapeTable = R.drawable.landscape_blue_table,
        portraitTable = R.drawable.portrait_blue_table,
        gradientStart = Color.parseColor("#fd055c"),
        gradientEnd = Color.parseColor("#930034"),
        light = Color.parseColor("#ed1561"),
        dark = Color.parseColor("#930034"),
        btn1 = Color.parseColor("#fd055c"),
        btn2 = Color.parseColor("#930034")
    ),
    Theme(
        name = "red",
        bg = R.drawable.blue_table_bg,
        landscapeTable = R.drawable.landscape_red_table,
        portraitTable = R.drawable.portrait_red_table,
        gradientStart = Color.parseColor("#ccb205"),
        gradientEnd = Color.parseColor("#5f5302"),
        light = Color.parseColor("#b59f0d"),
        dark = Color.parseColor("#5f5302"),
        btn1 = Color.parseColor("#ccb205"),
        btn2 = Color.parseColor("#5f5302")
    ),
    Theme(
        name = "yellow",
        bg = R.drawable.blue_table_bg,
        landscapeTable = R.drawable.landscape_yellow_table,
        portraitTable = R.drawable.portrait_yellow_table,
        gradientStart = Color.parseColor("#c3c34b"),
        gradientEnd = Color.parseColor("#626226"),
        light = Color.parseColor("#c3c34b"),
        dark = Color.parseColor("#626226"),
        btn1 = Color.parseColor("#c3c34b"),
        btn2 = Color.parseColor("#626226")
    ),
    Theme(
        name = "green",
        bg = R.drawable.blue_table_bg,
        landscapeTable = R.drawable.landscape_green_table,
        portraitTable = R.drawable.portrait_green_table,
        gradientStart = Color.parseColor("#fd055c"),
        gradientEnd = Color.parseColor("#930034"),
        light = Color.parseColor("#ed1561"),
        dark = Color.parseColor("#930034"),
        btn1 = Color.parseColor("#fd055c"),
        btn2 = Color.parseColor("#930034")
    )
)

fun gradientBg(startColor: Int, endColor: Int, cornerRadius: Int = 0): GradientDrawable {
    val gd = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(startColor, endColor)
    )
    gd.shape = GradientDrawable.RECTANGLE
    gd.gradientType = GradientDrawable.LINEAR_GRADIENT
    gd.cornerRadius = dpToPxFloat(cornerRadius)

    return gd
}