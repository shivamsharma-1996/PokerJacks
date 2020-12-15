package com.gtgt.pokerjacks.extensions

import android.animation.ValueAnimator
import com.devs.vectorchildfinder.VectorChildFinder


fun VectorChildFinder.animatePathColors(
    fromColor: Int,
    toColor: Int,
    vararg paths: String,
    callback: () -> Unit
) {
    ValueAnimator.ofArgb(fromColor, toColor).apply {
        duration = 500
        addUpdateListener { value ->
            paths.forEach { path ->
                if (findPathByName(path).fillColor != toColor) {
                    changePathColor(
                        path,
                        value.animatedValue as Int
                    )
                }
            }
            callback()
        }
        start()
    }
}

fun VectorChildFinder.changePathColor(path: String, color: Int) {
    findPathByName(path).fillColor = color
}

fun VectorChildFinder.changePathStrokeColor(path: String, color: Int) {
    findPathByName(path).strokeColor = color
}