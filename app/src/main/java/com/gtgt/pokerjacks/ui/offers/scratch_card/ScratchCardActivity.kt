package com.gtgt.pokerjacks.ui.offers.scratch_card

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.databinding.ActivityScratchCardBinding
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.offers.model.TotalScratchCards
import com.gtgt.pokerjacks.ui.offers.viewModel.ScratchCardViewModel
import kotlinx.android.synthetic.main.activity_scratch_card.*

class ScratchCardActivity : BaseActivity() {

    private val viewModel: ScratchCardViewModel by viewModel()
    private val scratchCardItem by lazy { intent.getSerializableExtra("SCRATCH_CARD_ITEM") as TotalScratchCards? }
    private var isRefresh = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        DataBindingUtil.setContentView<ActivityScratchCardBinding>(
            this,
            R.layout.activity_scratch_card
        ).also {
            it.viewModel = this

            scratchCardItem?.let { scratchCardItem ->
                it.data = scratchCardItem
            }
        }

        scratchCardItem?.let { scratchCardItem ->
            if (scratchCardItem.card_type == "Open") {
                tv_winSc.text = "You won\n₹${scratchCardItem.formattedIssuedAmount}"

                fl_scWin.visibility = View.VISIBLE
                fl_scLoss.visibility = View.GONE
            } else {
                if (scratchCardItem.bonus_amount.toInt() > 0) {
                    fl_scWin.visibility = View.VISIBLE
                    fl_scLoss.visibility = View.GONE

                    tv_winSc.text = "You won\n₹${scratchCardItem.formattedBonusAmount}"
                } else {
                    fl_scWin.visibility = View.GONE
                    fl_scLoss.visibility = View.GONE
                }
            }

            setDescription(
                desc1 = scratchCardItem.card_description1,
                desc2 = scratchCardItem.card_description2
            )
        }

        supportActionBar?.hide()

        scratchCard.setOnScratchListener { scratchCard, visiblePercent ->
            if (visiblePercent > 0.15) {
                scratchCard.visibility = View.GONE
            }
            scratchCardItem?.let {
                viewModel.openScratchCard(it.user_bonus_master_id)
            }
            isRefresh = true
        }

        viewModel.openScratchCardResponse.observe(this, Observer {
            if (it.info?.issued_amount!!.toInt() <= 0) {
                tv_description1.text = "Oops!"
                tv_description2.visibility = View.INVISIBLE

                fl_scWin.visibility = View.GONE
                fl_scLoss.visibility = View.VISIBLE
            } else {
                setDescription(
                    desc1 = it.info.card_description1,
                    desc2 = it.info.card_description2
                )

                tv_winSc.text = "You won\n₹${it.info.formattedIssuedAmount}"

                fl_scWin.visibility = View.VISIBLE
                fl_scLoss.visibility = View.GONE
            }
            isRefresh = true
        })
    }

    private fun setDescription(desc1: String, desc2: String) {
        if (desc1 == desc2) {
            tv_description1.text = desc1
            tv_description2.visibility = View.INVISIBLE
        } else {
            tv_description1.text = desc1
            tv_description2.text = desc2
        }
    }

    fun onClose() {
        onBackPressed()
    }

    override fun onBackPressed() {
        setResult(1, intent.putExtra("isRefresh", isRefresh))
        finish()
        ActivityCompat.finishAfterTransition(this)
    }
}