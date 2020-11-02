package com.gtgt.pokerjacks.retrofit

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.ui.login.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterfacePlatform {

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

}