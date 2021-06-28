package com.gtgt.pokerjacks.ui.lobby.adapter

import android.widget.ImageView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.EventItemBinding
import com.gtgt.pokerjacks.extensions.diffChecker
import com.gtgt.pokerjacks.extensions.dpToPx
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.widthHeightRaw
import com.gtgt.pokerjacks.ui.game.Card
import com.gtgt.pokerjacks.ui.game.createCard
import com.gtgt.pokerjacks.ui.game.view.slot.slots2Positions
import com.gtgt.pokerjacks.ui.game.view.slot.slots6Positions
import com.gtgt.pokerjacks.ui.game.view.slot.slots6PositionsTable
import com.gtgt.pokerjacks.ui.game.view.slot.slots9Positions
import com.gtgt.pokerjacks.ui.lobby.model.LobbyTables
import com.gtgt.pokerjacks.utils.EasyBindingAdapter
import com.gtgt.pokerjacks.utils.SlotPositions
import com.gtgt.pokerjacks.utils.SlotPositions.*

var tableWidth = 0
var tableHeight = 0

var cardWidth = 0
var cardHeight = 0

val playerSize = dpToPx(24).toFloat()
val roundingSize = dpToPx(5).toFloat()

var playerPositions = mutableMapOf<SlotPositions, Pair<Float, Float>>()

class LobbyAdapter(val onClick: (LobbyTables.Info) -> Unit) :
    EasyBindingAdapter<LobbyTables.Info, EventItemBinding>(
        R.layout.event_item,
        diffChecker { old, new ->
            old.table_id == new.table_id
        }
    ) {
    override fun onBindViewHolder(holder: Holder<EventItemBinding>, position: Int) {
        val data = getItemAt(position)
        holder.binding.data = data

        holder.binding.root.onOneClick { onClick(data) }

        holder.binding.btnJoin.onOneClick { onClick(data) }

        holder.binding.table.postDelayed({
            if (tableWidth == 0) {
                tableWidth = holder.binding.table.width
                tableHeight = holder.binding.table.height - dpToPx(5)

                cardWidth = tableWidth / 8
                cardHeight = (cardWidth * 1.3).toInt()

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
            when (data.plan_details.max_players) {
                9 -> slots9Positions
                6 -> slots6PositionsTable
                else -> slots2Positions
            }.forEachIndexed { index, position ->
                val player = ImageView(holder.binding.root.context)
                holder.binding.table.addView(player)
                player.apply {
//                    setImageResource(if (data.activePlayers <= index) R.drawable.no_player else R.drawable.player)
                    setImageResource(if (data.empty_seats.contains(index + 1)) R.drawable.no_player else R.drawable.player)
                    widthHeightRaw(playerSize, playerSize)
                    x = playerPositions[position]!!.first
                    y = playerPositions[position]!!.second
                }
            }

            holder.binding.cards.removeAllViews()
            data.community_cards.forEach {
                createCard(holder.binding.cards, Card.fromShortForm(it), cardWidth, cardHeight)
            }
        }, if (tableWidth == 0) 100L else 0L)
    }
}