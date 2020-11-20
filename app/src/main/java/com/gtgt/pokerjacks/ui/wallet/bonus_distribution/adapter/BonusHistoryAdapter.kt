package com.gtgt.pokerjacks.ui.wallet.bonus_distribution.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.AllBonusHistoryItemsBinding
import com.gtgt.pokerjacks.extensions.diffChecker
import com.gtgt.pokerjacks.utils.EasyBindingAdapter
import com.gtgt.pokerjacks.ui.wallet.model.BonusHistory
import java.text.SimpleDateFormat
import java.util.*

class BonusHistoryAdapter(context: Context) :
    EasyBindingAdapter<BonusHistory, AllBonusHistoryItemsBinding>(
        R.layout.all_bonus_history_items,
        diffChecker { old, new ->
            old.bonusCode == new.bonusCode
        }, {
            it.isLoading
        }
    ) {

    val today = Date()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder<AllBonusHistoryItemsBinding>, position: Int) {
        val data = getItemAt(position)
        holder.binding.data = data

        if (data.bonusValue == data.utilizedAmount) {
            holder.binding.tvDaysLeft.text = "Utilized"
            holder.binding.tvDaysLeft.setTextColor(Color.parseColor("#FFCE35"))
        } else {
            val bonusExpiryDate =
                SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(data.bonusExpiryDate)
            val diff = bonusExpiryDate!!.time - today.time
            val numOfDaysRemain = (diff / (1000 * 60 * 60 * 24)).toInt()
            val hoursRemain = (diff / (1000 * 60 * 60)).toInt()
            val minutesRemain = (diff / (1000 * 60)).toInt()
            if (numOfDaysRemain <= 0 && hoursRemain <= 0 && minutesRemain <= 0) {
                holder.binding.tvDaysLeft.text = "Expired"
                holder.binding.tvDaysLeft.setTextColor(Color.parseColor("#EC0000"))
            } else {
                if (numOfDaysRemain == 0) {
                    if (hoursRemain <= 0) {
                        holder.binding.tvDaysLeft.text = "$minutesRemain minutes left"
                    } else {
                        holder.binding.tvDaysLeft.text = "$hoursRemain hours left"
                    }
                } else {
                    holder.binding.tvDaysLeft.text = "$numOfDaysRemain days left"
                }
                holder.binding.tvDaysLeft.setTextColor(Color.parseColor("#4188ed"))
            }
        }
    }

    fun daysBetween(endDate: String): Int {
        val bonusExpiryDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate)
        val today = Date()
        val diff = bonusExpiryDate!!.time - today.time
        val numOfYear = (diff / (1000 * 60 * 60 * 24) / 365).toInt()
        val numOfDays = (diff / (1000 * 60 * 60 * 24)).toInt()
        val hours = (diff / (1000 * 60 * 60)).toInt()
        val minutes = (diff / (1000 * 60)).toInt()
        val seconds = (diff / 1000).toInt()

        return numOfDays
    }
}