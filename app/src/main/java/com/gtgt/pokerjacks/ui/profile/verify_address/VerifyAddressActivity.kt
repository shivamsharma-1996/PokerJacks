package com.gtgt.pokerjacks.ui.profile.verify_address

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.toolbar_layout.*

class VerifyAddressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_address)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Address Verification"
    }
}