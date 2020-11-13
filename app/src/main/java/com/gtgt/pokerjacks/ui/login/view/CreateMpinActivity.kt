package com.gtgt.pokerjacks.ui.login.view

import android.os.Bundle
import android.text.InputType
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.HomeActivity
import com.gtgt.pokerjacks.ui.login.viewModel.MPinViewModel
import kotlinx.android.synthetic.main.activity_create_mpin.*

class CreateMpinActivity : BaseActivity() {

    private val viewModel: MPinViewModel by viewModel()
    private var isPinHide = false
    private var isConfirmPinHide = false
    private val FROM_FORGOT_MPIN by lazy { intent.getBooleanExtra("FROM_FORGOT_MPIN", false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_mpin)

        initUI()

        btn_next.onOneClick {
            callMPINApi()
        }

        viewModel.mPinResponse.observe(this, Observer {
            if (it.success) {
                putPermanentString("MPIN", confirm_mpin.text.toString())
                putModel("loginInfo", it.info)
                if (canAuthenticateWithBiometrics(this)) {
                    launchActivity<SetFingerPrintActivity> { }
                } else {
                    launchActivity<HomeActivity> { }
                    finishAffinity()
                }
            } else {
                it.description?.let { it1 -> showSnack(it1) }
            }
        })

        confirm_mpin.onDone {
            callMPINApi()
        }


    }

    private fun callMPINApi() {
        if (set_mpin.text.toString() == confirm_mpin.text.toString() && !confirm_mpin.text.toString().isNullOrEmpty()) {
            if(FROM_FORGOT_MPIN){
                viewModel.resetMPIN(confirm_mpin.text.toString())
            }
            else{
                viewModel.createMPIN(confirm_mpin.text.toString())
            }
        } else {
            showSnack("MPIN mismatch")
        }
    }

    private fun initUI() {
        content.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up))
        setBackgroundBlur(set_mpin_blur_view)

        iv_showSetPin.onOneClick {
            //            set_mpin.transformationMethod = PasswordTransformationMethod.getInstance()
            if (isPinHide) {
                set_mpin.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                iv_showSetPin.setBackgroundResource(R.drawable.ic_show_pin)
                isPinHide = false
            } else {
                set_mpin.inputType =
                    InputType.TYPE_CLASS_NUMBER
                iv_showSetPin.setBackgroundResource(R.drawable.ic_hide_pin)
                isPinHide = true
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