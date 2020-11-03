package com.gtgt.pokerjacks.retrofit

import android.graphics.Bitmap
import androidx.annotation.NonNull
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.ui.login.models.*
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

    @POST("userService/resetMPIN")
    fun resetMPIN(@Body data: JsonElement): Call<CreateMPinResponse>

    @POST("userService/logout/")
    fun logout(): Call<BaseModel>

    @Multipart
    @POST("userService/uploadProfilePic/")
    fun uploadProfilePic(
        @Part profilePicPath: MultipartBody.Part
    ): Call<BaseModel>



}