package com.gtgt.pokerjacks.ui.payment.view

import android.content.Intent
import android.os.Bundle
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.log
import com.gtgt.pokerjacks.extensions.store
import com.gtgt.pokerjacks.ui.payment.model.CreatePaymentInfo
import com.gtgt.pokerjacks.ui.payment.viewModel.PaymentViewModel
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager

class PaytmPayment : BaseActivity() {

    private val ActivityRequestCode = 2
    private val createPaymentInfo by lazy { intent.getSerializableExtra("TOKEN_DATA") as CreatePaymentInfo? }
    var orderId = ""
    private val viewModel: PaymentViewModel by store()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createPaymentInfo?.let {
            processToPay(it)
            orderId = it.orderId
        }
    }

    fun sendPaymentStatus(
        orderId: String = "",
        status: String = "",
        txnId: String = "",
        statusId: Int
    ) {
//        subscribeToTopic(Constants.TopicName.PAIDUSER.name)
        intent.putExtra("orderId", this.orderId)
        intent.putExtra("status", status)
        intent.putExtra("txnId", txnId)
        intent.putExtra("statusId", statusId)
        setResult(2, intent)

        viewModel.paymentstatus(
            orderId = this.orderId,
            status = status,
            txnId = txnId
        )
        finish()
    }

    private fun processToPay(response: CreatePaymentInfo) {

        val paytmOrder = PaytmOrder(
            response.orderId,
            response.mId,
            response.txnId,
            response.txnAmount,
            response.callbackUrl
        )
        val transactionManager =
            TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {
                override fun onTransactionResponse(bundle: Bundle) {

                    val status = "${bundle["STATUS"]}"
                    val order_id = "${bundle["ORDERID"]}"
                    val txn_id = "${bundle["TXNID"]}"

                    sendPaymentStatus(
                        orderId = order_id,
                        status = status,
                        txnId = txn_id,
                        statusId = -1
                    )
                }

                override fun networkNotAvailable() {
                    sendPaymentStatus(status = "NETWORK_ERROR", statusId = 1)
                }

                override fun onErrorProceed(s: String) {
                    sendPaymentStatus(status = s, statusId = 1)
                }

                override fun clientAuthenticationFailed(s: String) {
                    sendPaymentStatus(status = s, statusId = 0)
                }

                override fun someUIErrorOccurred(s: String) {
                    sendPaymentStatus(status = s, statusId = 1)
                }

                override fun onErrorLoadingWebPage(i: Int, s: String, s1: String) {
                    sendPaymentStatus(status = s, statusId = 1)
                }

                override fun onBackPressedCancelTransaction() {
                    sendPaymentStatus(status = "BACK_PRESS_CANCEL", statusId = 2)
                }

                override fun onTransactionCancel(s: String, bundle: Bundle) {
                    sendPaymentStatus(status = s, statusId = 1)
                }
            })

        val host = if (BuildConfig.DEBUG) {
            "https://securegw-stage.paytm.in/"
        } else {
            "https://securegw.paytm.in/"
        }

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage")
        transactionManager.setAppInvokeEnabled(false)
        transactionManager.startTransaction(this, ActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log("onActivityResult", " result code $resultCode")
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ActivityRequestCode && data != null) {
            val bundle = data.extras
            if (bundle != null) {
                val status = "${bundle["STATUS"]}"
                val order_id = "${bundle["ORDERID"]}"
                val txn_id = "${bundle["TXNID"]}"
                when (resultCode) {
                    -1 -> sendPaymentStatus(
                        orderId = order_id,
                        status = status,
                        txnId = txn_id,
                        statusId = -1
                    )
                    0 -> sendPaymentStatus(
                        orderId = order_id,
                        status = status,
                        txnId = txn_id,
                        statusId = 0
                    )
                    1 -> sendPaymentStatus(status = "NETWORK_ERROR", statusId = 1)
                }
            }
            log(
                "onActivityResult",
                " data " + data.getStringExtra("nativeSdkForMerchantMessage")!!
            )
            log("onActivityResult", " data response - " + data.getStringExtra("response")!!)
        } else {
            log("onActivityResult", " payment failed")
            sendPaymentStatus(status = "TXN_FAILED", statusId = 0)
        }
    }
}