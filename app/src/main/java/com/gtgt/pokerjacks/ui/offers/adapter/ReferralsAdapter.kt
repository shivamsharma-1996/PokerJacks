package com.gtgt.pokerjacks.ui.offers.adapter

import android.content.Context
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.ReferralItemsBinding
import com.gtgt.pokerjacks.ui.offers.model.ReferralDataInfo
import com.gtgt.pokerjacks.utils.EasyBindingAdapter

class ReferralsAdapter(val context: Context) :
    EasyBindingAdapter<ReferralDataInfo, ReferralItemsBinding>(
        R.layout.referral_items
    ) {

    override fun onBindViewHolder(holder: Holder<ReferralItemsBinding>, position: Int) {
        holder.binding.data=getItemAt(position)
    }
}