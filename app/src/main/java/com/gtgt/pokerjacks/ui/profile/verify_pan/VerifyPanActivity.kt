package com.gtgt.pokerjacks.ui.profile.verify_pan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.toolbar_layout.*

class VerifyPanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_pan)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text="PAN Verification"
    }
}