package com.gtgt.pokerjacks.ui.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.getAdId
import com.gtgt.pokerjacks.extensions.getFcmToken
import com.gtgt.pokerjacks.extensions.uniqueId
import com.gtgt.pokerjacks.ui.login.models.CreateUserByMobileResponse
import com.gtgt.pokerjacks.utils.Constants

class RegistrationViewModel : BaseViewModel() {
    var _createUserByMobileResponse: MutableLiveData<CreateUserByMobileResponse> = MutableLiveData()
    val createUserByMobileResponse: LiveData<CreateUserByMobileResponse> =
        _createUserByMobileResponse

    fun createUserByMobile(mobile: String) {
        MyApplication.appContext?.let {
            uniqueId(it) { deviceId ->
                getFcmToken { fcmId ->
                    getAdId { aaid ->
                        val jsonInput = JsonObject()
                        jsonInput.addProperty("mobile", mobile)
                        jsonInput.addProperty("product", Constants.PRODUCT_NAME)
                        jsonInput.addProperty("aaid", aaid)
                        jsonInput.addProperty("deviceId", deviceId)
                        jsonInput.addProperty("fcmId", fcmId)
                        jsonInput.addProperty("osType", "Android")
                        apiServicesPlatform.createUser(jsonInput)
                            .execute(activity, true) { createUserByMobileResponse ->
                                _createUserByMobileResponse.value = createUserByMobileResponse
                            }
                    }
                }
            }
        }
    }


}