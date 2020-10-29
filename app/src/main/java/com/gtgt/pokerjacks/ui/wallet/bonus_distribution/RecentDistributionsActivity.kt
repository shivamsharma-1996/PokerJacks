package com.gtgt.pokerjacks.ui.wallet.bonus_distribution

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.toolbar_layout.*

class RecentDistributionsActivity : BaseActivity() {
    //    private val viewModel: BonusDistributionViewModel by viewModel()
//    var stopApiCall = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_distributions)

        iv_back.onOneClick {
            onBackPressed()
        }
        tv_commonTitle.text = "Recent Disbursements"

        /*if (rv_recentDistributions.layoutManager == null) {
            rv_recentDistributions.layoutManager = LinearLayoutManagerWrapper(this)
            rv_recentDistributions.adapter = recentDistributionAdapter
        }

        viewModel.getBonusDisbursements()
        viewModel.getBonusDisbursementsInfo.observe(this, Observer {
            if (it.isNotEmpty()) {
                recentDistributionAdapter.submitList(it.map { it })
            }
        })

        rv_recentDistributions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((rv_recentDistributions.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == recentDistributionAdapter.currentList.size - 1) {
                    if (!stopApiCall) {
                        viewModel.getBonusDisbursements(false)
                    }
                }
            }
        })

        viewModel.stopApiCall.observe(this, Observer {
            stopApiCall = it
        })*/
    }
}