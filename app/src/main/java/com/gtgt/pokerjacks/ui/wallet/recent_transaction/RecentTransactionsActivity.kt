package com.gtgt.pokerjacks.ui.wallet.recent_transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.transaction_filter_layout.view.*

class RecentTransactionsActivity : BaseActivity() {
    /*private val transactionsAdapter by lazy {
        RecentTransactionsAdapter(this,
            onItemClicked = { recentTransactionInfo ->
                launchActivity<RecentTransactionDetailsActivity> {
                    putExtra("recentTransactionInfo", recentTransactionInfo as Serializable)
                }
            })
    }

    private val viewModel: RecentTransactionsViewModel by viewModel()*/

    private var cacheFilterList = mutableSetOf<String>()
    private var filters = mutableListOf<String>()
    private val filtersList = arrayOf(
        "Cash Added",
        "Withdrawal",
        "Points Rummy",
        "Pools Rummy",
        "Deals Rummy",
        "Bonus Disbursements"
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
    }

    private fun initUI() {
        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text="Recent Transactions"
        tv_filter.visibility=View.VISIBLE

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

            /*filters.forEach { name ->
                val cb = CheckBox(this)
                dialogView.ll_filters.addView(cb)

                cb.apply {
                    checkboxes.add(this)
                    text = name
                    tag = name

                    setTextColor(Color.WHITE)
                    buttonTintList = resources.getColorStateList(R.color.green)

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
            }*/

            dialog.show()
        }
    }
}