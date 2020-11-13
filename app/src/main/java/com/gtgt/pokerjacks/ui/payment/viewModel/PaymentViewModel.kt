package com.gtgt.pokerjacks.ui.payment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.ui.payment.model.CreatePaymentResponse
import com.gtgt.pokerjacks.ui.payment.model.PaymentStatusResponse

class PaymentViewModel : BaseViewModel() {
    private val _createPaymentResponse = MutableLiveData<CreatePaymentResponse>()
    val createPaymentResponse: LiveData<CreatePaymentResponse> = _createPaymentResponse

    private val _paymentStatus = MutableLiveData<PaymentStatusResponse>()
    val paymentStatus: LiveData<PaymentStatusResponse> = _paymentStatus

    fun createPayment(amount: String, promocode: String? =null) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("amount", amount)
        jsonObject.addProperty("promocode", promocode)
        apiServicesPayment.createPayment(jsonObject).execute(activity, true) {
            _createPaymentResponse.value = it
        }
    }

    fun paymentstatus(orderId: String, status: String, txnId: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("orderId", orderId)
        jsonObject.addProperty("status", status)
        jsonObject.addProperty("txnId", txnId)
        apiServicesPayment.paymentstatus(jsonObject).execute(activity, true) {
            _paymentStatus.value = it
        }
    }
}