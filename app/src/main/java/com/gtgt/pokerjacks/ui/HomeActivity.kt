package com.gtgt.pokerjacks.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessaging
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.lobby.HomeViewModel
import com.gtgt.pokerjacks.ui.lobby.LobbyFragment
import com.gtgt.pokerjacks.ui.offers.offer.OffersFragment
import com.gtgt.pokerjacks.ui.profile.profile.ProfileFragment
import com.gtgt.pokerjacks.ui.profile.profile.viewModel.ProfileViewModel
import com.gtgt.pokerjacks.ui.profile.update_name.UpdateNameActivity
import com.gtgt.pokerjacks.ui.side_nav.SideNavFragment
import com.gtgt.pokerjacks.ui.tourneys.TourneysFragment
import com.gtgt.pokerjacks.ui.wallet.wallet.WalletFragment
import com.gtgt.pokerjacks.utils.Constants
import com.gtgt.pokerjacks.utils.GpsTracker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_nav_layout.*


class HomeActivity : BaseActivity(), View.OnClickListener {

    private val kycFragment by lazy { TourneysFragment() }
    private val offersFragment by lazy { OffersFragment() }
    private val lobbyFragment by lazy { LobbyFragment() }
    private val walletFragment by lazy { WalletFragment() }
    private val profileFragment by lazy { ProfileFragment() }
    private val sideNavFragment by lazy { SideNavFragment() }
    private var selectedScreen = 2
    private var number_of_clicks = 0
    var appliedPromoIntent: Intent? = null
    var popupBonusCode: String = ""
    val fadeout: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fade_out) }
    val fadeIn: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.fade_in) }
    private val profileViewModel: ProfileViewModel by store()
    private val homeViewModel: HomeViewModel by viewModel()
    private val REQUEST_CODE_LOCATION_PERMISSION = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        putBoolean("IS_USER_LOGIN", true)
        putPermanentString("OLD_MOBILE", retrieveString("MOBILE"))
        initUI()
    }

    private fun initUI() {
        fl_nav0.setOnClickListener(this)
        fl_nav1.setOnClickListener(this)
        fl_nav2.setOnClickListener(this)
        fl_nav3.setOnClickListener(this)
        fl_nav4.setOnClickListener(this)

        onLobbyClicked(isDefault = true)
        replaceFragment(sideNavFragment, R.id.side_nav, true)

        profileViewModel.getUserProfileDetailsInfo(false)
        profileViewModel.userProfileInfo.observe(this, Observer {
            if (!it.isUserNameUpdated) {
//                subscribeToTopic(Constants.TopicName.NEWUSER.name)
                launchActivity<UpdateNameActivity> { }
            }
            /*showPopup(isHome = true) { info ->
                when (info.popUpTargetName) {
                    Constants.PopupEvents.BONUS.name -> {
                        Log.i("popup_wallet", info.toString())
                        popupBonusCode=info.BonusCode
                        onWalletClicked()
                    }
                    Constants.PopupEvents.WEBPAGE.name -> {
                        Log.i("popup_web_page", info.toString())
                        popupBonusCode=""
                        launchActivity<WebViewActivity> {
                            putExtra("ACTIVITY_TITLE", info.web_page_title)
                            putExtra("ACTIVITY_URL", info.web_page_url)
                        }
                    }
                }
            }*/
        })

//        checkPermission()
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
                    onOffersClicked()
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

                    if (appliedPromoIntent != null) {
                        appliedPromoIntent = null
                        walletFragment.clearAppliedPromoCode()
                    }
                    onWalletClicked()
                }
            }
            R.id.fl_nav4 -> {
                if (!nav4.isSelected) {
                    nav4.startAnimation(fadeout)
                    nav4.startAnimation(fadeIn)
                    onProfileClicked()
                }
            }
        }
    }

    fun onProfileClicked() {
        replaceFragment(profileFragment, R.id.fl_homeContainer)
        setBottomBarSelected(4)
    }

    fun onWalletClicked() {
        replaceFragment(walletFragment, R.id.fl_homeContainer)
        setBottomBarSelected(3)
    }

    fun onLobbyClicked(isDefault: Boolean = false) {
        if (isDefault)
            replaceFragmentIfNoFragment(lobbyFragment, R.id.fl_homeContainer)
        else
            replaceFragment(lobbyFragment, R.id.fl_homeContainer)
        setBottomBarSelected(2)
    }

    fun onOffersClicked() {
        replaceFragment(offersFragment, R.id.fl_homeContainer)
        setBottomBarSelected(1)
    }

    fun onTourneysClicked() {
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
            drawer_layout.isDrawerOpen(GravityCompat.START) -> {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
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

    fun checkPermission() {
        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION_PERMISSION
                )
            } else {
                getLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLocation() {
        val gpsTracker = GpsTracker(this)
        if (gpsTracker.canGetLocation()) {
            val latitude: Double = gpsTracker.latitude
            val longitude: Double = gpsTracker.longitude

            putString("LATITUDE", latitude.toString())
            putString("LONGITUDE", longitude.toString())
            checkBannedState(latitude, longitude)

            putBoolean("DENIED_ONLY", false)
            putBoolean("DENIED_PERMANENTLY", false)
        } else {
            gpsTracker.showSettingsAlert()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permission = permissions[0]
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            putBoolean("DENIED_ONLY", true)
            val showRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                shouldShowRequestPermissionRationale(permission)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            if (!showRationale) {
                // show setting screen
                putBoolean("DENIED_PERMANENTLY", true)
//                gotoSettings()
            } else {
//                checkPermission()
            }
        }
    }

    private fun gotoSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CODE_LOCATION_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            checkPermission()
        }
    }

    private fun checkBannedState(latitude: Double, longitude: Double) {
        homeViewModel.checkBannedState(
            latitude = latitude,
            longitude = longitude
        ).observe(this, Observer {
            if (it.success) {
                putBoolean("BANNED_STATE", it.info)
                if (it.info) {
                    showBannedStatesDialog()
                }
            }
        })
    }

    private fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                /*var msg = getString(R.string.msg_subscribed) + topic
                if (!task.isSuccessful) {
                    msg = getString(R.string.msg_subscribe_failed) + topic
                }*/
                Log.d("topicStatusMsg", task.toString())
            }

    }
}