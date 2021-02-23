package com.gtgt.pokerjacks.ui.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.widthHeightRaw
import kotlinx.android.synthetic.main.card_layout.view.*

fun createCard(
    parent: ViewGroup,
    card: Card?,
    width: Int,
    height: Int,
    position: Pair<Float, Float>? = null,
    animateToPosition: Pair<Float, Float>? = null,
    zIndex: Float = 0f,
    duration: Long = 300,
    rotationBy: Float = 0f,
    callback: ((View) -> Unit)? = null
): View {
    val context = parent.context!!
    val cardView = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, null)
    parent.addView(cardView)
    cardView.z = zIndex

    cardView.cardIv.apply {
        if (card == null) {
            cardView.alpha = 0f
        } else {
            setImageResource(
                resources.getIdentifier(
                    card.suite + if (card.suite.isEmpty())
                        card.faceValue else ("_" + card.faceValue),
                    "drawable",
                    context.packageName
                )
            )
        }
        widthHeightRaw(width, height)
    }

    cardView.apply {
        widthHeightRaw(width, height)

        if (position != null) {
            x = position.first
            y = position.second
        }

        if (animateToPosition != null) {
            animate().apply {
                interpolator = AccelerateDecelerateInterpolator()
                x(animateToPosition.first)
                y(animateToPosition.second)
                rotationBy(rotationBy)
                this.duration = duration
                withEndAction { callback?.invoke(cardView) }
                start()
            }
        } else {
            callback?.invoke(cardView)
        }
    }

    return cardView
}