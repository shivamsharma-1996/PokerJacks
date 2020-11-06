package com.gtgt.pokerjacks.ui.offers.offer

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.MainActivity
import com.gtgt.pokerjacks.ui.offers.adapter.BonusOffersAdapter
import com.gtgt.pokerjacks.ui.offers.adapter.ReferralsAdapter
import com.gtgt.pokerjacks.ui.offers.bonus.AllBonusActivity
import com.gtgt.pokerjacks.ui.offers.bonus.CouponsActivty
import com.gtgt.pokerjacks.ui.offers.model.ReferralDataInfo
import com.gtgt.pokerjacks.ui.offers.model.TotalScratchCards
import com.gtgt.pokerjacks.ui.offers.referral.AllReferralsActivity
import com.gtgt.pokerjacks.ui.offers.scratch_card.AllScratchCardActivity
import com.gtgt.pokerjacks.ui.offers.viewModel.OffersViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bonus_info_popup.view.*
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.toolbar_layout_nav.*
import java.io.Serializable

class OffersFragment : BaseFragment() {

    private val bonusOffersAdapter by lazy {
        context?.let {
            BonusOffersAdapter(it, onBonusItemClicked = { position ->
                launchActivity<AllBonusActivity>(requestCode = REQUESTCODE_PROMO) {
                    putExtra("POSITION", position)
                }
            })
        }
    }
    private val referralsAdapter by lazy { context?.let { ReferralsAdapter(it) } }

    private val viewModel: OffersViewModel by viewModel()
    private var scratchCards: ArrayList<TotalScratchCards>? = null
    private var referralDataInfo: ArrayList<ReferralDataInfo>? = null
    private val REQUESTCODE_PROMO = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {

        rv_BonusCards.let {
            it.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = bonusOffersAdapter
        }

        rv_referrals.let {
            it.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = referralsAdapter
        }

        iv_hb.onOneClick {
            (activity as MainActivity).drawer_layout.openDrawer(GravityCompat.START)
        }

        tv_viewAllSC.onOneClick {
            launchActivity<AllScratchCardActivity>(requestCode = 2) {}
        }

        tv_viewAllBC.onOneClick {
            launchActivity<CouponsActivty>(requestCode = REQUESTCODE_PROMO) { }
        }

        tv_viewAllReferrals.onOneClick {
            launchActivity<AllReferralsActivity> {
                referralDataInfo?.let {
                    putExtra("REFERRAL_DATA", it as Serializable)
                }
            }
        }

        tv_CommonTitleHome.text = "Offers"

        bonusInfo.onOneClick {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.bonus_info_popup, null)
            //Now we need an AlertDialog.Builder object
            val builder = AlertDialog.Builder(context)
            //setting the view of the builder to our custom view that we already inflated
            builder.setView(dialogView)

            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogView.btn_closeSD.onOneClick { alertDialog.dismiss() }

            alertDialog.show()

        }
    }

}