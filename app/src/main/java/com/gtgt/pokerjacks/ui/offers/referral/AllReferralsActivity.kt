package com.gtgt.pokerjacks.ui.offers.referral

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import com.gtgt.pokerjacks.ui.offers.adapter.AllReferralsAdapter
import com.gtgt.pokerjacks.ui.offers.model.ReferralDataInfo
import kotlinx.android.synthetic.main.activity_all_referrals.*

class AllReferralsActivity : AppCompatActivity() {

    private val allReferralsAdapter by lazy { AllReferralsAdapter(this) }
    private val referralDataInfo by lazy { intent.getSerializableExtra("REFERRAL_DATA") as List<ReferralDataInfo>? }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_referrals)

        rv_allReferrals.adapter = allReferralsAdapter

        referralDataInfo?.let {
            if (it.isNullOrEmpty()) {
                rv_allReferrals.visibility = View.GONE
                ll_no_referrals.visibility = View.VISIBLE
            } else {
                rv_allReferrals.visibility = View.VISIBLE
                ll_no_referrals.visibility = View.GONE
                allReferralsAdapter.submitList(it)
                var referralRewards = 0.0
                it.forEach {
                    referralRewards += it.amount
                }
                tv_rewords.text = referralRewards.toDecimalFormat()
            }
        }

        iv_offerHB.onOneClick {
            onBackPressed()
        }
    }
}