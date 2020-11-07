package com.gtgt.pokerjacks.ui.offers.adapter

import android.content.Context
import android.graphics.Color
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.CouponsItemsBinding
import com.gtgt.pokerjacks.extensions.changeDrawableGradientColor
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.offers.model.BonusCodes
import com.gtgt.pokerjacks.utils.EasyBindingAdapter
import kotlinx.android.synthetic.main.bonus_offer_items.view.*

class CouponsAdapter(
    val context: Context,
    val onApplyClicked: (BonusCodes) -> Unit,
    val onItemClicked: (Int) -> Unit
) : EasyBindingAdapter<BonusCodes, CouponsItemsBinding>(
    R.layout.coupons_items
) {

    override fun onBindViewHolder(holder: Holder<CouponsItemsBinding>, position: Int) {
        val bonusCodes = getItemAt(position)
        holder.binding.data = bonusCodes

        /*holder.itemView.btn_applyCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(bonusCodes.color_code2))*/

        holder.binding.btnApplyCoupon.onOneClick {
            onApplyClicked(bonusCodes)
        }

        holder.binding.root.onOneClick {
            onItemClicked(position)
        }

        holder.binding.btnApplyCoupon.setBackgroundColor(Color.parseColor(bonusCodes.color_code2))

        holder.itemView.fl_bonusOfferCard.changeDrawableGradientColor(
            bonusCodes.color_code1,
            bonusCodes.color_code2,
            R.drawable.blue_bg
        )
    }
}