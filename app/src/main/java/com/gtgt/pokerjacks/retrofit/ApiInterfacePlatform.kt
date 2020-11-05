package com.gtgt.pokerjacks.retrofit

import android.graphics.Bitmap
import androidx.annotation.NonNull
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.base.AnyModel
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.ui.login.models.*
import com.gtgt.pokerjacks.ui.profile.manage_account.model.BankFromIFSC
import com.gtgt.pokerjacks.ui.profile.manage_account.model.CreateBankDetails
import com.gtgt.pokerjacks.ui.profile.manage_account.model.GetBankDetails
import com.gtgt.pokerjacks.ui.profile.manage_account.model.GetVerification
import com.gtgt.pokerjacks.ui.profile.profile.model.GetDepositeLimit
import com.gtgt.pokerjacks.ui.profile.profile.model.UpdateDepositeLimit
import com.gtgt.pokerjacks.ui.profile.profile.model.UserProfileDetails
import com.gtgt.pokerjacks.ui.profile.verify_address.UploadedDocumentDetails
import com.gtgt.pokerjacks.ui.profile.verify_address.UserDocumentDetails
import com.gtgt.pokerjacks.ui.profile.verify_address.UserLocationDetails
import com.gtgt.pokerjacks.ui.side_nav.refer_earn.model.GetReferralCode
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.*

interface ApiInterfacePlatform {

    companion object {
        fun createRequestBody(@NonNull bitmap: Bitmap, fileName: String): MultipartBody.Part {
            val f = File(MyApplication.instance!!.cacheDir, "${System.currentTimeMillis()}.jpg")
            f.createNewFile()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream as OutputStream?)

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(f)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            try {
                fos!!.write(stream.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), f)
            val part = MultipartBody.Part.createFormData(fileName, f.name, reqFile)
            return part
        }
    }

    @POST("userService/createUser/")
    fun createUser(@Body data: JsonObject): Call<CreateUserByMobileResponse>

    @POST("userService/verifyOTP/")
    fun verifyOtp(@Body data: JsonElement): Call<VerifyOtpResponse>

    @POST("userService/resendOTP/")
    fun resendOTP(@Body data: JsonElement): Call<ResendOTP>

    @POST("userService/createMPIN/")
    fun createMPIN(@Body data: JsonElement): Call<CreateMPinResponse>

    @POST("userService/login/")
    fun login(@Body data: JsonElement): Call<LoginResponse>

    @GET("userService/getWebPageUrls/")
    fun getUrls(): Call<WebPageUrls>

    @POST("userService/forgotMPIN/")
    fun forgotMPIN(@Body data: JsonElement): Call<BaseModel>

    @POST("userService/resetMPIN/")
    fun resetMPIN(@Body data: JsonElement): Call<CreateMPinResponse>

    @POST("userService/logout/")
    fun logout(): Call<BaseModel>

    @Multipart
    @POST("userService/uploadProfilePic/")
    fun uploadProfilePic(
        @Part profilePicPath: MultipartBody.Part
    ): Call<BaseModel>



    @POST("userService/createBankDetails/")
    fun createBankDetails(
        @Body data: JsonElement
    ): Call<CreateBankDetails>

    @GET("userService/getBankDetails/")
    fun getBankDetails(): Call<GetBankDetails>

    @POST("userService/deleteBankDetails/")
    fun deleteBankDetails(
        @Body data: JsonElement
    ): Call<BaseModel>

    @FormUrlEncoded
    @POST("userService/getBankVerificationFromUser/")
    fun getVerification(
        @Field("bankId") bankId: String
    ): Call<GetVerification>

    @GET("userService/getBankDetailsfromRedis/")
    fun getBankDetailsfromIFSC(
        @Query("ifsc") ifsc: String
    ): Call<BankFromIFSC>

    @GET("userService/getUserProfile/")
    fun getUserProfileDetails(): Call<UserProfileDetails>

    @GET("userService/getDepositLimit/")
    fun getDepositLimit(): Call<GetDepositeLimit>

    @POST("userService/changeDepositLimit/")
    fun updateDepositLimits(
        @Body jsonElement: JsonElement
    ): Call<UpdateDepositeLimit>

    @POST("userService/validateMPIN/")
    fun validateMPIN(
        @Body jsonElement: JsonElement
    ): Call<BaseModel>

    @POST("userService/changeMPIN/")
    fun changeMPIN(
        @Body jsonElement: JsonElement
    ): Call<BaseModel>

    @POST("userService/checkUserName/")
    fun checkUserName(
        @Body jsonElement: JsonElement
    ): Call<BaseModel>

    @POST("userService/checkReferralCode/")
    fun checkReferralCode(
        @Body jsonElement: JsonElement
    ): Call<BaseModel>

    @POST("userService/updateUserName/")
    fun updateUserName(
        @Body jsonElement: JsonElement
    ): Call<BaseModel>

    @GET("userService/getReferralCode/")
    fun getReferralCode(): Call<GetReferralCode>

    @GET("userService/checkReferralEligible/")
    fun checkReferralEligible(): Call<AnyModel>

    @POST("userService/applyReferralCode/")
    fun applyReferralCode(@Body data: JsonElement): Call<AnyModel>

    @POST("userService/blockMe/")
    fun blockMe(@Body data: JsonElement): Call<BaseModel>

    @POST("userService/updateEmail/")
    fun updateUserEmailAddress(@Body data: JsonElement): Call<UserProfileDetails>

    @POST("userService/verifyEmail/")
    fun verifyEmail(@Body data: JsonElement): Call<UserProfileDetails>

    @POST("userService/updateLocation/")
    fun updateLocation(@Body jsonElement: JsonElement): Call<UserLocationDetails>

    @Multipart
    @POST("userService/uploadDocuments/")
    fun uploadMultipleDocuments(
        @Part pinCode: MultipartBody.Part,
        @Part userId: MultipartBody.Part,
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part?,
        @Part doctype: MultipartBody.Part
    ): Call<UploadedDocumentDetails>

    @Multipart
    @POST("userService/uploadDocuments/")
    fun uploadSingleDocument(
        @Part pinCode: MultipartBody.Part,
        @Part userId: MultipartBody.Part,
        @Part file1: MultipartBody.Part,
        @Part doctype: MultipartBody.Part
    ): Call<UploadedDocumentDetails>

    @Multipart
    @POST("userService/uploadDocuments/")
    fun uploadMultipleDocumentsWithDocId(
        @Part doc_id: MultipartBody.Part,
        @Part pinCode: MultipartBody.Part,
        @Part userId: MultipartBody.Part,
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part?,
        @Part doctype: MultipartBody.Part
    ): Call<UploadedDocumentDetails>

    @Multipart
    @POST("userService/uploadDocuments/")
    fun uploadSingleDocumentWithDocId(
        @Part doc_id: MultipartBody.Part,
        @Part pinCode: MultipartBody.Part,
        @Part userId: MultipartBody.Part,
        @Part file1: MultipartBody.Part,
        @Part doctype: MultipartBody.Part
    ): Call<UploadedDocumentDetails>

    @POST("userService/checkAddressDetails/")
    fun getUserDocumentDetails(): Call<UserDocumentDetails>


}