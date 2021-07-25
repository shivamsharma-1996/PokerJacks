package com.gtgt.pokerjacks.extensions

import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.devs.vectorchildfinder.VectorChildFinder
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.ui.game.Card

var isColorDeckEnabled = false
    set(value) {
        field = value
        try {
            coloredImages.forEach {
                it.key.coloredCard(it.value)
            }
        }catch (e:Exception){
            log("Exception::isColorDeckEnabled", Log.getStackTraceString(e))
        }
    }

val suiteColorMap = mapOf(
    "spade" to Color.parseColor("#000000"),
    "heart" to Color.parseColor("#EC0000"),
    "club" to Color.parseColor("#005AD9"),
    "diamond" to Color.parseColor("#098D0E")
)

val coloredImages = mutableMapOf<ImageView, String>()

@BindingAdapter("shortForm")
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