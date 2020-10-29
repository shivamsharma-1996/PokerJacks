package com.gtgt.pokerjacks.ui.offers.bonus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity

class CouponsActivty : BaseActivity() {
    /*private val couponsAdapter by lazy {
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
    private val REQUESTCODE_PROMO = 101*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons_activty)
    }
}