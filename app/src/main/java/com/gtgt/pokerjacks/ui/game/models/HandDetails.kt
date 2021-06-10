package com.gtgt.pokerjacks.ui.game.models

data class HandDetails(
    val cards: List<String>,
    val rankOrder: String,
    val tieBreaker: List<String>
)