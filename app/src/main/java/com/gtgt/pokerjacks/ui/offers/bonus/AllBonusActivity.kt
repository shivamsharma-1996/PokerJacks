package com.gtgt.pokerjacks.ui.offers.bonus

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.databinding.AllBonusItemsBinding
import com.gtgt.pokerjacks.extensions.changeDrawableGradientColor
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.showToast
import com.gtgt.pokerjacks.extensions.viewModel
import com.gtgt.pokerjacks.ui.offers.model.BonusCodes
import com.gtgt.pokerjacks.ui.offers.viewModel.OffersViewModel
import kotlinx.android.synthetic.main.activity_all_bonus.*
import kotlinx.android.synthetic.main.all_bonus_items.view.*
import kotlinx.android.synthetic.main.bonus_offer_items.view.*
import kotlinx.android.synthetic.main.bonus_offer_items.view.fl_bonusOfferCard
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlin.math.abs

class AllBonusActivity : BaseActivity() {

    private var isMore = false
    private var POSITION = 0
    private val viewModel: OffersViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_bonus)

        iv_closeBonus.onOneClick {
            onBackPressed()
        }

        if (intent != null) {
            POSITION = intent.getIntExtra("POSITION", 0)
        }

        viewModel.getUserOffers()
        viewModel.offersResponse.observe(this, Observer {
            if (it.success) {
                if (it.info != null) {
                    it.info.bonusCodes?.let { bonusCodes ->
                        initVP(bonusCodes as ArrayList<BonusCodes>)
                        bonus_vp.currentItem = POSITION
                        setBounsCount(position = POSITION, bonusCodes = bonusCodes)
                    }
                }
            } else {
                showToast("Sorry, requested data is not available in the system")
                finish()
            }
        })

        iv_prev.onOneClick(200) {
            bonus_vp.currentItem = bonus_vp.currentItem - 1
        }

        iv_next.onOneClick(200) {
            bonus_vp.currentItem = bonus_vp.currentItem + 1
        }
    }

    private fun initVP(bonusCodes: ArrayList<BonusCodes>?) {
        bonus_vp.clipToPadding = false
        bonusCodes?.let {
            bonus_vp.adapter = CustomPagerAdapter(this, it)
        }
        bonus_vp.pageMargin = 24
        bonus_vp.setPadding(48, 8, 48, 8)
        bonus_vp.offscreenPageLimit = 3
        val scaleFactor = 0.85f
        bonus_vp.setPageTransformer(
            false
        ) { page, position ->
            page.scaleY = 1 - abs(position) * (1 - scaleFactor)

            page.scaleX = scaleFactor + abs(position) * (1 - scaleFactor)
        }

        bonus_vp?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                POSITION = position
                iv_prev.isClickable = position != 0
                if (position == 0) {
                    iv_prev.visibility = View.INVISIBLE
                } else {
                    iv_prev.visibility = View.VISIBLE
                }
                if (position == bonusCodes?.size?.minus(1) ?: 0) {
                    iv_next.visibility = View.INVISIBLE
                } else {
                    iv_next.visibility = View.VISIBLE
                }
                iv_next.isClickable = position != bonusCodes?.size?.minus(1) ?: 0
                setBounsCount(position, bonusCodes)
            }
        })

        btn_applyCoupon.onOneClick {
            bonusCodes?.let {
                intent.putExtra("coupon_code", it[POSITION].bonus_code)
                intent.putExtra("bonus_config_id", it[POSITION].bonus_config_id)
                intent.putExtra("bonus_percentage", it[POSITION].bonus_percentage)
                intent.putExtra("min_deposit_amount", it[POSITION].min_deposit_amount)
                intent.putExtra("max_cashback", it[POSITION].max_cashback)
                intent.putExtra("sub_bonus_type", it[POSITION].sub_bonus_type)
                setResult(101, intent)
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun setBounsCount(
        position: Int,
        bonusCodes: ArrayList<BonusCodes>?
    ) {
        tv_bonusCount.text = "${position + 1} / ${bonusCodes?.size}"
    }

    inner class CustomPagerAdapter(
        private val mContext: Context,
        val bonusCodes: List<BonusCodes>
    ) : PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(mContext)

            val dataBindingUtil = DataBindingUtil.inflate<AllBonusItemsBinding>(
                inflater,
                R.layout.all_bonus_items,
                collection,
                false
            )

            dataBindingUtil.data = bonusCodes[position]

            val layout = dataBindingUtil.root as ViewGroup

            layout.tv_bonusDesc.text = bonusCodes[position].bonus_terms

            layout.tv_bonus.setOnClickListener {
                if (isMore) {
                    layout.tv_bonusDesc.visibility = View.GONE
                    isMore = false
                    layout.tv_bonus.text = "View More"
                    layout.tv_bonus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_white, 0)
                } else {
                    layout.tv_bonusDesc.visibility = View.VISIBLE
                    isMore = true
                    layout.tv_bonus.text = "View Less"
                    layout.tv_bonus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_less_white, 0)
                }
            }

            if (bonusCodes[position].color_code1.isNotEmpty() && bonusCodes[position].color_code2.isNotEmpty()) {
                layout.fl_bonusOfferCard.changeDrawableGradientColor(
                    bonusCodes[position].color_code1,
                    bonusCodes[position].color_code2,
                    R.drawable.blue_bg
                )
            }
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return bonusCodes.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

    }
}