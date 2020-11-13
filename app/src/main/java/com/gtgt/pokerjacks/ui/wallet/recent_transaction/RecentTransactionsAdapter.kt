package com.gtgt.pokerjacks.ui.wallet.recent_transaction

import android.content.Context
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.TransactionHeaderBinding
import com.gtgt.pokerjacks.databinding.TransactionItemBinding
import com.gtgt.pokerjacks.extensions.diffChecker
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.wallet.wallet.RecentTransactionInfo
import com.gtgt.pokerjacks.utils.EasyBindingSectionAdapter

class RecentTransactionsAdapter(
    val context: Context,
    val onItemClicked: (RecentTransactionInfo) -> Unit
) :
    EasyBindingSectionAdapter<RecentTransactionInfo, TransactionHeaderBinding, TransactionItemBinding>(
        R.layout.transaction_header,
        R.layout.transaction_item,
        {
            it.isHeader
        },
        diffChecker { old, new ->
            old.user_txn_id == new.user_txn_id
        }, {
            it.isFooter
        }
    ) {
    override fun onBindHeaderHolder(holder: HeaderHolder<TransactionHeaderBinding>, position: Int) {
        val transaction = getItem(position)
        holder.binding.transaction = transaction
    }

    override fun onBindItemHolder(holder: ItemHolder<TransactionItemBinding>, position: Int) {
        val transaction = getItem(position)
        holder.binding.transaction = transaction

        holder.binding.root.onOneClick {
            onItemClicked(transaction)
        }
    }
}