package com.gtgt.pokerjacks.retrofit

import com.google.gson.JsonElement
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.ui.payment.model.CreatePaymentResponse
import com.gtgt.pokerjacks.ui.payment.model.PaymentStatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterfacePayment {

    @POST("paymentService/createPayment")
    fun createPayment(
        @Body data: JsonElement
    ): Call<CreatePaymentResponse>

    @POST("paymentService/updatePaymentStatus")
    fun paymentstatus(
        @Body data: JsonElement
    ): Call<PaymentStatusResponse>

    @POST("paymentService/requestWithdrawal")
    fun withdrawalmoney(
        @Body jsonElement: JsonElement
    ): Call<BaseModel>

    @POST("paymentService/createMerchant")
    fun createMerchant(
        @Body jsonElement: JsonElement
    ): Call<BaseModel>
}