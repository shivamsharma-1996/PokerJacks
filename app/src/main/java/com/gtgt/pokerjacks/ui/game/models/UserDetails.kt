package com.gtgt.pokerjacks.ui.game.models

data class UserDetails(
    val _id: String,
    var auto_muck: Boolean,
    var auto_next_game: Boolean,
    var hand_strength: Boolean,
    val status: String,
    val user_name: String,
    val user_unique_id: String
)