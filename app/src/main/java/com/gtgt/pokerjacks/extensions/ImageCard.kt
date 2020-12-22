package com.gtgt.pokerjacks.extensions

import android.graphics.Color
import android.widget.ImageView
import com.devs.vectorchildfinder.VectorChildFinder
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.ui.game.Card

var isColorDeckEnabled = false
    set(value) {
        field = value
        coloredImages.forEach {
            it.key.coloredCard(it.value)
        }
    }

val suiteColorMap = mapOf(
    "spade" to Color.parseColor("#583d72"),
    "heart" to Color.parseColor("#ff4646"),
    "club" to Color.parseColor("#6155a6"),
    "diamond" to Color.parseColor("#fc8621")
)

val coloredImages = mutableMapOf<ImageView, String>()

fun ImageView.coloredCard(shortForm: String) {
    coloredImages[this] = shortForm

    val card = Card.fromShortForm(shortForm)
    if (card.faceValue == "deck_card") {
        setImageResource(R.drawable.deck_card)
    } else {
        val vector = VectorChildFinder(
            context,
            MyApplication.instance!!.resources.getIdentifier(
                card.suite + if (card.suite.isEmpty())
                    card.faceValue else ("_" + card.faceValue),
                "drawable",
                MyApplication.instance!!.packageName
            ), this
        )

        if (isColorDeckEnabled) {
            vector.changePathColor("p1", suiteColorMap[card.suite]!!)
            vector.changePathColor("p2", suiteColorMap[card.suite]!!)
            vector.changePathColor("p3", suiteColorMap[card.suite]!!)
            vector.changePathColor("p4", suiteColorMap[card.suite]!!)
            vector.changePathColor("p5", suiteColorMap[card.suite]!!)
        }
        invalidate()
    }
}