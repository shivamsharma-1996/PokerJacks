package com.gtgt.pokerjacks.retrofit

import com.google.gson.JsonObject
import com.gtgt.pokerjacks.ui.splash_screen.CheckUpdateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterfaceUpdate {
    @POST("gameService/checkUpdateAvailable/")
    fun checkUpdateAvailable(@Body data: JsonObject): Call<CheckUpdateResponse>
}