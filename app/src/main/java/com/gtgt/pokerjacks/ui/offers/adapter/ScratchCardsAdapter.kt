package com.gtgt.pokerjacks.ui.offers.adapter

import android.content.Context
import android.view.View
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.ScratchCardItemsBinding
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.offers.model.TotalScratchCards
import com.gtgt.pokerjacks.utils.EasyBindingAdapter

class ScratchCardsAdapter(
    val context: Context,
    val onItemClicked: (View, TotalScratchCards) -> Unit
) :
    EasyBindingAdapter<TotalScratchCards, ScratchCardItemsBinding>(
        R.layout.scratch_card_items
    ) {

    override fun onBindViewHolder(holder: Holder<ScratchCardItemsBinding>, position: Int) {
        holder.binding.data = getItemAt(position)

        holder.itemView.onOneClick {
            if (getItemAt(position).card_type != "ClosedInactive") {
                onItemClicked(holder.itemView, getItemAt(position))
            }
        }
    }
}