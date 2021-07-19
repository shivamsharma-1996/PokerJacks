package com.gtgt.pokerjacks.ui.game.models

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import java.io.Serializable

data class TableUserStats(
    val tableUserStatsItem: ArrayList<TableUserStatsItem>
) : Serializable

data class TableUserStatsHeader(
    val title_user: String = "User",
    val title_buy_in: String = "Buy-In",
    val title_winnings: String = "Winnings"
)

data class TableUserStatsItem(
    val buyin_amount: Int,
    val game_id: String,
    val inplay_amount: Double,
    val join_id: String,
    val seat_no: Int,
    val status: String,
    val table_id: String,
    val user_name: String,
    val user_unique_id: String,
    val winnings: Double
) : Serializable {
    val formattedWinnings: String
        get() = when {
                winnings < 0.0 -> {
                    StringBuilder().append("-").append("₹").append((winnings * -1).toString())
                        .toString()
                }
                else -> {
                    "₹$winnings"
                }
            }


    val winningAmtColor: Int
        get() = when {
            winnings < 0.0 -> {
                Color.parseColor("#ed1561")
            }
            winnings > 0.0 -> {
                Color.parseColor("#41c46e")
            }
            else -> {
                Color.parseColor("#f1f1f1")
            }
        }
}



