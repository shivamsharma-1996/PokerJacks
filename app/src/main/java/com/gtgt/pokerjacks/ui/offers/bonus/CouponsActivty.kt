package com.gtgt.pokerjacks.ui.offers.bonus

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.offers.adapter.CouponsAdapter
import com.gtgt.pokerjacks.ui.offers.model.BonusCodes
import com.gtgt.pokerjacks.ui.offers.viewModel.OffersViewModel
import kotlinx.android.synthetic.main.activity_coupons_activty.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

class CouponsActivty : BaseActivity() {
    private val couponsAdapter by lazy {
        CouponsAdapter(this, onApplyClicked = {
            viewModel.applyPromoCode(it.bonus_code)
        },
            onItemClicked = { position ->
                launchActivity<AllBonusActivity>(requestCode = REQUESTCODE_PROMO) {
                    putExtra("POSITION", position)
                }
            })
    }
    private val viewModel: OffersViewModel by viewModel()
    private var couponsCodes: List<BonusCodes>? = null
    private val REQUESTCODE_PROMO = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons_activty)

        iv_back.onOneClick {
            onBackPressed()
        }
        tv_toolbar_title.text = "Available Coupons"
        rv_coupons.adapter = couponsAdapter

        viewModel.getUserOffers()
        viewModel.offersResponse.observe(this, androidx.lifecycle.Observer {
            if (it.success) {
                if (it.info!!.bonusCodes.isNullOrEmpty()) {
                    rv_coupons.visibility = View.GONE
                    ll_no_coupons.visibility = View.VISIBLE
                } else {
                    rv_coupons.visibility = View.VISIBLE
                    ll_no_coupons.visibility = View.GONE
                    it.info.bonusCodes?.let {
                        couponsCodes = it
                    }
                    couponsCodes?.let { it1 -> couponsAdapter.addList(it1) }
                }

            } else {
                rv_coupons.visibility = View.GONE
                ll_no_coupons.visibility = View.VISIBLE
                showSnack(it.errorDesc)
            }
        })

        viewModel.applyPromoResponse.observe(this, androidx.lifecycle.Observer {
            if (it.success) {
                intent.putExtra("coupon_code", it.info.bonus_code)
                intent.putExtra("bonus_config_id", it.info.bonus_config_id)
                intent.putExtra("bonus_percentage", it.info.bonus_percentage)
                intent.putExtra("min_deposit_amount", it.info.min_deposit_amount)
                intent.putExtra("max_cashback", it.info.max_cashback)
                intent.putExtra("sub_bonus_type", it.info.sub_bonus_type)
                setResult(101, intent)
                finish()
            } else {
                showSnack(it.errorDesc)
            }
        })

        tv_applyPromoCode.onOneClick {
            if (et_promoCode.text.toString().isNotEmpty()) {
                viewModel.applyPromoCode(et_promoCode.text.toString())
            }
        }

        et_promoCode.afterTextChanged {
            var s: String = it
            if (s != s.toUpperCase(Locale.getDefault())) {
                s = s.toUpperCase(Locale.getDefault())
                et_promoCode.setText(s)
                et_promoCode.setSelection(s.length)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUESTCODE_PROMO) {
            data?.let {
                setResult(101, data)
                finish()
            }
        }
    }
}