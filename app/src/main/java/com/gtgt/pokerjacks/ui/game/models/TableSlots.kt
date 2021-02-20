package com.gtgt.pokerjacks.ui.game.models

import com.google.gson.JsonElement

enum class TableSlotStatus(status: String) {
    ACTIVE("ACTIVE"),
    WAITING("WAITING"),
    LEFT("LEFT"),
    INACTIVE("INACTIVE"),
    VACANT("VACANT"),
    FOLD("FOLD"),
}

data class TableSlot(
    val _id: String,
    val game_id: String,
    val game_user: Boolean,
    val in_watch_mode: Boolean,
    val inplay_amount: Double,
    val join_id: String,
    val ref_txn_id: String,
    val seat_no: Int,
    val status: String,
    val table_id: String,
    val user_name: String,
    val user_unique_id: String,
    var user: GameUser?,
    val sitout_details: JsonElement
) {
    override fun equals(other: Any?): Boolean {
        return other is TableSlot && other.seat_no == seat_no
    }

    fun next(slots: List<TableSlot>): TableSlot {
        val myPosition = slots.indexOf(this)
        return try {
            slots[myPosition + 1]
        } catch (ex: Exception) {
            slots[0]
        }
    }

    fun previous(slots: List<TableSlot>): TableSlot {
        val myPosition = slots.indexOf(this)
        return try {
            slots[myPosition - 1]
        } catch (ex: Exception) {
            slots.last()
        }
    }

}