package com.gtgt.pokerjacks.ui.side_nav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.toolbar_layout.*

class ReferAndEarnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_and_earn)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Refer & Earn"
    }
}