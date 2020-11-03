package com.gtgt.pokerjacks.ui.side_nav

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.MainActivity
import com.gtgt.pokerjacks.ui.lobby.HomeViewModel
import com.gtgt.pokerjacks.ui.login.view.RegistrationActivity
import com.gtgt.pokerjacks.ui.offers.bonus.AllBonusActivity
import com.gtgt.pokerjacks.ui.offers.scratch_card.AllScratchCardActivity
import kotlinx.android.synthetic.main.fragment_side_nav.*
import kotlinx.android.synthetic.main.logout_confirmation_dialog.view.*

class SideNavFragment : BaseFragment(), View.OnClickListener {

    private val REQUESTCODE_PROMO = 101

    //    private val profileViewModel: ProfileViewModel by sharedViewModel()
    private val homeViewModel: HomeViewModel by store()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_side_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_logout.onOneClick {
            onClickLogout()
        }

        ll_refer_earn.onOneClick {
            launchActivity<ReferAndEarnActivity> { }
        }

        /*if (!retrieveBoolean("forceUpdate", false) && !retrieveBoolean(
                "recommendedUpdate",
                false
            )
        ) {
            tv_navUpdate.text = "Current Version - ${BuildConfig.VERSION_NAME} (Up to date)"
            btn_updateFromNav.visibility = View.GONE
        } else {
            tv_navUpdate.text =
                "Current Version - ${BuildConfig.VERSION_NAME} (Update Available)"
            btn_updateFromNav.visibility = View.VISIBLE
            btn_updateFromNav.onOneClick {
                launchActivity<SplashActivity> { }
                activity?.finishAffinity()
            }
        }*/

        ll_TC.setOnClickListener(this)
        ll_pp.setOnClickListener(this)
        ll_support.setOnClickListener(this)
        ll_howToPlay.setOnClickListener(this)
        ll_scratchCards.setOnClickListener(this)
        ll_bonusAndPromo.setOnClickListener(this)

        openProfile.onOneClick {
            activity?.onBackPressed()
            (activity as MainActivity).onProfileClicked()
        }

        /*profileViewModel.userProfileInfo.observe(viewLifecycleOwner, Observer {
            try {
                name.text = it.username
                putBoolean("isUserBlocked",  it.user_status == "BLOCKED")
                putString("unblock_time", it.unblock_time?: "")
            } catch (ex: Exception) {

            }
        })*/
    }

    private fun onClickLogout() {

        //Creation of Logout confirmation dialog
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.logout_confirmation_dialog, null)
        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(context!!)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()


        dialogView.logout_btn_yes.setOnClickListener {
            homeViewModel.logout().observe(viewLifecycleOwner, Observer {
                if (it!!.success) {
                    putBoolean("IS_USER_LOGIN", false)
//                    unSubscribeFromTopic(Constants.TopicName.ALL.name)
//                    unSubscribeFromTopic(Constants.TopicName.NEWUSER.name)
//                    unSubscribeFromTopic(Constants.TopicName.PAIDUSER.name)

                    alertDialog.dismiss()

                    val intent = Intent(
                        context,
                        RegistrationActivity::class.java
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    activity!!.clearUserSavedData()
                    activity!!.finish()
                }
            })
        }

        dialogView.logout_btn_no.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.logout_close_btn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_support -> {
//                launchActivity<SupportActivity> { }
            }

            R.id.ll_pp -> {
                /*launchActivity<WebViewActivity> {
                    putExtra("ACTIVITY_TITLE", "Privacy Policy")
                    putExtra(
                        "ACTIVITY_URL",
                        MyApplication.sharedPreferencesDontClear.getString("privacy_policy", "")
                    )
                }*/
            }

            R.id.ll_TC -> {
                /*launchActivity<WebViewActivity> {
                    putExtra("ACTIVITY_TITLE", "Terms and Conditions")
                    putExtra(
                        "ACTIVITY_URL",
                        MyApplication.sharedPreferencesDontClear.getString(
                            "terms_conditions",
                            ""
                        )
                    )
                }*/
            }

            R.id.ll_howToPlay -> {
                /*launchActivity<WebViewActivity> {
                    putExtra("ACTIVITY_TITLE", "How to Play")
                    putExtra(
                        "ACTIVITY_URL",
                        MyApplication.sharedPreferencesDontClear.getString("game_guide", "")
                    )
                }*/
            }

            R.id.ll_scratchCards -> {
                launchActivity<AllScratchCardActivity> { }
            }

            R.id.ll_bonusAndPromo -> {
                launchActivity<AllBonusActivity>(requestCode = REQUESTCODE_PROMO) {
                    putExtra("POSITION", 0)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUESTCODE_PROMO) {
            data?.let {
                applyPromoCode(it)
            }
        }
    }

    private fun applyPromoCode(it: Intent) {
//        (context as MainActivity).appliedPromoIntent = it
//        (context as MainActivity).onWalletClicked()
    }
}