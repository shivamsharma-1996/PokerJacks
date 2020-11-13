package com.gtgt.pokerjacks.ui.wallet.withdraw.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute

class WithdrawViewModel: BaseViewModel(){
    val _withdrawMoney: MutableLiveData<BaseModel> = MutableLiveData()
    val withdrawMoney: LiveData<BaseModel> = _withdrawMoney

    fun withdrawalmoney(detailId: String, withdrawAmount: Int) {
        val jsonObject= JsonObject()
        jsonObject.addProperty("bankId", detailId)
        jsonObject.addProperty("amount", withdrawAmount)
        apiServicesPayment.withdrawalmoney(jsonObject).execute(activity, true) {
            _withdrawMoney.value = it
        }
    }
}