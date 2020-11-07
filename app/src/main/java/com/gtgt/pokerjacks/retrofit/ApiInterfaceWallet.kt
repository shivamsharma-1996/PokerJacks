package com.gtgt.pokerjacks.retrofit

import com.google.gson.JsonElement
import com.gtgt.pokerjacks.ui.wallet.wallet.RecentTransactionResponse
import com.gtgt.pokerjacks.ui.wallet.wallet.WalletDetailsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterfaceWallet {

    @GET("walletService/getWalletDetailsByToken/")
    fun getWalletDetailsByToken(): Call<WalletDetailsResponse>

    @POST("walletService/getWalletTransactions/")
    fun getRecentTransactions(
        @Body jsonElement: JsonElement
    ): Call<RecentTransactionResponse>

//    @POST("walletService/getBonusHistory/")
//    fun getBonusHistory(
//        @Body jsonElement: JsonElement
//    ): Call<GetBonusHistory>
//
//    @POST("walletService/getBonusDisbursements/")
//    fun getBonusDisbursements(
//        @Body jsonElement: JsonElement
//    ): Call<GetBonusDisbursements>
//
//    @POST("walletService/getPointsInPlayTransactions/")
//    fun getPointsInPlayTransactions(
//        @Body jsonElement: JsonElement
//    ): Call<PointsInPlayTransactionsResponse>
}