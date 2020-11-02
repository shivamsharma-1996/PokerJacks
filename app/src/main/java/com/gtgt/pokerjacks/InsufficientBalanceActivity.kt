package com.gtgt.pokerjacks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.toolbar_layout.*

class InsufficientBalanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insufficient_balance)

        tv_commonTitle.text = "Insufficient Balance"


    }
}