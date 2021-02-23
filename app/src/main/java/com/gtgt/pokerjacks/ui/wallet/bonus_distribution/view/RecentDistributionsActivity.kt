package com.gtgt.pokerjacks.ui.wallet.bonus_distribution.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.wallet.bonus_distribution.adapter.RecentDistributionAdapter
import com.gtgt.pokerjacks.ui.wallet.bonus_distribution.viewModel.BonusDistributionViewModel
import com.gtgt.pokerjacks.utils.LinearLayoutManagerWrapper
import kotlinx.android.synthetic.main.activity_recent_distributions.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class RecentDistributionsActivity : BaseActivity() {
    private val viewModel: BonusDistributionViewModel by viewModel()
    var stopApiCall = false
    private val recentDistributionAdapter = RecentDistributionAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_distributions)

        iv_back.onOneClick {
            onBackPressed()
        }
        tv_toolbar_title.text = "Recent Disbursements"

        if (rv_recentDistributions.layoutManager == null) {
            rv_recentDistributions.layoutManager = LinearLayoutManagerWrapper(this)
            rv_recentDistributions.adapter = recentDistributionAdapter
        }

        viewModel.getBonusDisbursements()
        viewModel.getBonusDisbursementsInfo.observe(this, Observer {
            if (it.isNotEmpty()) {
                ll_no_recent_distribution.visibility = View.GONE
                rv_recentDistributions.visibility = View.VISIBLE
                recentDistributionAdapter.submitList(it.map { it })
            } else {
                ll_no_recent_distribution.visibility = View.VISIBLE
                rv_recentDistributions.visibility = View.GONE
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
        })
    }
}