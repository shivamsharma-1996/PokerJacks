package com.gtgt.pokerjacks.ui.game

import com.gtgt.pokerjacks.MyApplication

val suites = listOf("spade", "heart", "club", "diamond")
val cardFaces = listOf(
    "a", "2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k"
)

private fun suiteShortForm(suite: String) = when (suite) {
    "spade" -> "S"
    "heart" -> "H"
    "club" -> "C"
    "diamond" -> "D"
    else -> ""
}

fun suiteActualForm(shortForm: String) = when (shortForm) {
    "S" -> "spade"
    "H" -> "heart"
    "C" -> "club"
    "D" -> "diamond"
    else -> ""
}

private fun cardFaceShortForm(cardFace: String) = when (cardFace) {
    "a" -> "A"
    "10" -> "T"
    "j" -> "J"
    "q" -> "Q"
    "k" -> "K"
    "joker" -> "JO"
    else -> cardFace
}

private fun cardFaceActualForm(shortForm: String) = when (shortForm) {
    "A" -> "a"
    "T" -> "10"
    "J" -> "j"
    "Q" -> "q"
    "K" -> "k"
    "JO" -> "joker"
    else -> shortForm
}

class Card(
    val faceValue: String,
    val suite: String
) {
    val shortForm: String
        get() = cardFaceShortForm(faceValue) + suiteShortForm(suite)

    override fun equals(other: Any?): Boolean {
        return other is Card && (other.faceValue == faceValue && other.suite == suite)
    }

    companion object {
        fun fromShortForm(shortForm: String): Card {
            return if (shortForm == "JO") {
                Card("joker", "")
            } else {
                try {
                    Card(
                        cardFaceActualForm(shortForm[0].toString()),
                        suiteActualForm(shortForm[1].toString())
                    )
                } catch (ex: Exception) {
                    Card("deck_card", "")
                }
            }
        }

        fun getResource(shortForm: String): Int {
            val card = fromShortForm(shortForm)
            return MyApplication.instance!!.resources.getIdentifier(
                card.suite + if (card.suite.isEmpty())
                    card.faceValue else ("_" + card.faceValue),
                "drawable",
                MyApplication.instance!!.packageName
            )
        }
    }
}