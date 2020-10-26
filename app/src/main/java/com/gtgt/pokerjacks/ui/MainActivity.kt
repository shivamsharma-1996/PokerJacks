package com.gtgt.pokerjacks.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.replaceFragment
import com.gtgt.pokerjacks.extensions.replaceFragmentIfNoFragment
import com.gtgt.pokerjacks.extensions.showToast
import com.gtgt.pokerjacks.ui.kyc.KycFragment
import com.gtgt.pokerjacks.ui.lobby.LobbyFragment
import com.gtgt.pokerjacks.ui.offers.OffersFragment
import com.gtgt.pokerjacks.ui.profile.ProfileFragment
import com.gtgt.pokerjacks.ui.wallet.WalletFragment
import kotlinx.android.synthetic.main.bottom_nav_layout.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private val kycFragment by lazy { KycFragment() }
    private val offersFragment by lazy { OffersFragment() }
    private val lobbyFragment by lazy { LobbyFragment() }
    private val walletFragment by lazy { WalletFragment() }
    private val profileFragment by lazy { ProfileFragment() }
    private var selectedScreen = 2
    private var number_of_clicks = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {
        nav0.setOnClickListener(this)
        nav1.setOnClickListener(this)
        nav2.setOnClickListener(this)
        nav3.setOnClickListener(this)
        nav4.setOnClickListener(this)

        onLobbyClicked(isDefault = true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.nav0 -> {
                if (!nav0.isSelected)
                    onKycClicked()
            }
            R.id.nav1 -> {
                if (!nav1.isSelected)
                    onOffersClciked()
            }
            R.id.nav2 -> {
                if (!nav2.isSelected)
                    onLobbyClicked(isDefault = false)
            }
            R.id.nav3 -> {
                if (!nav3.isSelected)
                    onWalletClicked()
            }
            R.id.nav4 -> {
                if (!nav4.isSelected)
                    onProfileClciked()
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
            replaceFragmentIfNoFragment(offersFragment, R.id.fl_homeContainer)
        else
            replaceFragment(lobbyFragment, R.id.fl_homeContainer)
        setBottomBarSelected(2)
    }

    private fun onOffersClciked() {
        replaceFragment(offersFragment, R.id.fl_homeContainer)
        setBottomBarSelected(1)
    }

    private fun onKycClicked() {
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