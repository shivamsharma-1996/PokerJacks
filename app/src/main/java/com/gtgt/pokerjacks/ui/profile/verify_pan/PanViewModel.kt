package com.gtgt.pokerjacks.ui.profile.verify_pan

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.AnyModel
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlatform
import com.gtgt.pokerjacks.ui.profile.verify_pan.model.PanDetails
import com.gtgt.pokerjacks.ui.profile.verify_pan.model.UploadedPanDetailsInfo
import okhttp3.MultipartBody
import kotlin.concurrent.thread

class PanViewModel : BaseViewModel() {


    private val _userPanDetails: MutableLiveData<PanDetails> = MutableLiveData()
    val userPanDetails: LiveData<PanDetails> = _userPanDetails

    private val _verificationStatus: MutableLiveData<JsonObject> = MutableLiveData()
    val verificationStatus: LiveData<JsonObject> = _verificationStatus

    private val _uploadedPanDetails: MutableLiveData<UploadedPanDetailsInfo> = MutableLiveData()
    val uploadedPanDetails: LiveData<UploadedPanDetailsInfo> = _uploadedPanDetails

    fun getUserPanDetails() {
        apiServicesPlatform.getUserPanDetails(
        ).execute(activity, true) {
            _userPanDetails.value = it
        }
    }


    fun submitPanDetails(
        saveDetails: Boolean,
        bitmap: Bitmap,
        userPanName: String,
        userPanNumber: String,
        userDob: String
    ) : LiveData<AnyModel>{
        val response = MutableLiveData<AnyModel>()

        thread {
            apiServicesPlatform.uploadPanDetails(
                save_details = MultipartBody.Part.createFormData(
                    "save_details",
                    "$saveDetails"
                ),
                pan_pic_path = ApiInterfacePlatform.createRequestBody(
                    bitmap,
                    "pan_pic_path"
                ),
                user_pan_name = MultipartBody.Part.createFormData(
                    "user_pan_name",
                    userPanName
                ),
                pan_num = MultipartBody.Part.createFormData(
                    "pan_num",
                    userPanNumber
                ),
                dob_date = MultipartBody.Part.createFormData("dob_date", userDob)

            ).execute(activity, true) {
                response.value = it
            }
        }
        return response
    }
}