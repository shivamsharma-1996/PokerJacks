package com.gtgt.pokerjacks.ui.offers.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.ui.offers.model.OpenScratchCardResponse

class ScratchCardViewModel : BaseViewModel() {
    private val _openScratchCardResponse = MutableLiveData<OpenScratchCardResponse>()
    val openScratchCardResponse: LiveData<OpenScratchCardResponse> = _openScratchCardResponse

    fun openScratchCard(bonusMasterId: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("bonusMasterId", bonusMasterId)
        apiInterfaceBonus.openScratchCard(jsonObject).execute(activity, true) {
            _openScratchCardResponse.value = it
        }
    }

}