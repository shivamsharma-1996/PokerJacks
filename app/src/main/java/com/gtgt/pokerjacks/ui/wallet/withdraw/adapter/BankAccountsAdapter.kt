package com.gtgt.pokerjacks.ui.wallet.withdraw.adapter

import android.content.Context
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.BankAccountsItemsBinding
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.profile.manage_account.model.GetBankDetailsInfo
import com.gtgt.pokerjacks.utils.EasyBindingAdapter

class BankAccountsAdapter(
    val context: Context,
    defaultPos: Int=0,
    val onChangeSelection: (Int) -> Unit
) :
    EasyBindingAdapter<GetBankDetailsInfo, BankAccountsItemsBinding>(
        R.layout.bank_accounts_items
    ) {

    private var lastCheckedPos=defaultPos

    override fun onBindViewHolder(holder: Holder<BankAccountsItemsBinding>, position: Int) {
        val getBankDetailsInfo=getItemAt(position)
        holder.binding.data=getBankDetailsInfo
        holder.binding.rbAccounts.isChecked = lastCheckedPos == holder.adapterPosition

        holder.binding.root.onOneClick {
            lastCheckedPos = holder.adapterPosition
            notifyDataSetChanged()
            onChangeSelection(lastCheckedPos)
        }

        when (getBankDetailsInfo.AccountNameByType) {
            "UPI" -> holder.binding.ivAccount.setImageDrawable(context.getDrawable(R.drawable.upi_icon))
            "PAYTM" -> holder.binding.ivAccount.setImageDrawable(context.getDrawable(R.drawable.ic_paytm_logo))
            else -> holder.binding.ivAccount.setImageDrawable(context.getDrawable(R.drawable.bank_icon))
        }
    }
}