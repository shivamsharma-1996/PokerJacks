package com.gtgt.pokerjacks.ui.offers.scratch_card

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.offers.adapter.AllScratchCardsAdapter
import com.gtgt.pokerjacks.ui.offers.model.TotalScratchCards
import com.gtgt.pokerjacks.ui.offers.viewModel.OffersViewModel
import kotlinx.android.synthetic.main.activity_all_scratch_card.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.io.Serializable

class AllScratchCardActivity : BaseActivity() {
    private val viewModel: OffersViewModel by viewModel()
    var stopApiCall = false
    private val allScratchCardsAdapter = AllScratchCardsAdapter(this, ::onItemClicked)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_scratch_card)

        if (rv_allScratchCards.layoutManager == null) {
            rv_allScratchCards.layoutManager = GridLayoutManager(this, 2)
            rv_allScratchCards.adapter = allScratchCardsAdapter
        }
        viewModel.getScritchCards()
        viewModel.scratchCardResponse.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                allScratchCardsAdapter.submitList(it.map { it })
            } else {
                if (viewModel.offset == 1) {
                    rv_allScratchCards.visibility = View.GONE
                    iv_no_scratch_card_img.visibility = View.VISIBLE
                    tv_no_scratch_cards.visibility = View.VISIBLE
                }
            }
        })

        viewModel.totalAmount.observe(this, Observer {
            tv_totalRewards.text = "₹${it.toDecimalFormat()}"
        })

        rv_allScratchCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((rv_allScratchCards.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == allScratchCardsAdapter.currentList.size - 1) {
                    if (!stopApiCall) {
                        viewModel.getScritchCards(false)
                    }
                }
            }
        })

        viewModel.stopApiCall.observe(this, Observer {
            stopApiCall = it
        })

        iv_backSC.onOneClick {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        setResult(2)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun calculateRewards(it: List<TotalScratchCards>) {
        var totalRewards = 0.0
        it.forEach {
            if (it.card_type == "Open") {
                totalRewards += it.issued_amount
            }
        }

        tv_totalRewards.text = "₹${totalRewards.toDecimalFormat()}"
    }

    private fun onItemClicked(view: View, itemAt: TotalScratchCards) {
        val intent = Intent(this, ScratchCardActivity::class.java)
        intent.putExtra("SCRATCH_CARD_ITEM", itemAt as Serializable)
        // Get the transition name from the string
        val transitionName = getString(R.string.transaction_name)

        val options =

            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                view, // Starting view
                transitionName    // The String
            )

        ActivityCompat.startActivityForResult(this, intent, 1, options.toBundle())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            if (data != null) {
                if (data.getBooleanExtra("isRefresh", false)) {
                    viewModel.offset = 1
                    viewModel.getScritchCards(false)
                }
            }
        }
    }
}