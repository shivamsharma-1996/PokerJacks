package com.gtgt.pokerjacks.ui.game.models

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.dpToPx
import com.gtgt.pokerjacks.extensions.dpToPxFloat

class Theme(
    val name: String,
    val bg: Int,
    val table: Int,
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
                btn2
            )
        }

    val popupDrawable
        get() = gradientBg(gradientStart, gradientEnd, 10).apply {
            setStroke(
                dpToPx(1),
                Color.parseColor("#FFFFFF")
            )
        }
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
        table = R.drawable.blue_table,
        gradientStart = Color.parseColor("#1D8DE1"),
        gradientEnd = Color.parseColor("#003964"),
        light = Color.parseColor("#1D8DE2"),
        dark = Color.parseColor("#002C52"),
        btn1 = Color.parseColor("#E1C300"),
        btn2 = Color.parseColor("#716200")
    ), Theme(
        name = "red",
        bg = R.drawable.red_table_bg,
        table = R.drawable.red_table,
        gradientStart = Color.parseColor("#B40505"),
        gradientEnd = Color.parseColor("#5A0303"),
        light = Color.parseColor("#B40505"),
        dark = Color.parseColor("#570101"),
        btn1 = Color.parseColor("#E1C300"),
        btn2 = Color.parseColor("#716200")
    ), Theme(
        name = "red_green",
        bg = R.drawable.green_table_bg,
        table = R.drawable.red_table_old,
        gradientStart = Color.parseColor("#B40505"),
        gradientEnd = Color.parseColor("#5A0303"),
        light = Color.parseColor("#B40505"),
        dark = Color.parseColor("#570101"),
        btn1 = Color.parseColor("#E1C300"),
        btn2 = Color.parseColor("#716200")
    ), Theme(
        name = "ace23",
        bg = R.drawable.ace23_table_bg,
        table = R.drawable.ace23_table,
        gradientStart = Color.parseColor("#C71F25"),
        gradientEnd = Color.parseColor("#741316"),
        light = Color.parseColor("#C71F25"),
        dark = Color.parseColor("#570101"),
        btn1 = Color.parseColor("#E1C300"),
        btn2 = Color.parseColor("#716200")
    ), Theme(
        name = "rc",
        bg = R.drawable.rc_table_bg,
        table = R.drawable.rc_table,
        gradientStart = Color.parseColor("#009F34"),
        gradientEnd = Color.parseColor("#095222"),
        light = Color.parseColor("#009F34"),
        dark = Color.parseColor("#033810"),
        btn1 = Color.parseColor("#AA0B11"),
        btn2 = Color.parseColor("#620407"),
        secondaryTextColor = Color.parseColor("#620407")
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