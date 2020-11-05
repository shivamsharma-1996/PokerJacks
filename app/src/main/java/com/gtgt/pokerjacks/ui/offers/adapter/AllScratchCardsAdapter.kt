package com.gtgt.pokerjacks.ui.offers.adapter

import android.content.Context
import android.view.View
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.AllScratchCardItemsBinding
import com.gtgt.pokerjacks.extensions.diffChecker
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.offers.model.TotalScratchCards
import com.gtgt.pokerjacks.utils.EasyBindingAdapter

class AllScratchCardsAdapter(
    val context: Context,
    val onItemClicked: (View, TotalScratchCards) -> Unit
) :
    EasyBindingAdapter<TotalScratchCards, AllScratchCardItemsBinding>(
        R.layout.all_scratch_card_items,
        diffChecker { old, new ->
            old.user_bonus_master_id == new.user_bonus_master_id
        }, {
            it.isFooter
        }
    ) {

    override fun onBindViewHolder(holder: Holder<AllScratchCardItemsBinding>, position: Int) {
        val data = getItemAt(position)
        holder.binding.data = data

        holder.itemView.onOneClick {
            if (data.card_type != "ClosedInactive") {
                onItemClicked(holder.itemView, data)
            }
        }
    }
}