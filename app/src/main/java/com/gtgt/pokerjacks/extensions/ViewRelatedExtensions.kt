package com.gtgt.pokerjacks.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.util.*
import java.util.concurrent.TimeUnit


fun View.onRendered(callback: (View) -> Unit) {
    var isCallBackSent = false
    val viewTreeObserver = viewTreeObserver
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (width > 0) {
                    if (viewTreeObserver.isAlive) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                    if (!isCallBackSent) {
                        isCallBackSent = true
                        callback(this@onRendered)
                    }
                }
            }
        })
    }
}

fun EditText.limitLength(maxLength: Int) {
    filters = arrayOf(InputFilter.LengthFilter(maxLength))
}

fun Activity.getDeviceHeight(): String {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels.toString()
}

fun Activity.getDeviceWidth(): String {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels.toString()
}

fun View.margins(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): View {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(dip(left), dip(top), dip(right), dip(bottom))
        requestLayout()
    }
    return this
}

fun View.marginBottom(margin: Int): View {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, dip(margin))
        requestLayout()
    }
    return this
}

fun View.marginBottomRaw(margin: Int): View {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, margin)
        requestLayout()
    }
    return this
}

fun View.marginLeftRaw(margin: Int): View {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(margin, p.topMargin, p.rightMargin, p.bottomMargin)
        requestLayout()
    }
    return this
}

fun View.marginsRaw(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): View {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        requestLayout()
    }
    return this
}

fun View.padding(all: Int = 0, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): View {
    if (all != 0) {
        setPadding(all, all, all, all)
    } else {
        setPadding(left, top, right, bottom)
    }
    return this
}

fun View.widthHeight(width: Int = 0, height: Int = 0): View {
    val params = layoutParams
    if (params != null) {
        if (width != 0)
            params.width = dip(width)
        if (height != 0)
            params.height = dip(height)

        layoutParams = params
    }
    return this
}

fun View.widthHeightRaw(width: Int = 0, height: Int = 0): View {
    val params = layoutParams
    if (params != null) {
        if (width != 0)
            params.width = width
        if (height != 0)
            params.height = height


        layoutParams = params
    }
    return this
}

fun View.scale(factor: Float) {
    scaleX = factor
    scaleY = factor
    widthHeightRaw(width * factor, height * factor)
}

fun View.widthHeightRaw(width: Float = 0f, height: Float = 0f): View {
    val params = layoutParams
    if (params != null) {
        if (width != 0f)
            params.width = width.toInt()
        if (height != 0f)
            params.height = height.toInt()


        layoutParams = params
    }
    return this
}

fun View.dip(dp: Int): Int {
    return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun View.dip(dp: Float): Int {
    return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun dpToPxFloat(dp: Int): Float {
    return (dp * Resources.getSystem().displayMetrics.density)
}

fun pxToDp(px: Float): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun spToPx(sp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        Resources.getSystem().displayMetrics
    )
}

fun spToPx(sp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp.toFloat(),
        Resources.getSystem().displayMetrics
    )
}

fun pxToSp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.scaledDensity).toInt()
}

fun pxToSp(px: Float): Int {
    return (px / Resources.getSystem().displayMetrics.scaledDensity).toInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun pxToDpFloat(px: Int): Float {
    return (px / Resources.getSystem().displayMetrics.density)
}

fun Activity.getWindowHeight(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    // val width = displayMetrics.widthPixels
    return height
}

fun getViewWidth(view: View): Int {
    val viewTreeObserver = view.viewTreeObserver
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
    return (view.width) / 3
}

fun getViewheight(view: View): Int {
    val viewTreeObserver = view.viewTreeObserver
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
    return (view.height) / 3
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun ImageView.loadURL(url: String?, placeholder: Int = 0) {
    if (placeholder == 0) {
        Glide.with(this).load(url).into(this)
    } else {
        Glide.with(this).load(url).placeholder(placeholder).into(this)
    }
}

@SuppressLint("SetTextI18n")
fun TextView.setDaysAgoTimer(prefix: String, time: Long) {
    try {
        val past = Date(time)
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

        when {
            seconds < 60 -> this.text =
                "$prefix - $seconds seconds ago"
            minutes < 60 -> this.text =
                "$prefix - $minutes minutes ago"
            hours < 24 -> this.text =
                "$prefix - $hours hours ago"
            else -> this.text =
                "$prefix - $days days ago"
        }
    } catch (j: Exception) {
        j.printStackTrace()
    }
}

fun TextView.makeTextUnderline() {
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun EditText.placeCursorToEnd() {
    this.setSelection(this.text.length)
}

fun TextView.SpannableString(
    spannableString: SpannableString,
    color: String,
    startIndex: Int = 0,
    endIndex: Int = spannableString.length
) {
    spannableString.setSpan(
        ForegroundColorSpan(Color.parseColor(color)),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    text = spannableString
}