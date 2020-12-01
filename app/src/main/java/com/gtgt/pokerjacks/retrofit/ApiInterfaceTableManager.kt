package com.gtgt.pokerjacks.retrofit

import com.google.gson.JsonElement
import com.gtgt.pokerjacks.base.AnyModel
import com.gtgt.pokerjacks.ui.lobby.model.LobbyTables
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterfaceTableManager {
    @GET("getActiveTables")
    fun getActiveTables(): Call<LobbyTables>

    @POST("joinTable")
    fun joinTable(@Body data: JsonElement): Call<AnyModel>
}