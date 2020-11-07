package com.gtgt.pokerjacks.ui.wallet.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.toDecimalFormat
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.MainActivity
import com.gtgt.pokerjacks.ui.offers.bonus.CouponsActivty
import com.gtgt.pokerjacks.ui.wallet.bonus_distribution.BonusDistributionActivity
import com.gtgt.pokerjacks.ui.wallet.recent_transaction.RecentTransactionsActivity
import com.gtgt.pokerjacks.ui.wallet.withdraw.WithdrawActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import kotlinx.android.synthetic.main.toolbar_layout_nav.*

class WalletFragment : BaseFragment() {

    private var isExpanded = false

    private val viewModel: WalletViewModel by viewModel()

    //        private val paymentViewModel: PaymentViewModel by viewModel()
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        viewModel.getWalletDetailsByToken()
        viewModel.walletDetailsResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                tv_totalBalance.text =
                    "${getString(R.string.ruppe)}${(it.info.deposits + it.info.winnings + it.info.bonus).toDecimalFormat()}"
                tv_unUtilizedCash.text =
                    "${getString(R.string.ruppe)}${it.info.deposits.toDecimalFormat()}"
                tv_winnings.text =
                    "${getString(R.string.ruppe)}${it.info.winnings.toDecimalFormat()}"
                userWinningsAmt = it.info.winnings.toDecimalFormat()
                winningAmt = it.info.winnings
                tv_usableBonus.text =
                    "${getString(R.string.ruppe)}${it.info.bonus.toDecimalFormat()}"
            }
        })
//
//        paymentViewModel.createPaymentResponse.observe(viewLifecycleOwner, Observer {
//            if (it.success) {
//                launchActivity<PaytmPayment>(requestCode = REQUESTCODE_PAYMENT) {
//                    putExtra("TOKEN_DATA", it.info as Serializable)
//                }
//            }
//        })
//
//        paymentViewModel.paymentStatus.observe(viewLifecycleOwner, Observer {
//            it.info?.status?.let { it1 -> showToast(it1) }
//            viewModel.getWalletDetailsByToken()
//        })

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
            (activity as MainActivity).drawer_layout.openDrawer(GravityCompat.START)
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