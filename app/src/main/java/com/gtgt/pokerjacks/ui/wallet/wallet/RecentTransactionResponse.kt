package com.gtgt.pokerjacks.ui.wallet.wallet

import com.gtgt.pokerjacks.extensions.toDecimalFormat
import java.io.Serializable

data class RecentTransactionResponse(
    val success: Boolean,
    val errorCode: Int,
    val errorDesc: String,
    val info: List<RecentTransactionInfo>
) : Serializable

data class RecentTransactionInfo(
    val user_txn_id: String = "",
    val ref_txn_id: String = "",
    val username: String = "",
    val amount: String = "",
    val initial_balance: String = "",
    val closing_balance: String = "",
    val txn_type: String = "",
    val txn_mode: String = "", // Credit
    val sub_txn_type: String = "",
    val description: String = "",
    val creationDate: String = "",
    val creationTime: String = "",
    val image: String? = "",
    val mode: String? = "",
    val account: String? = "",
    val status: String? = "",
    var isHeader: Boolean = false,
    var isFooter: Boolean = false
) : Serializable {

    val formatted_closing_balance: String
        get() = closing_balance.toDecimalFormat()

    val formattedAccount: String
        get() {
            return "*******" + account?.let {
                it.substring(it.length - 4)
            }
        }
}