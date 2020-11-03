package com.gtgt.pokerjacks.ui.profile.manage_account.adapter

import android.content.Context
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.databinding.ExistingBankAccountsItemsBinding
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.profile.manage_account.model.GetBankDetailsInfo
import com.gtgt.pokerjacks.utils.EasyBindingAdapter

class ExistingBankAccountsAdapter(
    val context: Context,
    val onDidNotReceiveClciked: (GetBankDetailsInfo) -> Unit,
    val onConfirmClicked: (GetBankDetailsInfo) -> Unit,
    val onDeleteClicked: (GetBankDetailsInfo) -> Unit
) :
    EasyBindingAdapter<GetBankDetailsInfo, ExistingBankAccountsItemsBinding>(
        R.layout.existing_bank_accounts_items
    ) {

    override fun onBindViewHolder(holder: Holder<ExistingBankAccountsItemsBinding>, position: Int) {
        val getBankDetailsInfo = getItemAt(position)
        holder.binding.data = getBankDetailsInfo

        holder.binding.ivDeleteAcc.onOneClick {
            onDeleteClicked(getBankDetailsInfo)
        }

        holder.binding.btnInvalidOk.onOneClick {
            onDeleteClicked(getBankDetailsInfo)
        }

        holder.binding.btnConfNotReceived.onOneClick {
            onDidNotReceiveClciked(getBankDetailsInfo)
        }

        holder.binding.btnConfirmAcc.onOneClick {
            onConfirmClicked(getBankDetailsInfo)
        }

        when (getBankDetailsInfo.AccountNameByType) {
            "UPI" -> holder.binding.ivAccount.setImageDrawable(context.getDrawable(R.drawable.upi_icon))
            "PAYTM" -> holder.binding.ivAccount.setImageDrawable(context.getDrawable(R.drawable.ic_paytm_logo))
            else -> holder.binding.ivAccount.setImageDrawable(context.getDrawable(R.drawable.bank_icon))
        }
    }
}