package com.gtgt.pokerjacks.ui.profile.vrify_email

import android.os.Bundle
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.activity_verify_email.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class VerifyEmailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Email Verification"

        btn_get_otp.onOneClick {
            launchActivity<VerifyEmailOTPActivity> {  }
        }
    }
}