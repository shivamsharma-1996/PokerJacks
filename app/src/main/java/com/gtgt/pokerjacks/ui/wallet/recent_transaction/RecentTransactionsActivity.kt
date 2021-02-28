package com.gtgt.pokerjacks.ui.wallet.recent_transaction

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.utils.LinearLayoutManagerWrapper
import kotlinx.android.synthetic.main.activity_recent_transactions.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.toolbar_layout_filter.*
import kotlinx.android.synthetic.main.transaction_filter_layout.view.*

class RecentTransactionsActivity : BaseActivity() {
    private val transactionsAdapter by lazy {
        RecentTransactionsAdapter(this,
            onItemClicked = { recentTransactionInfo ->
//                launchActivity<RecentTransactionDetailsActivity> {
//                    putExtra("recentTransactionInfo", recentTransactionInfo as Serializable)
//                }
            })
    }

    private val viewModel: RecentTransactionsViewModel by viewModel()


    private var cacheFilterList = mutableSetOf<String>()
    private var filters = mutableListOf<String>()
    private val filtersList = arrayOf(
        "Cash added",
        "Bonus Disbursement",
        "Buy in",
        "Buy out",
        "Withdrawal"
    )

    fun getFilterList() {
        for (i in filtersList.indices) {
            filters.add(filtersList[i])
        }
    }

    var stopApiCall = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_transactions)

        initUI()
        getFilterList()

        rv_recentTransactions.layoutManager = LinearLayoutManagerWrapper(this)
        rv_recentTransactions.adapter = transactionsAdapter

        viewModel.getWalletTransactionDetails(cacheFilterList.toList())
        viewModel.transactionDetails.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                if (viewModel.offset <= 2) {
                    ll_no_recent_transactions.visibility = View.VISIBLE
                    rv_recentTransactions.visibility = View.GONE
                }
            } else {
                rv_recentTransactions.visibility = View.VISIBLE
                ll_no_recent_transactions.visibility = View.GONE
                transactionsAdapter.submitList(it.map { it })
            }
        })

        rv_recentTransactions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((rv_recentTransactions.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == transactionsAdapter.currentList.size - 1) {
//                    recentTransactionsViewModel.pageNumber++
                    if (!stopApiCall) {
                        viewModel.getWalletTransactionDetails(
                            cacheFilterList.toList(),
                            false
                        )
                    }
                }
            }
        })

        viewModel.stopApiCall.observe(this, Observer {
            stopApiCall = it
        })
    }

    private fun initUI() {
        iv_go_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Recent Transactions"
        tv_filter.visibility = View.VISIBLE

        tv_filter.onOneClick {
            val selectedFilters = cacheFilterList.map { it }.toMutableSet()

            val dialogView =
                LayoutInflater.from(this).inflate(R.layout.transaction_filter_layout, null)

            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogView)
            dialog.setCancelable(true)

            if (selectedFilters.size == 6) {
                dialogView.cb_all.isChecked = true
            }

            val checkboxes = mutableListOf<CheckBox>()

            filters.forEach { name ->
                val cb = CheckBox(this)
                dialogView.ll_filters.addView(cb)

                cb.apply {
                    checkboxes.add(this)
                    text = name
                    tag = name

                    setTextColor(Color.WHITE)
                    buttonTintList = resources.getColorStateList(R.color.dark_blue)

                    margins(0, 0, 0, dip(8))
                    padding(0, dip(16), 0, 0)

                    isChecked = cacheFilterList.contains(name)

                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) selectedFilters.add(
                            name
                        ) else selectedFilters.remove(name)
                    }
                }
            }

            dialogView.cb_all.setOnCheckedChangeListener { buttonView, isChecked ->
                checkboxes.forEach { it.isChecked = isChecked }
            }

            (dialogView.parent as View).setBackgroundColor(Color.TRANSPARENT)
            dialogView.btn_applyFilter.onOneClick {

                cacheFilterList = selectedFilters

                viewModel.offset = 1
                viewModel.getWalletTransactionDetails(cacheFilterList.toList())

                dialog.dismiss()
            }

            dialog.show()
        }
    }
}