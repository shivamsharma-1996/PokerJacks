package com.gtgt.pokerjacks.retrofit

import com.google.gson.JsonElement
import com.gtgt.pokerjacks.ui.offers.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterfaceBonus {

    @GET("bonusService/getUserOffers/")
    fun getUserOffers(): Call<BonusOffersResponse>

    @POST("bonusService/getUserScratchCards/")
    fun getUserScratchCards(
        @Body jsonElement: JsonElement
    ): Call<ScratchCardResponse>

    @GET("bonusService/getUserReferralData/")
    fun getUserReferralData(): Call<ReferralDataResponse>

    @POST("bonusService/applyPromoCode/")
    fun applyPromoCode(
        @Body data: JsonElement
    ): Call<ApplyPromoCodeResponse>

    @POST("bonusService/openScratchCard/")
    fun openScratchCard(
        @Body data: JsonElement
    ): Call<OpenScratchCardResponse>
}