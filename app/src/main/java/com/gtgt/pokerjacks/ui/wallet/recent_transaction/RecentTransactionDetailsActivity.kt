package com.gtgt.pokerjacks.ui.wallet.recent_transaction

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.wallet.wallet.RecentTransactionInfo

//class RecentTransactionDetailsActivity : BaseActivity() {
//
//    private val viewModel: RecentTransactionsViewModel by viewModel()
//    private val recentTransactionInfo by lazy { intent.getSerializableExtra("recentTransactionInfo") as RecentTransactionInfo? }
//
//    @SuppressLint("SetTextI18n")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_recent_transaction_details)
//
//        supportActionBar?.hide()
//
//        recentTransactionInfo?.let {
//            tv_title.text = it.description
//            tv_amount.text = "₹ ${it.amount.toDecimalFormat()}"
//            tv_closingBalance.text = "Closing Balance: ₹${it.closing_balance.toDecimalFormat()}"
//            tv_time.text = "${it.creationDate} | ${it.creationTime}"
//            if (it.txn_type == "Points" && it.sub_txn_type == "BuyOut") {
//                viewModel.getPointsInPlayTransactions(it.user_txn_id)
//            } else if (it.txn_type == "Deposit") {
//                ll_cashAdded.visibility = View.VISIBLE
//                tv_cashAddedId.text = "Txn Id: ${it.user_txn_id}"
//                tv_addCashBank.text = it.mode
//            } else if (it.txn_type == "Withdrawal") {
//                ll_cashWithdraw.visibility = View.VISIBLE
//                tv_withdrawMode.text = "Mode: ${it.mode}"
//                tv_withdrawTxnId.text = "Txn Id: ${it.user_txn_id}"
//                tv_withdrawStatus.text = "${it.status}"
//                tv_withdrawBank.text = it.formattedAccount
//            } else if (it.txn_type == "Points" && it.sub_txn_type == "BuyIn") {
//                tv_bo2.visibility = View.VISIBLE
//                tv_bo2.text = "Id: ${it.user_txn_id}"
//            } else if (it.txn_type == "Deals" && it.sub_txn_type == "BuyIn") {
//                tv_bo2.visibility = View.VISIBLE
//                tv_bo2.text = "Id: ${it.user_txn_id}"
//            }
//        }
//
//        viewModel.pointsInPlayTransactionsResponse.observe(this, Observer {
//            if (it.success) {
//                it.info?.let { info ->
//                    fl_points.visibility = View.VISIBLE
//                    ll_pointsRummy.visibility = View.VISIBLE
//                    tv_lostAmount.text = "₹${info.moneyLost.toDecimalFormat()}"
//                    tv_wonAmount.text = "₹${info.moneyWon.toDecimalFormat()}"
//                    tv_buyInAmount.text = "₹${info.buyInAmount.toDecimalFormat()}"
//
//                    ll_pointsView.removeAllViews()
//                    for (i in info.transactionHistory.indices) {
//                        addPointsView(ll_pointsView, i, info.transactionHistory)
//                    }
//                }
//            }
//        })
//
//        iv_back.onOneClick {
//            onBackPressed()
//        }
//
//        ll_support.onOneClick {
//            launchActivity<SupportActivity> { }
//        }
//    }
//
//    private fun addPointsView(
//        llPointsview: LinearLayout,
//        position: Int,
//        transactionHistory: List<TransactionHistory>
//    ) {
//        val layout = LayoutInflater.from(this)
//            .inflate(R.layout.transaction_poins_view, llPointsview, false)
//
//        layout.tv_gameId.text = transactionHistory[position].gameId
//        layout.tv_time.text = transactionHistory[position].createdAt.formatDate()
//        if (transactionHistory[position].txnMode == "Credit") {
//            layout.tv_amount.setTextColor(Color.parseColor("#1BC243"))
//            layout.tv_amount.text = "+ ₹${transactionHistory[position].amount.toDecimalFormat()}"
//        } else {
//            layout.tv_amount.setTextColor(Color.parseColor("#FFFFFF"))
//            layout.tv_amount.text = "- ₹${transactionHistory[position].amount.toDecimalFormat()}"
//        }
//
//        if (position == transactionHistory.size - 1) {
//            layout.v_line.visibility = View.INVISIBLE
//        } else {
//            layout.v_line.visibility = View.VISIBLE
//        }
//
//        llPointsview.addView(layout)
//    }
//}