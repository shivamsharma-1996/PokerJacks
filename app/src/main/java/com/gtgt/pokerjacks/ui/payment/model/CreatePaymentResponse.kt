package com.gtgt.pokerjacks.ui.payment.model

import java.io.Serializable

data class CreatePaymentResponse(
    val success: Boolean,
    val errorCode: Int,
    val description: String,
    val info: CreatePaymentInfo
)

data class CreatePaymentInfo(
    val userId: String,
    val txnAmount: String,
    val mId: String,
    val callbackUrl: String,
    val txnId: String,
    val orderId: String,
    val status: String,
    val checksumhash: String,
    val website: String
): Serializable