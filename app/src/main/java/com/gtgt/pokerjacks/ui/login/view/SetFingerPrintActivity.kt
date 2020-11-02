package com.gtgt.pokerjacks.ui.login.view

import android.os.Bundle
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.MainActivity
import kotlinx.android.synthetic.main.activity_set_fingerprint.*

class SetFingerPrintActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_fingerprint)

        setBackgroundBlur(set_fingerPrint_blur_view)

        btn_enable_fingerPrint.onOneClick {
            if (canAuthenticateWithBiometrics(this)) {  // Check whether this device can authenticate with biometrics
                showBiometricPrompt(
                    callback = {
                        putPermanentBoolean("IS_FP_ENABLE", true)
                        callLoginApi()
                    },
                    cancelCallBack = {}
                )
            } else {
                // Cannot use biometric prompt
                showSnack("Cannot use biometric")
            }
        }

        tv_skip_now.onOneClick {
            putPermanentBoolean("IS_FP_ENABLE", false)
            callLoginApi()
        }

    }

    private fun callLoginApi() {
        launchActivity<MainActivity> { }
        finishAffinity()
    }
}