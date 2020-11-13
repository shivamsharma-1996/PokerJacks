package com.gtgt.pokerjacks.ui.wallet.bonus_distribution.adapter

import android.content.Context
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.RecentDistrobutionHeaderBinding
import com.gtgt.pokerjacks.databinding.RecentDistrobutionItemsBinding
import com.gtgt.pokerjacks.extensions.diffChecker
import com.gtgt.pokerjacks.ui.wallet.model.GetBonusDisbursementsInfo
import com.gtgt.pokerjacks.utils.EasyBindingSectionAdapter

class RecentDistributionAdapter(context: Context) :
    EasyBindingSectionAdapter<GetBonusDisbursementsInfo, RecentDistrobutionHeaderBinding, RecentDistrobutionItemsBinding>(
        R.layout.recent_distrobution_header,
        R.layout.recent_distrobution_items,
        {
            it.isHeader
        },
        diffChecker { old, new ->
            old.gameId == new.gameId
        }, {
            it.isFooter
        }
    ) {
    override fun onBindHeaderHolder(holder: HeaderHolder<RecentDistrobutionHeaderBinding>, position: Int) {
        val data = getItem(position)
        holder.binding.data = data
    }

    override fun onBindItemHolder(holder: ItemHolder<RecentDistrobutionItemsBinding>, position: Int) {
        val data = getItem(position)
        holder.binding.data = data
    }
}