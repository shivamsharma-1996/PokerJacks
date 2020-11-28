package com.gtgt.pokerjacks.ui.side_nav.refer_earn.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.jsonObject
import com.gtgt.pokerjacks.base.AnyModel
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.ui.side_nav.refer_earn.model.GetReferralCode

class ReferAndEarnViewModel : BaseViewModel() {

    fun getReferralCode(): LiveData<GetReferralCode> {
        val _getReferralCode: MutableLiveData<GetReferralCode> = MutableLiveData()
        apiServicesPlatform.getReferralCode().execute(activity, true){
            _getReferralCode.value = it
        }
        return _getReferralCode
    }

    fun checkReferralEligible(): LiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        apiServicesPlatform.checkReferralEligible().execute(activity) {
            response.value = it.success
        }
        return response
    }

    fun applyReferralCode(code: String): LiveData<AnyModel> {
        val response = MutableLiveData<AnyModel>()
        apiInterfaceBonus.applyReferralCode(jsonObject("code" to code)).execute(activity) {
            response.value = it
        }
        return response
    }
}