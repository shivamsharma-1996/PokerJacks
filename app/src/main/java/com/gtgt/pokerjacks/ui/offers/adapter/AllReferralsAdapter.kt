package com.gtgt.pokerjacks.ui.offers.adapter

import android.content.Context
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.AllReferralItemsBinding
import com.gtgt.pokerjacks.ui.offers.model.ReferralDataInfo
import com.gtgt.pokerjacks.utils.EasyBindingAdapter

class AllReferralsAdapter(val context: Context) :
    EasyBindingAdapter<ReferralDataInfo, AllReferralItemsBinding>(
        R.layout.all_referral_items
    ) {

    override fun onBindViewHolder(holder: Holder<AllReferralItemsBinding>, position: Int) {
        holder.binding.data=getItemAt(position)
    }
}