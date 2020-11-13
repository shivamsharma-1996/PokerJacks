package com.gtgt.pokerjacks.ui.payment.model

data class PaymentStatusResponse(
    val success: Boolean,
    val errorCode: Int,
    val description: String,
    val info: PaymentStatusInfo?
)

data class PaymentStatusInfo(
    val orderId: String,
    val amount: String,
    val status: String
)