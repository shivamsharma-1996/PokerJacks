package com.gtgt.pokerjacks.ui.wallet.withdraw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.toolbar_layout.*

class WithdrawActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text="Withdraw"
    }
}