package com.gtgt.pokerjacks.ui.wallet.wallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.MainActivity
import com.gtgt.pokerjacks.ui.offers.bonus.AllBonusActivity
import com.gtgt.pokerjacks.ui.offers.bonus.CouponsActivty
import com.gtgt.pokerjacks.ui.offers.scratch_card.AllScratchCardActivity
import com.gtgt.pokerjacks.ui.offers.viewModel.OffersViewModel
import com.gtgt.pokerjacks.ui.wallet.bonus_distribution.BonusDistributionActivity
import com.gtgt.pokerjacks.ui.wallet.recent_transaction.RecentTransactionsActivity
import com.gtgt.pokerjacks.ui.wallet.withdraw.WithdrawActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import kotlinx.android.synthetic.main.toolbar_layout_nav.*
import java.io.Serializable
import kotlin.math.roundToInt

class WalletFragment : BaseFragment() {

    private val viewModel: WalletViewModel by viewModel()
    private var isExpanded = false
//    private val paymentViewModel: PaymentViewModel by store()
    private val offersViewModel: OffersViewModel by viewModel()
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
        setCodeFromOfferScreen()
        setCodeFromHome()
        /*(context as MainActivity).showPopup(isHome = false) { info ->
            when (info.popUpTargetName) {
                Constants.PopupEvents.BONUS.name -> {
                    Log.i("popup_wallet", info.toString())
                    (context as HomeActivity).popupBonusCode = info.BonusCode
                    setCodeFromHome()
                }
            }
        }*/

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

        /*paymentViewModel.createPaymentResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                launchActivity<PaytmPayment>(requestCode = REQUESTCODE_PAYMENT) {
                    putExtra("TOKEN_DATA", it.info as Serializable)
                }
            }
        })

        paymentViewModel.paymentStatus.observe(viewLifecycleOwner, Observer {
            it.info?.status?.let { it1 -> showToast(it1) }
            viewModel.getWalletDetailsByToken()
        })*/

