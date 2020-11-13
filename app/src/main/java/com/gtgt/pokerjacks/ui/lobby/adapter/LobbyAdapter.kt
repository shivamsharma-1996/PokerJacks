package com.gtgt.pokerjacks.ui.lobby.adapter

import android.widget.ImageView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.EventItemBinding
import com.gtgt.pokerjacks.extensions.dpToPx
import com.gtgt.pokerjacks.extensions.widthHeightRaw
import com.gtgt.pokerjacks.ui.lobby.model.Event
import com.gtgt.pokerjacks.utils.EasyBindingAdapter
import com.gtgt.pokerjacks.utils.PlayerPositions
import com.gtgt.pokerjacks.utils.PlayerPositions.*

var tableWidth = 0
var tableHeight = 0
val playerSize = dpToPx(22).toFloat()
val roundingSize = dpToPx(5).toFloat()

var playerPositions = mutableMapOf<PlayerPositions, Pair<Float, Float>>()
var player9Positions = listOf(
    LEFT_TOP_CENTER, LEFT_TOP, RIGHT_TOP, RIGHT_TOP_CENTER,
    RIGHT_BOTTOM_CENTER, RIGHT_BOTTOM, BOTTOM_CENTER, LEFT_BOTTOM, LEFT_BOTTOM_CENTER
)

var player6Positions = listOf(
    LEFT_CENTER, LEFT_TOP, RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM, LEFT_BOTTOM
)

val player2Positions = listOf(TOP_CENTER, BOTTOM_CENTER)

class LobbyAdapter : EasyBindingAdapter<Event, EventItemBinding>(
    R.layout.event_item//,
    /*diffChecker { old, new ->
        old.== new .
    }*/
) {


    override fun onBindViewHolder(holder: Holder<EventItemBinding>, position: Int) {
        val data = getItemAt(position)
        holder.binding.data = data

        holder.binding.table.postDelayed({
            if (tableWidth == 0) {
                tableWidth = holder.binding.table.width
                tableHeight = holder.binding.table.height

                playerPositions[LEFT_TOP_CENTER] = playerSize / 2 to playerSize
                playerPositions[LEFT_TOP] = 2 * playerSize to 0f
                playerPositions[TOP_CENTER] = (tableWidth - playerSize) / 2 to 0f
                playerPositions[RIGHT_TOP] = tableWidth - 3 * playerSize to 0f
                playerPositions[RIGHT_TOP_CENTER] = tableWidth - 1.5f * playerSize to playerSize

                playerPositions[RIGHT_BOTTOM_CENTER] =
                    tableWidth - 1.5f * playerSize to tableHeight - 2 * playerSize
                playerPositions[RIGHT_BOTTOM] =
                    tableWidth - 3 * playerSize to tableHeight - playerSize
                playerPositions[BOTTOM_CENTER] =
                    (tableWidth - playerSize) / 2 to tableHeight - playerSize
                playerPositions[LEFT_BOTTOM] = 2 * playerSize to tableHeight - playerSize
                playerPositions[LEFT_BOTTOM_CENTER] =
                    playerSize / 2 to tableHeight - 2f * playerSize

                playerPositions[LEFT_CENTER] = roundingSize to (tableHeight - playerSize) / 2
                playerPositions[RIGHT_CENTER] =
                    tableWidth - playerSize - roundingSize to (tableHeight - playerSize) / 2
            }

            holder.binding.table.removeAllViews()
            when (data.totalPlayers) {
                9 -> player9Positions
                6 -> player6Positions
                else -> player2Positions
            }.forEachIndexed { index, position ->
                val player = ImageView(holder.binding.root.context)
                holder.binding.table.addView(player)
                player.apply {
                    setImageResource(if (data.filledPlayers <= index) R.drawable.no_player else R.drawable.player)
                    widthHeightRaw(playerSize, playerSize)
                    x = playerPositions[position]!!.first
                    y = playerPositions[position]!!.second
                }
            }

        }, if (tableWidth == 0) 100L else 0L)

    }
}