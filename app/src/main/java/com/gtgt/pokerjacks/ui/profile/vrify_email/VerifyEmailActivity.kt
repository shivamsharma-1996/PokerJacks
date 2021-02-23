package com.gtgt.pokerjacks.ui.profile.vrify_email

import android.os.Bundle
import android.view.View
import com.github.salomonbrys.kotson.jsonObject
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.showSnack
import kotlinx.android.synthetic.main.activity_verify_email.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class VerifyEmailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_toolbar_title.text = "Email Verification"

        if (intent != null) {
            if (intent.getBooleanExtra("EMAIL_VERIFIED", false)) {
                et_user_email.setText(intent.getStringExtra("USER_EMAIL"))
                tv_email_verified.visibility = View.VISIBLE
                tv_enter_email.alpha = 0.6F
                et_user_email.alpha = 0.6F
                et_user_email.isEnabled = false
                btn_get_otp.visibility = View.GONE
            }
        }

        btn_get_otp.onOneClick {
            val email = et_user_email.text.toString().trim()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                apiServicesPlatform.updateUserEmailAddress(jsonObject("email" to email))
                    .execute(this, true) {

                        if (it.success) {
                            launchActivity<VerifyEmailOTPActivity> {
                                putExtra("USER_EMAIL", email)
                            }
                            finish()
                        } else {
                            showSnack(it.description)
                        }
                    }
            } else {
                showSnack("Please Enter Your Email Address to Proceed...")
            }
        }
    }
}