        et_addMoney.afterTextChanged {
            if (bonus_config_id.isNotEmpty()) {
                if (it.isNotEmpty()) {
                    if (et_addMoney.text.toString().toInt() < min_deposit_amount.toInt()) {
                        btn_addMoney.isEnabled = false
                        btn_addMoney.alpha = 0.8f
                    } else {
                        btn_addMoney.isEnabled = true
                        btn_addMoney.alpha = 1f
                    }
                    applyPromoCode(
                        coupon_code,
                        bonus_config_id,
                        bonus_percentage,
                        min_deposit_amount,
                        max_cashback,
                        sub_bonus_type
                    )
                }
            }
        }
    }

    private fun initUI() {
        iv_expandWallet.setOnClickListener {
            if (isExpanded) {
                iv_expandWallet.setImageResource(R.drawable.ic_more_white)
                ll_walletDetails.visibility = View.GONE
                isExpanded = false
            } else {
                iv_expandWallet.setImageResource(R.drawable.ic_less_white)
                ll_walletDetails.visibility = View.VISIBLE
                isExpanded = true
            }
        }

        tv_applyPromoCode.onOneClick {
            launchActivity<CouponsActivty>(requestCode = REQUESTCODE_PROMO) { }
        }

        tv_removeOffer.onOneClick {
            clearAppliedPromoCode()
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
            context?.showComingSoonDialog {  }
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

        tv_CommonTitleHome.text = "Wallet"

        cv_walletOffers.onOneClick {
            launchActivity<AllBonusActivity>(requestCode = REQUESTCODE_PROMO) {
                putExtra("POSITION", 0)
            }
        }

        cv_walletSC.onOneClick {
            launchActivity<AllScratchCardActivity> { }
        }

        cv_bonusDist.onOneClick {
            launchActivity<BonusDistributionActivity> { }
        }
    }

    fun clearAppliedPromoCode() {
        (context as MainActivity).appliedPromoIntent = null

        ll_offerApplied.visibility = View.INVISIBLE
        tv_applyPromoCode.visibility = View.VISIBLE
        tv_promoCodeDesc.visibility = View.GONE
        et_addMoney.text.clear()
        bonus_config_id = ""
        btn_addMoney.isEnabled = true
    }

    private fun getPaymentToken() {
        /*if (bonus_config_id == "") {
            paymentViewModel.createPayment(
                amount = et_addMoney.text.toString(),
                promocode = null
            )
        } else {
            paymentViewModel.createPayment(
                amount = et_addMoney.text.toString(),
                promocode = bonus_config_id
            )
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUESTCODE_PROMO) {
            data?.let {
                clearAppliedPromoCode()
                callApplyPromo(it)
            }
        } else if (resultCode == REQUESTCODE_PAYMENT) {

            if (data?.getIntExtra("statusId", -2) == 2) {
                showToast("Transaction cancel on back clicked")
            } else {
                /*paymentViewModel.paymentstatus(
                    orderId = data?.getStringExtra("orderId").toString(),
                    status = data?.getStringExtra("status").toString(),
                    txnId = data?.getStringExtra("txnId").toString()
                )*/
            }
            clearAppliedPromoCode()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun applyPromoCode(
        couponCode: String,
        bonus_config_id: String,
        bonus_percentage: Double,
        min_deposit_amount: Double,
        max_cashback: Double,
        sub_bonus_type: String
    ) {
        if (bonus_config_id.isNotEmpty()) {
            ll_offerApplied.visibility = View.VISIBLE
            tv_applyPromoCode.visibility = View.INVISIBLE
            tv_successCode.text = couponCode
            if (et_addMoney.text.toString().isEmpty()) {
                if (sub_bonus_type.isNotEmpty() && sub_bonus_type == "Regular") {
                    val maxAmount = (100 / bonus_percentage) * max_cashback
                    et_addMoney.setText(maxAmount.toInt().toString())
                } else {
                    et_addMoney.setText(max_cashback.toInt().toString())
                }
            }

            setAppliedPromo(
                bonus_percentage,
                min_deposit_amount,
                max_cashback,
                sub_bonus_type
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun setAppliedPromo(
        bonus_percentage: Double,
        min_deposit_amount: Double,
        max_cashback: Double,
        sub_bonus_type: String
    ) {
        if (et_addMoney.text.toString().isNotEmpty()) {
            val amount = et_addMoney.text.toString().toDouble()

            tv_promoCodeDesc.visibility = View.VISIBLE
            if (amount < min_deposit_amount) {
                tv_promoCodeDesc.text =
                    "Minimum amount to be added to avail this offer is ${resources.getString(R.string.ruppe)}${min_deposit_amount.roundToInt()}"
                tv_promoCodeDesc.setTextColor(resources.getColor(R.color.red))
            } else {
                var discountAmount = (amount / 100) * bonus_percentage
                if (discountAmount > max_cashback) {
                    discountAmount = max_cashback
                }
                tv_promoCodeDesc.setTextColor(resources.getColor(R.color.text_blue))
                if (sub_bonus_type.isNotEmpty() && sub_bonus_type == "Regular") {
                    tv_promoCodeDesc.text =
                        "Bonus of ${resources.getString(R.string.ruppe)}${discountAmount.roundToInt()} /- will be issued"
                } else {
                    tv_promoCodeDesc.text =
                        "Scratch card of up to ${resources.getString(R.string.ruppe)}${max_cashback.roundToInt()} will be issued"
                }
            }
        }
    }

    fun setCodeFromOfferScreen() {
        if ((context as MainActivity).appliedPromoIntent != null) {
            (context as MainActivity).appliedPromoIntent?.let {
                clearAppliedPromoCode()
                callApplyPromo(it)
            }
        }
    }

    private fun setCodeFromHome() {
        if ((context as MainActivity).popupBonusCode.isNotEmpty()) {
            Log.i("popupBonusId", (context as MainActivity).popupBonusCode)
            offersViewModel.applyPromoCode((context as MainActivity).popupBonusCode)
            offersViewModel.applyPromoResponse.observe(viewLifecycleOwner, Observer {
                if (it.success) {
                    val intent = Intent()
                    intent.putExtra("coupon_code", it.info.bonus_code)
                    intent.putExtra("bonus_config_id", it.info.bonus_config_id)
                    intent.putExtra("bonus_percentage", it.info.bonus_percentage)
                    intent.putExtra("min_deposit_amount", it.info.min_deposit_amount)
                    intent.putExtra("max_cashback", it.info.max_cashback)
                    intent.putExtra("sub_bonus_type", it.info.sub_bonus_type)
                    clearAppliedPromoCode()
                    callApplyPromo(intent)
                } else {
                    showSnack(it.errorDesc)
                }
                (context as MainActivity).popupBonusCode = ""
            })
        }
    }

    fun callApplyPromo(intent: Intent) {
        coupon_code = intent.getStringExtra("coupon_code")!!
        bonus_config_id = intent.getStringExtra("bonus_config_id")!!
        bonus_percentage = intent.getDoubleExtra("bonus_percentage", 0.0)
        min_deposit_amount = intent.getDoubleExtra("min_deposit_amount", 0.0)
        max_cashback = intent.getDoubleExtra("max_cashback", 0.0)
        sub_bonus_type = intent.getStringExtra("sub_bonus_type")!!
        applyPromoCode(
            coupon_code,
            bonus_config_id,
            bonus_percentage,
            min_deposit_amount,
            max_cashback,
            sub_bonus_type
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            setCodeFromOfferScreen()
            viewModel.getWalletDetailsByToken()
        }
    }
}