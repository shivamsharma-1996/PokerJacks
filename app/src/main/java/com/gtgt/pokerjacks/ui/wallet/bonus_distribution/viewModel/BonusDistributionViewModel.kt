package com.gtgt.pokerjacks.ui.wallet.bonus_distribution.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.jsonObject
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.showSnack
import com.gtgt.pokerjacks.ui.wallet.model.BonusHistory
import com.gtgt.pokerjacks.ui.wallet.model.GetBonusDisbursementsInfo

class BonusDistributionViewModel : BaseViewModel() {

    private val _bonusHistoryList = MutableLiveData<List<BonusHistory>>()
    val bonusHistoryList: LiveData<List<BonusHistory>> = _bonusHistoryList

    private val _stopApiCall: MutableLiveData<Boolean> = MutableLiveData()
    val stopApiCall: LiveData<Boolean> = _stopApiCall

    val bonusHistory = mutableListOf<BonusHistory>()

    var offset = 1
    val pageLimit = 20
    val loading = BonusHistory(
        isLoading = true
    )

    val totalReceivedAmount: MutableLiveData<String> = MutableLiveData()
    val totalDisbursedAmount: MutableLiveData<String> = MutableLiveData()
    val totalRemainingBonus: MutableLiveData<String> = MutableLiveData()
    val totalExpiredBonus: MutableLiveData<String> = MutableLiveData()

    fun getBonusHistory(
        showLoading: Boolean = true
    ) {
        apiServicesWallet.getBonusHistory(jsonObject("offset" to offset++))
            .execute(activity, showLoading) {
                if (it.success) {
                    if (offset <= 2) {
                        totalReceivedAmount.value = it.info.totalReceivedAmount
                        totalDisbursedAmount.value = it.info.totalDisbursedAmount
                        totalRemainingBonus.value = it.info.totalRemainingBonus
                        totalExpiredBonus.value = it.info.totalExpiredBonus
                    }
                    _stopApiCall.value =
                        it.info.bonusHistory.isNullOrEmpty() || it.info.bonusHistory.size < pageLimit
                    bonusHistory.remove(loading)
                    if (it.info.bonusHistory.isNotEmpty()) {
                        bonusHistory.addAll(it.info.bonusHistory)

                        if (it.info.bonusHistory.size == pageLimit)
                            bonusHistory.add(loading)
                    }
                    _bonusHistoryList.value = bonusHistory
                } else {
                    _bonusHistoryList.value = listOf()
                }
            }
    }

    private val _getBonusDisbursementsInfo: MutableLiveData<List<GetBonusDisbursementsInfo>> =
        MutableLiveData()
    val getBonusDisbursementsInfo: LiveData<List<GetBonusDisbursementsInfo>> =
        _getBonusDisbursementsInfo

    val bonusDisbursements = mutableListOf<GetBonusDisbursementsInfo>()

    val distLoading = GetBonusDisbursementsInfo(
        isFooter = true
    )

    private val datesSet = mutableSetOf<String>()

    private var isInNetworkCall = false

    fun getBonusDisbursements(
        showLoading: Boolean = true
    ) {
        if (offset == 1) {
            bonusDisbursements.clear()
            datesSet.clear()
        }
        apiServicesWallet.getBonusDisbursements(jsonObject("offset" to offset++))
            .execute(activity, showLoading) {
                isInNetworkCall = false
                if (it.success) {

                    _stopApiCall.value = it.info.isNullOrEmpty() || it.info.size < 20

                    bonusDisbursements.remove(distLoading)
                    if (it.info.isNotEmpty()) {
                        val currentDates = it.info.map {
                            it.creationDate
                        }.toSet()
                        currentDates.forEach { date ->
                            val trxsInDate = it.info.filter {
                                it.creationDate == date
                            }

                            if (trxsInDate.isNotEmpty()) {
                                if (!datesSet.contains(date))
                                    bonusDisbursements.add(
                                        GetBonusDisbursementsInfo(
                                            isHeader = true,
                                            creationDate = date
                                        )
                                    )

                                bonusDisbursements.addAll(trxsInDate)
                            }
                        }
                        datesSet.addAll(currentDates)

                        if (it.info.size == 15)
                            bonusDisbursements.add(distLoading)
                    }

                    _getBonusDisbursementsInfo.value = bonusDisbursements

                } else {
                    _getBonusDisbursementsInfo.value = listOf()
                    activity!!.showSnack(it.description)
                }
            }
    }
}