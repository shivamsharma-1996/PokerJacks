package com.gtgt.pokerjacks.ui.profile.update_name

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.profile.profile.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_update_name.*

class UpdateNameActivity : BaseActivity() {
    private val profileViewModel: ProfileViewModel by viewModel()
    var isUserNameVerified = false
    var isReferralVerified = false
    var isUserNameChecked = false
    var isReferralChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_update_name)

        supportActionBar?.hide()
//        et_updateName.filters = arrayOf(filter)

        et_updateName.afterTextChanged {
            if (tv_errorMessage.visibility == View.VISIBLE) {
                tv_errorMessage.visibility = View.GONE
            }
            if (iv_nameStatus.visibility == View.VISIBLE) {
                iv_nameStatus.visibility = View.GONE
                isUserNameVerified = false
                isUserNameChecked = false
            }
            updateProceedButton()
        }

        et_referralCode.afterTextChanged {
            if (tv_errorMsgReferral.visibility == View.VISIBLE) {
                tv_errorMsgReferral.visibility = View.GONE
            }
            if (iv_referralStatus.visibility == View.VISIBLE) {
                iv_referralStatus.visibility = View.GONE
                isReferralVerified = false
                isReferralChecked = false
            }
        }

        /*et_referralCode.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                checkUserName()
            }
        }*/

        profileViewModel.checkName.observe(this, Observer {
            iv_nameStatus.visibility = View.VISIBLE
            if (it.success) {
                iv_nameStatus.setImageResource(R.drawable.ic_approved_20dp)
                tv_errorMessage.visibility = View.GONE
                isUserNameVerified = true
            } else {
                iv_nameStatus.setImageResource(R.drawable.ic_rejected_20dp)
                tv_errorMessage.visibility = View.VISIBLE
                tv_errorMessage.text = it.description
                isReferralVerified = false
            }
            updateProceedButton()
        })

        btn_updateName.setOnClickListener {
            val userName = et_updateName.text.toString().trim()
            val referralCode = et_referralCode.text.toString().trim()
            when {
                userName.isEmpty() -> showToast("Username cannot be empty")
                userName.length < 6 -> showToast("Username should have at least min 6 characters")
                else -> {
                    profileViewModel.updateUserName(userName, referralCode, isReferralVerified)
                    putString("USERNAME",userName)
                }
            }
        }

        profileViewModel.updateName.observe(this, Observer {
            showSnack(it.description)
            if (it.success) {
                finish()
            }
        })

        profileViewModel.checkReferral.observe(this, Observer {
            iv_referralStatus.visibility = View.VISIBLE
            if (it.success) {
                iv_referralStatus.setImageResource(R.drawable.ic_approved_20dp)
                tv_errorMsgReferral.visibility = View.GONE
                isReferralVerified = true
            } else {
                iv_referralStatus.setImageResource(R.drawable.ic_rejected_20dp)
                tv_errorMsgReferral.visibility = View.VISIBLE
                tv_errorMsgReferral.text = it.description
                isReferralVerified = false
            }
        })

        et_updateName.setOnFocusChangeListener { v, hasFocus ->
            log("focuscahnaged", hasFocus)
            if (!hasFocus) {
                checkUserName()
            }
        }

        et_referralCode.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                checkReferralCode()
            }
        }

        rl_popup.isKeyboardOpenOrClose {
            if (!it) {
                if (et_updateName.hasFocus()) {
                    et_updateName.clearFocus()
                } else if (et_referralCode.hasFocus()) {
                    et_referralCode.clearFocus()
                }
            }
        }
    }

    private fun checkReferralCode() {
        if (!et_referralCode.text.isNullOrEmpty() && !isReferralVerified) {
            profileViewModel.checkReferralCode(et_referralCode.text.toString())
        }
    }

    private fun checkUserName() {
        if (et_updateName.text.toString().length >= 6 && !isUserNameVerified) {
            profileViewModel.checkUserName(et_updateName.text.toString())
        } else {
            showToast("*Minimum 6 Characters, Maximum 12 Characters")
        }
    }

    private fun updateProceedButton() {
        if (!isUserNameVerified) {
            btn_updateName.isEnabled = false
            btn_updateName.alpha = 0.7f
        } else {
            btn_updateName.isEnabled = true
            btn_updateName.alpha = 1f
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

}
