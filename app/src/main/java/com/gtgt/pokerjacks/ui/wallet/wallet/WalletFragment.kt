package com.gtgt.pokerjacks.ui.wallet.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.wallet.bonus_distribution.BonusDistributionActivity
import com.gtgt.pokerjacks.ui.wallet.recent_transaction.RecentTransactionsActivity
import com.gtgt.pokerjacks.ui.wallet.withdraw.WithdrawActivity
import kotlinx.android.synthetic.main.fragment_wallet.*
import kotlinx.android.synthetic.main.toolbar_layout_nav.*

class WalletFragment : BaseFragment() {

    private var isExpanded = false

    //    private val viewModel: WalletViewModel by store()
//    private val paymentViewModel: PaymentViewModel by viewModel()
//    private val offersViewModel: OffersViewModel by viewModel()
    private val REQUESTCODE_PROMO = 101
    private val REQUESTCODE_PAYMENT = 2
    var bonus_config_id = ""
    var coupon_code = ""
    var bonus_percentage = 0.0
    var min_deposit_amount = 0.0
    var max_cashback = 0.0
    var sub_bonus_type = ""
    var userWinningsAmt = ""
    var winningAmt = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        tv_CommonTitleHome.text = "Wallet"
        iv_expandWallet.setOnClickListener {
            if (isExpanded) {
                iv_expandWallet.setImageResource(R.drawable.ic_more_gray)
                ll_walletDetails.visibility = View.GONE
                isExpanded = false
            } else {
                iv_expandWallet.setImageResource(R.drawable.ic_less_gray)
                ll_walletDetails.visibility = View.VISIBLE
                isExpanded = true
            }
        }

        tv_applyPromoCode.onOneClick {
//            launchActivity<CouponsActivty>(requestCode = REQUESTCODE_PROMO) { }
        }

        tv_removeOffer.onOneClick {
//            clearAppliedPromoCode()
        }

        cv_withdraw.onOneClick {
            launchActivity<WithdrawActivity> {
                putExtra("WinningsAmt", winningAmt)
            }
        }

        cv_recentTransactions.onOneClick {
            launchActivity<RecentTransactionsActivity> { }
        }

        iv_hb.onOneClick {
//            (activity as MainActivity).drawer_layout.openDrawer(GravityCompat.START)
        }

        btn_addMoney.onOneClick {
            /*if (retrieveBoolean("DENIED_ONLY")) {
                if (retrieveBoolean("DENIED_PERMANENTLY")) {

                } else {
                    (activity as HomeActivity).checkPermission()
                }
            } else {
                if (retrieveBoolean("BANNED_STATE", false)) {
                    context?.showBannedStatesBottomSheet()
                } else {
                    if (et_addMoney.text.toString().isNotEmpty())
                        getPaymentToken()
                }
            }*/
//            context?.showComingSoonDialog {  }
        }

        btn_amount1.onOneClick {
            et_addMoney.setText("1000")
        }

        btn_amount2.onOneClick {
            et_addMoney.setText("2000")
        }

        btn_amount3.onOneClick {
            et_addMoney.setText("5000")
        }

        cv_walletOffers.onOneClick {
            /*launchActivity<AllBonusActivity>(requestCode = REQUESTCODE_PROMO) {
                putExtra("POSITION", 0)
            }*/
        }

        cv_walletSC.onOneClick {
//            launchActivity<AllScratchCardActivity> { }
        }

        cv_bonusDist.onOneClick {
            launchActivity<BonusDistributionActivity> { }
        }
    }
}