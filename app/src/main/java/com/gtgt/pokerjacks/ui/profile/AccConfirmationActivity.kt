package com.gtgt.pokerjacks.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.toolbar_layout.*

class AccConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc_confirmation)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Bank Accounts"
    }
}