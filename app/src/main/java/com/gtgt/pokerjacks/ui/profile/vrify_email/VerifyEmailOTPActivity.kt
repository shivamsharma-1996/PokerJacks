package com.gtgt.pokerjacks.ui.profile.vrify_email

import android.os.Bundle
import com.github.salomonbrys.kotson.jsonObject
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import kotlinx.android.synthetic.main.activity_verify_email_o_t_p.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class VerifyEmailOTPActivity : BaseActivity() {

    var userEmail = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email_o_t_p)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Email Verification"

        if (intent != null) {
            userEmail = intent.getStringExtra("USER_EMAIL")!!
            tv_user_email.text = userEmail
        }

        btn_resend_otp_email.makeTextUnderline()

        btn_verify_email.onOneClick {
            if (enter_email_otp.text!!.isNotEmpty()) {
                apiServicesPlatform.verifyEmail(
                    jsonObject(
                        "otp" to enter_email_otp.text.toString(),
                        "email" to userEmail
                    )
                ).execute(this, true) {

                    if (it.success) {
                        launchActivity<VerifyEmailActivity> {
                            putExtra("EMAIL_VERIFIED", true)
                            putExtra("USER_EMAIL", it.info.email)
                        }
                        finish()
                    } else {
                        showSnack(it.errorDesc)
                    }

                }
            } else {
                showSnack("Please Enter OTP to Proceed")
            }
        }

        btn_resend_otp_email.onOneClick {
            apiServicesPlatform.updateUserEmailAddress(jsonObject("email" to userEmail)).execute {
                if (!it.success) {
                    showSnack(it.errorDesc)
                }
            }
        }
    }
}