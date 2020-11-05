package com.gtgt.pokerjacks.ui.offers.adapter

import android.content.Context
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.BonusOfferItemsBinding
import com.gtgt.pokerjacks.extensions.changeDrawableGradientColor
import com.gtgt.pokerjacks.extensions.diffChecker
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.offers.model.BonusCodes
import com.gtgt.pokerjacks.utils.EasyBindingAdapter
import kotlinx.android.synthetic.main.bonus_offer_items.view.*

class BonusOffersAdapter(
    val context: Context,
    val onBonusItemClicked: (Int) -> Unit
) :
    EasyBindingAdapter<BonusCodes, BonusOfferItemsBinding>(
        R.layout.bonus_offer_items,
        diffChecker { old, new ->
            old.bonus_code_id == new.bonus_code_id
        }
    ) {

    override fun onBindViewHolder(holder: Holder<BonusOfferItemsBinding>, position: Int) {
        val bonusCodes = getItemAt(position)
        holder.binding.data = bonusCodes

        holder.binding.root.onOneClick {
            onBonusItemClicked(position)
        }

        holder.itemView.fl_bonusOfferCard.changeDrawableGradientColor(
            bonusCodes.color_code1,
            bonusCodes.color_code2,
            R.drawable.blue_bg
        )

        /*val vector =
            VectorChildFinder(context, R.drawable.bonus_badge, holder.itemView.iv_bonusBadge)
        val path1: VectorDrawableCompat.VFullPath = vector.findPathByName("demo")
        path1.fillColor = Color.RED*/
    }
}