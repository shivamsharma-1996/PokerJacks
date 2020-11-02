package com.gtgt.pokerjacks.ui.login.view

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.login.viewModel.OTPViewModel
import com.gtgt.pokerjacks.utils.SmsReceiver
import kotlinx.android.synthetic.main.activity_otp.*

class OtpActivity : BaseActivity() {
    private val REQ_USER_CONSENT = 200

    private val viewModel: OTPViewModel by viewModel()
    private val mobileNumber by lazy { intent.getStringExtra("MOBILE") }
    private val FROM_FORGOT_MPIN by lazy { intent.getBooleanExtra("FROM_FORGOT_MPIN", false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        initUI()

        btn_verify_otp.onOneClick {
            val otp = otp_view.text.toString()
            if (otp.length == 4)
                viewModel.verifyOtp(otp)
            else
                showToast("OTP you've entered is incorrect, please re-check")
        }

        tv_resend_otp.onOneClick {
            viewModel.resendOtp(mobileNumber)
        }

        otp_view.setOnFocusChangeListener { v, hasFocus ->
            otp_blur_view.setBackgroundResource(if (hasFocus) R.color.dark else android.R.color.transparent)
        }

        otp_view.afterTextChanged {
            if (it.length == 4) {
                viewModel.verifyOtp(it)
            }
        }

        otp_view.onDone {
            val otp = otp_view.text.toString()
            if (otp.length == 4)
                viewModel.verifyOtp(otp)
            else
                showToast("You have entered an incorrect OTP")
        }

        viewModel.verifyOtpResponse.observe(this, Observer {
            if (it.success) {
                if (it.info.setMpin || retrievePermanentString("OLD_MOBILE") != retrieveString("MOBILE") || FROM_FORGOT_MPIN) {
                    launchActivity<CreateMpinActivity> {
                        putExtra("FROM_FORGOT_MPIN", FROM_FORGOT_MPIN)
                    }
                } else {
                    launchActivity<EnterMpinOrTouchIdActivity> { }
                }
            } else {
                it.description?.let { it1 -> showSnack(it1) }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        content.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up))
        setBackgroundBlur(otp_blur_view)
        tv_resend_otp.makeTextUnderline()
        tv_number.text = "OTP sent to\n$mobileNumber"
        putString("MOBILE", mobileNumber)
    }

    private val smsBroadcastReceiver = SmsReceiver {
        otp_view.setText(it)
//        viewModel.verifyOtp(it)
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver)
    }
}