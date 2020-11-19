package com.gtgt.pokerjacks.ui.wallet.bonus_distribution.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.wallet.bonus_distribution.adapter.BonusHistoryAdapter
import com.gtgt.pokerjacks.ui.wallet.bonus_distribution.viewModel.BonusDistributionViewModel
import com.gtgt.pokerjacks.utils.LinearLayoutManagerWrapper
import kotlinx.android.synthetic.main.activity_bonus_distribution.*
import kotlinx.android.synthetic.main.activity_recent_distributions.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class BonusDistributionActivity : BaseActivity() {
    private val viewModel: BonusDistributionViewModel by viewModel()
    var stopApiCall = false
    private val bonusHistoryAdapter = BonusHistoryAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bonus_distribution)

        iv_back.onOneClick {
            onBackPressed()
        }
        tv_commonTitle.text = "Bonus Disbursements"

        tv_recentDistributions.onOneClick {
            launchActivity<RecentDistributionsActivity> { }
        }

        if (rv_bonusHistory.layoutManager == null) {
            rv_bonusHistory.layoutManager = LinearLayoutManagerWrapper(this)
            rv_bonusHistory.adapter = bonusHistoryAdapter
        }

        viewModel.getBonusHistory()
        viewModel.bonusHistoryList.observe(this, Observer {
            if (it.isNotEmpty()) {
                ll_no_recent_history.visibility = View.GONE
                rv_bonusHistory.visibility = View.VISIBLE
                bonusHistoryAdapter.submitList(it.map { it })

                tv_totalBonusRemaining.text =
                    "₹${viewModel.totalRemainingBonus.value?.toDecimalFormat()}"
                tv_received.text = "₹${viewModel.totalReceivedAmount.value?.toDecimalFormat()}"
                tv_disbursed.text = "₹${viewModel.totalDisbursedAmount.value?.toDecimalFormat()}"
                tv_expired.text = "₹${viewModel.totalExpiredBonus.value?.toDecimalFormat()}"
            } else {
                if (viewModel.offset <= 2) {
                    ll_no_recent_history.visibility = View.VISIBLE
                    rv_bonusHistory.visibility = View.GONE

                    tv_totalBonusRemaining.text = "₹0"
                    tv_received.text = "₹0"
                    tv_disbursed.text = "₹0"
                    tv_expired.text = "₹0"
                }
            }
        })

        rv_bonusHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((rv_bonusHistory.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == bonusHistoryAdapter.currentList.size - 1) {
                    if (!stopApiCall) {
                        viewModel.getBonusHistory(false)
                    }
                }
            }
        })

        viewModel.stopApiCall.observe(this, Observer {
            stopApiCall = it
        })
    }
}