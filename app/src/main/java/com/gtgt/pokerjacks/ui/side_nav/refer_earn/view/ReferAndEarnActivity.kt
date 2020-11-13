package com.gtgt.pokerjacks.ui.side_nav.refer_earn.view

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.profile.profile.viewModel.ProfileViewModel
import com.gtgt.pokerjacks.ui.side_nav.refer_earn.viewModel.ReferAndEarnViewModel
import com.gtgt.pokerjacks.ui.webView.WebViewActivity
import kotlinx.android.synthetic.main.activity_refer_and_earn.*
import kotlinx.android.synthetic.main.apply_referal_popup.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ReferAndEarnActivity : BaseActivity() {
    private val viewModel: ReferAndEarnViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private var dialogView: View? = null
    private var alertDialog: AlertDialog? = null
    var isReferralVerified = false
    var isReferralChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_and_earn)

        supportActionBar?.hide()
        tv_commonTitle.text = "Refer & Earn"
        iv_back.onOneClick {
            onBackPressed()
        }

        viewModel.getReferralCode().observe(this, Observer {
            if (it.success) {
                tv_referralCode.text = it.info.code

                btn_refer_earn.onOneClick {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey, It's Rummy Time! Sign-up on the RummyJacks App to Get upto Rs.1000/- Sign-up Bonus & an additional Rs.2000/- Bonus on your First Payment!\n\nUse Referral code ${it.info.code} after Sign-Up!\nDownload now at https://rummyjacks.app.link/51dv2CcNJab"
                        )
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }
        })

        viewModel.checkReferralEligible().observe(this, Observer {
            if (it) {
                dialogView =
                    LayoutInflater.from(this).inflate(R.layout.apply_referal_popup, null)
                //Now we need an AlertDialog.Builder object
                val builder = android.app.AlertDialog.Builder(this)
                //setting the view of the builder to our custom view that we already inflated
                builder.setView(dialogView)

                alertDialog = builder.create()
                alertDialog?.setCancelable(false)
                alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialogView?.logout_close_btn?.onOneClick {
                    alertDialog?.dismiss()
                }

                dialogView?.et_referralCode?.afterTextChanged {
                    dialogView?.let {
                        if (it.tv_errorMsgReferral.visibility == View.VISIBLE) {
                            it.tv_errorMsgReferral.visibility = View.GONE
                        }
                        if (it.iv_referralStatus.visibility == View.VISIBLE) {
                            it.iv_referralStatus.visibility = View.GONE
                            isReferralVerified = false
                            isReferralChecked = false
                        }
                    }
                    updateApplyButton()
                }

                dialogView?.btn_applyCode?.onOneClick {
                    if (isReferralVerified) {
                        viewModel.applyReferralCode(dialogView?.et_referralCode?.text.toString())
                            .observe(this, Observer {
                                if (it.success) {
                                    alertDialog?.dismiss()
                                } else {
                                    showToast(it.description)
                                }
                            })
                    } else {
                        showToast("Verify referral code")
                    }
                }

                alertDialog?.show()
            }
        })

        rl_refer_earn.isKeyboardOpenOrClose { isOpen ->
            dialogView?.let {
                if (!isOpen && dialogView?.et_referralCode?.hasFocus()!! && !isReferralChecked) {
                    checkReferralCode()
                    isReferralChecked = true
                }
            }
        }

        profileViewModel.checkReferral.observe(this, Observer { checkReferral ->
            dialogView?.let {
                it.iv_referralStatus.visibility = View.VISIBLE
                if (checkReferral.success) {
                    it.iv_referralStatus.setImageResource(R.drawable.ic_approved)
                    it.tv_errorMsgReferral.visibility = View.GONE
                    isReferralVerified = true
                } else {
                    it.iv_referralStatus.setImageResource(R.drawable.ic_rejected)
                    it.tv_errorMsgReferral.visibility = View.VISIBLE
                    it.tv_errorMsgReferral.text = checkReferral.description
                    isReferralVerified = false
                }
            }
        })

        ll_tc.onOneClick {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("ACTIVITY_TITLE", "Terms and Conditions")
            intent.putExtra(
                "ACTIVITY_URL",
                MyApplication.sharedPreferencesDontClear.getString("refer_terms_conditions", "")
            )
            startActivity(intent)
        }
    }

    private fun checkReferralCode() {
        if (!dialogView?.et_referralCode?.text.isNullOrEmpty() && !isReferralVerified) {
            profileViewModel.checkReferralCode(dialogView?.et_referralCode?.text.toString())
        }
    }

    private fun updateApplyButton() {
        dialogView?.let {
            if (!isReferralVerified) {
                it.btn_applyCode.isEnabled = false
                it.btn_applyCode.alpha = 0.7f
            } else {
                it.btn_applyCode.isEnabled = true
                it.btn_applyCode.alpha = 1f
            }
        }
    }

    fun copyContestCode(view: View) {
        val myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip = ClipData.newPlainText("text", tv_referralCode.text)
        myClipboard.setPrimaryClip(myClip)

        (view as TextView).apply {
            text = "Copied"
            setTextColor(Color.parseColor("#41a753"))
        }
    }

    override fun onStop() {
        super.onStop()
        alertDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}