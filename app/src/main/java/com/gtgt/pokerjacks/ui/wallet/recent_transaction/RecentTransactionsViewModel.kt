package com.gtgt.pokerjacks.ui.wallet.recent_transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.showSnack
import com.gtgt.pokerjacks.ui.wallet.wallet.RecentTransactionInfo

class RecentTransactionsViewModel : BaseViewModel() {
    private val _transactionDetails: MutableLiveData<List<RecentTransactionInfo>> =
        MutableLiveData()
    val transactionDetails: LiveData<List<RecentTransactionInfo>> = _transactionDetails


    private val _stopApiCall: MutableLiveData<Boolean> = MutableLiveData()
    val stopApiCall: LiveData<Boolean> = _stopApiCall

    val transactions = mutableListOf<RecentTransactionInfo>()

    var offset = 1

    val loading = RecentTransactionInfo(
        isFooter = true
    )

    private val datesSet = mutableSetOf<String>()

    private var isInNetworkCall = false
    fun getWalletTransactionDetails(
        filterList: List<String>?,
        showLoading: Boolean = true
    ) {
        if (offset == 1) {
            transactions.clear()
            datesSet.clear()
        }
        if (!isInNetworkCall) {
            isInNetworkCall = true

            val jsonObject = JsonObject()
            val jsonArray = JsonArray()
            filterList?.forEach {
                jsonArray.add(it)
            }
            jsonObject.add("filter", jsonArray)
            jsonObject.addProperty("offset", this.offset++)

            apiServicesWallet.getRecentTransactions(
                jsonObject
            ).execute(activity, showLoading) { recentTransaction ->
                isInNetworkCall = false
                if (recentTransaction.success) {

                    _stopApiCall.value = recentTransaction.info.size < 15

                    transactions.remove(loading)
                    if (recentTransaction.info.isNotEmpty()) {
                        val currentDates = recentTransaction.info.map {
                            it.creationDate
                        }.toSet()
                        currentDates.forEach { date ->
                            val trxsInDate = recentTransaction.info.filter {
                                it.creationDate == date
                            }

                            if (trxsInDate.isNotEmpty()) {
                                if (!datesSet.contains(date))
                                    transactions.add(
                                        RecentTransactionInfo(
                                            isHeader = true,
                                            creationDate = date
                                        )
                                    )

                                transactions.addAll(trxsInDate)
                            }
                        }
                        datesSet.addAll(currentDates)

                        if (recentTransaction.info.size == 15)
                            transactions.add(loading)
                    }

                    _transactionDetails.value = transactions

                } else {
                    _transactionDetails.value = listOf()
                    activity!!.showSnack(recentTransaction.errorDesc)
                }
            }
        }
    }

    private val _pointsInPlayTransactionsResponse: MutableLiveData<PointsInPlayTransactionsResponse> =
        MutableLiveData()
    val pointsInPlayTransactionsResponse: LiveData<PointsInPlayTransactionsResponse> = _pointsInPlayTransactionsResponse

    fun getPointsInPlayTransactions(txnId: String) {
        apiServicesWallet.getPointsInPlayTransactions(jsonObject("txnId" to txnId))
            .execute(activity, true) {
                _pointsInPlayTransactionsResponse.value=it
            }
    }
}