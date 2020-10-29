package com.gtgt.pokerjacks.ui.wallet.bonus_distribution

import android.os.Bundle
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.activity_bonus_distribution.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class BonusDistributionActivity : BaseActivity() {
    //    private val viewModel: BonusDistributionViewModel by viewModel()
//    var stopApiCall = false
//    private val bonusHistoryAdapter = BonusHistoryAdapter(this)
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

        /*if (rv_bonusHistory.layoutManager == null) {
            rv_bonusHistory.layoutManager = LinearLayoutManagerWrapper(this)
            rv_bonusHistory.adapter = bonusHistoryAdapter
        }

        viewModel.getBonusHistory()
        viewModel.bonusHistoryList.observe(this, Observer {
            if (it.isNotEmpty()) {
                bonusHistoryAdapter.submitList(it.map { it })

                tv_totalBonusRemaining.text =
                    "₹${viewModel.totalRemainingBonus.value?.toDecimalFormat()}"
                tv_received.text = "₹${viewModel.totalReceivedAmount.value?.toDecimalFormat()}"
                tv_disbursed.text = "₹${viewModel.totalDisbursedAmount.value?.toDecimalFormat()}"
                tv_expired.text = "₹${viewModel.totalExpiredBonus.value?.toDecimalFormat()}"
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
        })*/
    }
}