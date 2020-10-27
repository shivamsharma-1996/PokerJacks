package com.gtgt.pokerjacks.ui.profile.vrify_email

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.toolbar_layout.*

class VerifyEmailOTPActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email_o_t_p)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Email Verification"
    }
}