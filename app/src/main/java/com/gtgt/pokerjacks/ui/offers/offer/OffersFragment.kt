package com.gtgt.pokerjacks.ui.offers.offer

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.MainActivity
import com.gtgt.pokerjacks.ui.offers.bonus.CouponsActivty
import com.gtgt.pokerjacks.ui.offers.referral.AllReferralsActivity
import com.gtgt.pokerjacks.ui.offers.scratch_card.AllScratchCardActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bonus_info_popup.view.*
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.toolbar_layout_nav.*

class OffersFragment : BaseFragment() {

    /*private val bonusOffersAdapter by lazy {
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
    private var referralDataInfo: ArrayList<ReferralDataInfo>? = null*/
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
        /*viewModel.getUserOffers()
        viewModel.getScritchCardsList()
        viewModel.getUserReferralData()


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

        viewModel.offersResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                if (it.info!!.bonusCodes.isNullOrEmpty()) {
                    rv_BonusCards.visibility = View.GONE
                    tv_no_bonus_offers.visibility = View.VISIBLE
                } else {
                    it.info.bonusCodes?.let { it1 -> bonusOffersAdapter?.submitList(it1) }
                }

            } else {
                rv_BonusCards.visibility = View.GONE
                tv_no_bonus_offers.visibility = View.VISIBLE
            }
        })

        viewModel.scratchCardList.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                if (it.info.totalScratchCards.isNullOrEmpty()) {
                    rv_scratchCards.visibility = View.GONE
                    tv_no_scratch_cards.visibility = View.VISIBLE
                } else {
                    rv_scratchCards.visibility = View.VISIBLE
                    tv_no_scratch_cards.visibility = View.GONE
                    scratchCards = it.info.totalScratchCards as ArrayList<TotalScratchCards>
                    val scratchCardsAdapter by lazy {
                        context?.let {
                            ScratchCardsAdapter(it, ::onItemClicked)
                        }
                    }
                    rv_scratchCards.let {
                        it.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        it.adapter = scratchCardsAdapter
                    }
                    scratchCards?.let { it1 -> scratchCardsAdapter?.addList(it1) }
                }

                tv_bonusReceived.text =
                    "${getString(R.string.ruppe)}${(it.info.totalAmount ?: "0").toDecimalFormat()}"
            } else {
                rv_scratchCards.visibility = View.GONE
                tv_no_scratch_cards.visibility = View.VISIBLE
            }
        })

        viewModel.referralResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                if (it.info.isNullOrEmpty()) {
                    rv_referrals.visibility = View.GONE
                    tv_no_referrals.visibility = View.VISIBLE
                } else {
                    tv_no_referrals.visibility = View.GONE
                    rv_referrals.visibility = View.VISIBLE
                    referralsAdapter?.submitList(it.info)
                    referralDataInfo = it.info as ArrayList<ReferralDataInfo>
                }
            } else {
                rv_referrals.visibility = View.GONE
                tv_no_referrals.visibility = View.VISIBLE
                referralDataInfo = it.info as ArrayList<ReferralDataInfo>
            }
        })*/
    }
    private fun initUI() {

        /*rv_BonusCards.let {
            it.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = bonusOffersAdapter
        }

        rv_referrals.let {
            it.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = referralsAdapter
        }*/

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
                /*referralDataInfo?.let {
                    putExtra("REFERRAL_DATA", it as Serializable)
                }*/
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