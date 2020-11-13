package com.gtgt.pokerjacks.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.replaceFragment
import com.gtgt.pokerjacks.extensions.replaceFragmentIfNoFragment
import com.gtgt.pokerjacks.extensions.showToast
import com.gtgt.pokerjacks.ui.lobby.view.LobbyFragment
import com.gtgt.pokerjacks.ui.offers.offer.OffersFragment
import com.gtgt.pokerjacks.ui.profile.profile.ProfileFragment
import com.gtgt.pokerjacks.ui.tourneys.TourneysFragment
import com.gtgt.pokerjacks.ui.wallet.wallet.WalletFragment
import kotlinx.android.synthetic.main.bottom_nav_layout.*


class MainActivity : BaseActivity(), View.OnClickListener {

    private val kycFragment by lazy { TourneysFragment() }
    private val offersFragment by lazy { OffersFragment() }
    private val lobbyFragment by lazy { LobbyFragment() }
    private val walletFragment by lazy { WalletFragment() }
    private val profileFragment by lazy { ProfileFragment() }
    private var selectedScreen = 2
    private var number_of_clicks = 0
    val fadeout: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fade_out) }
    val fadeIn: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fade_in) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {
        fl_nav0.setOnClickListener(this)
        fl_nav1.setOnClickListener(this)
        fl_nav2.setOnClickListener(this)
        fl_nav3.setOnClickListener(this)
        fl_nav4.setOnClickListener(this)

        onLobbyClicked(isDefault = true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_nav0 -> {
                if (!nav0.isSelected) {
                    nav0.startAnimation(fadeout)
                    nav0.startAnimation(fadeIn)
                    onTourneysClicked()
                }
            }
            R.id.fl_nav1 -> {
                if (!nav1.isSelected) {
                    nav1.startAnimation(fadeout)
                    nav1.startAnimation(fadeIn)
                    onOffersClciked()
                }
            }
            R.id.fl_nav2 -> {
                if (!nav2.isSelected) {
                    nav2.startAnimation(fadeout)
                    nav2.startAnimation(fadeIn)
                    onLobbyClicked(isDefault = false)
                }
            }
            R.id.fl_nav3 -> {
                if (!nav3.isSelected) {
                    nav3.startAnimation(fadeout)
                    nav3.startAnimation(fadeIn)
                    onWalletClicked()
                }
            }
            R.id.fl_nav4 -> {
                if (!nav4.isSelected) {
                    nav4.startAnimation(fadeout)
                    nav4.startAnimation(fadeIn)
                    onProfileClciked()
                }
            }
        }
    }

    private fun onProfileClciked() {
        replaceFragment(profileFragment, R.id.fl_homeContainer)
        setBottomBarSelected(4)
    }

    private fun onWalletClicked() {
        replaceFragment(walletFragment, R.id.fl_homeContainer)
        setBottomBarSelected(3)
    }

    private fun onLobbyClicked(isDefault: Boolean = false) {
        if (isDefault)
            replaceFragmentIfNoFragment(lobbyFragment, R.id.fl_homeContainer)
        else
            replaceFragment(lobbyFragment, R.id.fl_homeContainer)
        setBottomBarSelected(2)
    }

    private fun onOffersClciked() {
        replaceFragment(offersFragment, R.id.fl_homeContainer)
        setBottomBarSelected(1)
    }

    private fun onTourneysClicked() {
        replaceFragment(kycFragment, R.id.fl_homeContainer)
        setBottomBarSelected(0)
    }

    private fun setBottomBarSelected(position: Int) {
        selectedScreen = position
        Log.i("setBottomBarSelected", "$selectedScreen")
        for (i in 0..4) {
            val buttonID = "nav$i"
            val resID = resources.getIdentifier(buttonID, "id", packageName)
            (findViewById<ImageButton>(resID)).isSelected = position == i

            (findViewById<ImageButton>(resID)).apply {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                requestLayout()
            }

            /*if (position != i) {
                (findViewById<ImageButton>(resID)).apply {
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    requestLayout()
                }
            } else {
                (findViewById<ImageButton>(resID)).apply {
                    layoutParams.width = dpToPx(112)
                    layoutParams.height = dpToPx(44)
                    requestLayout()
                }
            }*/

//            nav0.clearAnimation()
//            nav1.clearAnimation()
//            nav2.clearAnimation()
//            nav3.clearAnimation()
//            nav4.clearAnimation()
        }
    }

    override fun onBackPressed() {
        when {
            /*drawer_layout.isDrawerOpen(GravityCompat.START) -> {
                drawer_layout.closeDrawer(GravityCompat.START)
            }*/
            selectedScreen == 2 -> {
                number_of_clicks++
                Handler().postDelayed({ number_of_clicks = 0 }, 2500)
                if (number_of_clicks == 2) {
                    super.onBackPressed()
                } else {
                    showToast("Please click BACK again to exit")
                }
            }
            else -> {
                onLobbyClicked(isDefault = false)
            }
        }
    }
}