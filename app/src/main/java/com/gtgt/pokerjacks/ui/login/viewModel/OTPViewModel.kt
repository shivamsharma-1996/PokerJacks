package com.gtgt.pokerjacks.ui.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.retrieveString
import com.gtgt.pokerjacks.extensions.showSnack
import com.gtgt.pokerjacks.ui.login.models.VerifyOtpResponse

class OTPViewModel : BaseViewModel() {

    private var _verifyOtpResponse: MutableLiveData<VerifyOtpResponse> = MutableLiveData()
    val verifyOtpResponse: LiveData<VerifyOtpResponse> = _verifyOtpResponse
    fun verifyOtp(otp: String) {
        val jsonInput = JsonObject()
        jsonInput.addProperty("otp", otp)
        jsonInput.addProperty("userUniqueId", retrieveString("USER_ID"))
        jsonInput.addProperty("deviceId", retrieveString("UNIQUE_ID"))
        apiServicesPlatform.verifyOtp(jsonInput).execute(activity, true){
            _verifyOtpResponse.value = it
        }
    }

    fun resendOtp(mobile: String) {
        val jsonInput = JsonObject()
        jsonInput.addProperty("mobile", mobile)
        jsonInput.addProperty("userId", retrieveString("USER_ID"))
        apiServicesPlatform.resendOTP(jsonInput).execute(activity, true) {
            activity?.showSnack(it.errorDesc)
        }
    }
}