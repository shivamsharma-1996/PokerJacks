package com.gtgt.pokerjacks.retrofit

import com.gtgt.pokerjacks.base.AnyModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterfacePlayWallet {
    @GET("playWalletService/addChips/")
    fun addChips(): Call<AnyModel>

    @GET("playWalletService/getWalletDetailsByToken/")
    fun getPlayWalletDetailsByToken(): Call<AnyModel>
}