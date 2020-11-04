package com.gtgt.pokerjacks.ui.profile.profile.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.retrieveString
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlatform
import com.gtgt.pokerjacks.ui.profile.profile.model.GetDepositeLimit
import com.gtgt.pokerjacks.ui.profile.profile.model.UpdateDepositeLimit
import com.gtgt.pokerjacks.ui.profile.profile.model.UserProfileDetailsInfo

class ProfileViewModel : BaseViewModel() {


    private var _userProfileInfo: MutableLiveData<UserProfileDetailsInfo> =
        MutableLiveData()
    val userProfileInfo: LiveData<UserProfileDetailsInfo> = _userProfileInfo

    private var _getDespositeLimit: MutableLiveData<GetDepositeLimit> = MutableLiveData()
    val getDespositeLimit: LiveData<GetDepositeLimit> = _getDespositeLimit

    private var _updateDepositLimits: MutableLiveData<UpdateDepositeLimit> = MutableLiveData()
    val updateDepositLimits: LiveData<UpdateDepositeLimit> = _updateDepositLimits

    private var _bloclMeResponse: MutableLiveData<BaseModel> = MutableLiveData()
    val bloclMeResponse: LiveData<BaseModel> = _bloclMeResponse

    fun uploadProfilePic(bitmap: Bitmap): LiveData<String> {
        val profilePicResponse: MutableLiveData<String> = MutableLiveData()
        apiServicesPlatform.uploadProfilePic(
            ApiInterfacePlatform.createRequestBody(bitmap, "file")
        ).execute(activity, true) {
            if (it.success) {
                profilePicResponse.value = it.description
            } else {
                profilePicResponse.value = it.description
            }
        }
        return profilePicResponse
    }


    fun getUserProfileDetailsInfo(showLoading: Boolean = true) {
        apiServicesPlatform.getUserProfileDetails().execute(activity, showLoading) {
            if (it.success) {
                _userProfileInfo.value = it.info
            } else {
                it.errorCode
            }
        }
    }

    fun getDepositLimit() {
        apiServicesPlatform.getDepositLimit().execute(activity, true) {
            _getDespositeLimit.value = it
        }
    }

    fun updateDepositLimits(amount: Double, isIncrease: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("amount", amount)
//        jsonObject.addProperty("isIncrease", isIncrease)
        apiServicesPlatform.updateDepositLimits(jsonObject).execute(activity, true) {
            _updateDepositLimits.value = it
        }
    }

    fun blockMe(blockStatus: String, blockType: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("blockStatus", blockStatus)
        jsonObject.addProperty("blockType", blockType)
        apiServicesPlatform.blockMe(jsonObject).execute(activity, true) {
            _bloclMeResponse.value = it
        }
    }

    fun validateMPIN(oldPassword: String): LiveData<BaseModel> {
        val validateMPINResponse: MutableLiveData<BaseModel> = MutableLiveData()
        val jsonObject = JsonObject()
        jsonObject.addProperty("oldPassword", oldPassword)
        apiServicesPlatform.validateMPIN(jsonObject).execute(activity, true) {
            validateMPINResponse.value = it
        }
        return validateMPINResponse
    }

    fun changeMPIN(newPassword: String): LiveData<BaseModel> {
        val changeMPINResponse: MutableLiveData<BaseModel> = MutableLiveData()
        val jsonObject = JsonObject()
        jsonObject.addProperty("newPassword", newPassword)
        jsonObject.addProperty("userUniqueId", retrieveString("USER_ID"))
        jsonObject.addProperty("deviceId", retrieveString("UNIQUE_ID"))
        apiServicesPlatform.changeMPIN(jsonObject).execute(activity, true) {
            changeMPINResponse.value = it
        }
        return changeMPINResponse
    }

    private var _checkName: MutableLiveData<BaseModel> = MutableLiveData()
    val checkName: LiveData<BaseModel> = _checkName

    var lastUserName = ""
    fun checkUserName(username: String) {
        if (lastUserName != username) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", username)
            apiServicesPlatform.checkUserName(jsonObject).execute(activity, true) {
                lastUserName = username
                _checkName.value = it
            }
        }
    }

    private var _checkReferral: MutableLiveData<BaseModel> = MutableLiveData()
    val checkReferral: LiveData<BaseModel> = _checkReferral

    fun checkReferralCode(referralCode: String = "") {
        val jsonObject = JsonObject()
        jsonObject.addProperty("referralCode", referralCode)
        apiServicesPlatform.checkReferralCode(jsonObject).execute(activity, true) {
            _checkReferral.value = it
        }
    }

    private var _updateName: MutableLiveData<BaseModel> = MutableLiveData()
    val updateName: LiveData<BaseModel> = _updateName

    fun updateUserName(username: String, code: String = "", isReferralVerified: Boolean = false) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("username", username)
        jsonObject.addProperty("isReferredUser", isReferralVerified)
        jsonObject.addProperty("code", code)
        apiServicesPlatform.updateUserName(jsonObject).execute(activity, true) {
            _updateName.value = it
        }
    }

    fun logout(): LiveData<BaseModel> {
        var response: MutableLiveData<BaseModel> = MutableLiveData()
        apiServicesPlatform.logout().execute(activity, true) {
            response.value = it
        }
        return response
    }
}