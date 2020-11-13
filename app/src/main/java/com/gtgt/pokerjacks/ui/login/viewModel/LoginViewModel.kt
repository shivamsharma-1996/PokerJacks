package com.gtgt.pokerjacks.ui.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.retrieveString
import com.gtgt.pokerjacks.ui.login.models.LoginResponse

class LoginViewModel : BaseViewModel() {

    private var _login: MutableLiveData<LoginResponse> = MutableLiveData()
    val login: LiveData<LoginResponse> = _login

    fun login(mpin: String) {
        val jsonInput = JsonObject()
        jsonInput.addProperty("mobile", retrieveString("MOBILE"))
        jsonInput.addProperty("deviceId", retrieveString("UNIQUE_ID"))
        jsonInput.addProperty("mpin", mpin)
        jsonInput.addProperty("latitude", "17.8176")
        jsonInput.addProperty("longitude", "78.4145")
        jsonInput.addProperty("fcmId", retrieveString("FCM_ID"))
        jsonInput.addProperty("aaid", retrieveString("AAID"))
        jsonInput.addProperty("os_type", "Android")

        apiServicesPlatform.login(jsonInput).execute(activity, true){
            _login.value = it
        }
    }

    fun forgotMPIN(): LiveData<BaseModel> {
        val response: MutableLiveData<BaseModel> = MutableLiveData()
        val jsonInput = JsonObject()
        jsonInput.addProperty("userUniqueId", retrieveString("USER_ID"))
        jsonInput.addProperty("deviceId", retrieveString("UNIQUE_ID"))
        jsonInput.addProperty("mobile", retrieveString("MOBILE"))
        apiServicesPlatform.forgotMPIN(jsonInput).execute(activity, true) {
            response.value = it
        }
        return response
    }
}