package com.gtgt.pokerjacks.ui.login.viewModel

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.getServiceProviderName
import com.gtgt.pokerjacks.extensions.retrieveString
import com.gtgt.pokerjacks.ui.login.models.CreateMPinResponse

class MPinViewModel : BaseViewModel() {

    private var _mPinResponse: MutableLiveData<CreateMPinResponse> = MutableLiveData()
    val mPinResponse: LiveData<CreateMPinResponse> = _mPinResponse

    fun createMPIN(mpin: String) {
        val jsonInput = JsonObject()
        jsonInput.addProperty("userId", retrieveString("USER_ID"))
        jsonInput.addProperty("deviceId", retrieveString("UNIQUE_ID"))
        jsonInput.addProperty("mpin", mpin)
        jsonInput.addProperty("latitude", "17.8176")
        jsonInput.addProperty("longitude", "78.4145")
        jsonInput.addProperty("fcmId", retrieveString("FCM_ID"))
        jsonInput.addProperty("aaid", retrieveString("AAID"))
        jsonInput.addProperty("os_type", "Android")
        jsonInput.addProperty("deviceName", Build.MANUFACTURER)
        jsonInput.addProperty("osVersion", Build.VERSION.RELEASE)
        jsonInput.addProperty("serviceProvider", activity!!.getServiceProviderName())

        apiServicesPlatform.createMPIN(jsonInput).execute(activity, true){
            _mPinResponse.value = it
        }
    }

    fun resetMPIN(mpin: String) {
        val jsonInput = JsonObject()
        jsonInput.addProperty("userUniqueId", retrieveString("USER_ID"))
        jsonInput.addProperty("deviceId", retrieveString("UNIQUE_ID"))
        jsonInput.addProperty("newMPIN", mpin)
        apiServicesPlatform.resetMPIN(jsonInput).execute(activity, true) {
            _mPinResponse.value = it
        }
    }
}