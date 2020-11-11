package com.gtgt.pokerjacks.ui.payment.model

data class Paytm(
        val mId: String,
        val channelId: String,
        val txnAmount: String,
        val website: String,
        val callBackUrl: String,
        val industryTypeId: String,
        val orderId: String,
        val custId: String
)