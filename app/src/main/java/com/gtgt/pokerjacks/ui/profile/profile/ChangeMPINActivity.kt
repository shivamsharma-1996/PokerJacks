package com.gtgt.pokerjacks.ui.profile.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.profile.profile.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_change_m_p_i_n.*

class ChangeMPINActivity : BaseActivity() {

    private val profileViewModel: ProfileViewModel by viewModel()
    private var isPinHide = false
    private var isNewPinHide = false
    private var isConfirmPinHide = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_change_m_p_i_n)

        initUI()

        btn_changeMPIN.onOneClick {
            if (enter_mpin.text.toString().length == 4) {
                profileViewModel.validateMPIN(enter_mpin.text.toString()).observe(this, Observer {
                    if (it.success) {
                        ll_validateMPIN.visibility = View.GONE
                        ll_newMPIN.visibility = View.VISIBLE
                    } else {
                        ll_validateMPIN.visibility = View.VISIBLE
                        ll_newMPIN.visibility = View.GONE
                        showSnack(it.description)
                    }
                })
            }
        }

        btn_setNewMPIN.onOneClick {
            if (set_mpin.text.toString() == confirm_mpin.text.toString() && set_mpin.text.toString().length == 4) {
                profileViewModel.changeMPIN(confirm_mpin.text.toString()).observe(this, Observer {
                    showToast(it.description)
                    if (it.success) {
                        putPermanentString("MPIN", confirm_mpin.text.toString())
                        finish()
                    }
                })
            } else {
                showSnack("MPIN mismatch")
            }
        }
    }

    private fun initUI() {
        supportActionBar?.hide()

        iv_closeMPIN.onOneClick {
            onBackPressed()
        }

        iv_showenterPin.setOnClickListener {
            if (isPinHide) {
                enter_mpin.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                iv_showenterPin.setBackgroundResource(R.drawable.ic_show_pin)
                isPinHide = false
            } else {
                enter_mpin.inputType =
                    InputType.TYPE_CLASS_NUMBER
                iv_showenterPin.setBackgroundResource(R.drawable.ic_hide_pin)
                isPinHide = true
            }
        }

        iv_showSetPin.onOneClick {
            if (isNewPinHide) {
                set_mpin.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                iv_showSetPin.setBackgroundResource(R.drawable.ic_show_pin)
                isNewPinHide = false
            } else {
                set_mpin.inputType =
                    InputType.TYPE_CLASS_NUMBER
                iv_showSetPin.setBackgroundResource(R.drawable.ic_hide_pin)
                isNewPinHide = true
            }
        }

        iv_showConfirmPin.onOneClick {
            if (isConfirmPinHide) {
                confirm_mpin.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                iv_showConfirmPin.setBackgroundResource(R.drawable.ic_show_pin)
                isConfirmPinHide = false
            } else {
                confirm_mpin.inputType =
                    InputType.TYPE_CLASS_NUMBER
                iv_showConfirmPin.setBackgroundResource(R.drawable.ic_hide_pin)
                isConfirmPinHide = true
            }
        }
    }
}