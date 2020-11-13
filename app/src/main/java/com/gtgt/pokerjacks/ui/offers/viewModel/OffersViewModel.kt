package com.gtgt.pokerjacks.ui.offers.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.ui.offers.model.*

class OffersViewModel : BaseViewModel() {
    private val _offersResponse = MutableLiveData<BonusOffersResponse>()
    val offersResponse: LiveData<BonusOffersResponse> = _offersResponse

    private val _scratchCardResponse = MutableLiveData<List<TotalScratchCards>>()
    val scratchCardResponse: LiveData<List<TotalScratchCards>> = _scratchCardResponse

    private val _stopApiCall: MutableLiveData<Boolean> = MutableLiveData()
    val stopApiCall: LiveData<Boolean> = _stopApiCall

    val scratchCards = mutableListOf<TotalScratchCards>()

    var offset = 1
    val pageLimit = 20
    val loading = TotalScratchCards(
        isFooter = true
    )

    private val _referralResponse = MutableLiveData<ReferralDataResponse>()
    val referralResponse: LiveData<ReferralDataResponse> = _referralResponse

    private val _applyPromoResponse = MutableLiveData<ApplyPromoCodeResponse>()
    val applyPromoResponse: LiveData<ApplyPromoCodeResponse> = _applyPromoResponse

    private val _scratchCardList = MutableLiveData<ScratchCardResponse>()
    val scratchCardList: LiveData<ScratchCardResponse> = _scratchCardList

    private val _totalAmount: MutableLiveData<String> = MutableLiveData()
    val totalAmount: LiveData<String> = _totalAmount

    fun getUserOffers(showLoading: Boolean = true) {
        apiInterfaceBonus.getUserOffers().execute(activity, showLoading) {
            _offersResponse.value = it
        }
    }

    fun getScritchCardsList(showLoading: Boolean = true) {
        apiInterfaceBonus.getUserScratchCards(jsonObject("offset" to 1))
            .execute(activity, showLoading) {
                _scratchCardList.value = it
            }
    }

    fun getScritchCards(showLoading: Boolean = true) {
        if (offset == 1) {
            scratchCards.clear()
        }
        apiInterfaceBonus.getUserScratchCards(
            jsonObject("offset" to offset++)
        )
            .execute(activity, showLoading) {
                if (it.success) {
                    _totalAmount.value = it.info.totalAmount ?: "0"
                    _stopApiCall.value =
                        it.info.totalScratchCards!!.size < pageLimit || it.info.totalScratchCards.isNullOrEmpty()
                    scratchCards.remove(loading)
                    if (it.info.totalScratchCards.isNotEmpty()) {
                        scratchCards.addAll(it.info.totalScratchCards)

                        if (it.info.totalScratchCards.size == pageLimit)
                            scratchCards.add(loading)
                    }
                    _scratchCardResponse.value = scratchCards
                } else {
                    _scratchCardResponse.value = listOf()
                }
            }
    }

    fun getUserReferralData(showLoading: Boolean = true) {
        apiInterfaceBonus.getUserReferralData().execute(activity, showLoading) {
            _referralResponse.value = it
        }
    }

    fun applyPromoCode(code: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("code", code)
        apiInterfaceBonus.applyPromoCode(jsonObject).execute(activity, true) {
            _applyPromoResponse.value = it
        }
    }

}