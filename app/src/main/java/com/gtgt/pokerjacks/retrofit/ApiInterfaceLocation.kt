package com.gtgt.pokerjacks.retrofit

import com.google.gson.JsonElement
import com.gtgt.pokerjacks.ui.location.CheckBannedStateRespone
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterfaceLocation {

    @POST("geoLocationService/checkBannedStates/")
    fun CheckBannedStates(
        @Body data: JsonElement
    ): Call<CheckBannedStateRespone>
}