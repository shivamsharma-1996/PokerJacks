package com.gtgt.pokerjacks.ui.profile.update_name

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity

class UpdateNameActivity : BaseActivity() {
//    private val profileViewModel: ProfileViewModel by viewModel()
    var isUserNameVerified = false
    var isReferralVerified = false
    var isUserNameChecked = false
    var isReferralChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_update_name)
    }
}