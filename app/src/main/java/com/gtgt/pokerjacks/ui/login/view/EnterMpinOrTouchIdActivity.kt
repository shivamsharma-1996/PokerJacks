package com.gtgt.pokerjacks.ui.login.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.HomeActivity
import com.gtgt.pokerjacks.ui.login.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.activity_enter_mpin_or_touchid.*
import kotlinx.android.synthetic.main.activity_enter_mpin_or_touchid.content

class EnterMpinOrTouchIdActivity : BaseActivity(), PopupMenu.OnMenuItemClickListener {

    private var isPinHide = false
    private val viewModel: LoginViewModel by viewModel()
    private var isManual = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_mpin_or_touchid)

        initUI()

        enter_mpin.setOnFocusChangeListener { v, hasFocus ->
            enter_mpin_touchId_blur_view.setBackgroundResource(if (hasFocus) R.color.dark else android.R.color.transparent)
        }

        btn_okay.onOneClick {
            if (enter_mpin.text.toString().length == 4) {
                callLoginApi(enter_mpin.text.toString())
            }
            isManual = true
        }

        enter_mpin.afterTextChanged {
            if (it.length == 4) {
                callLoginApi(enter_mpin.text.toString())
            }
            isManual = true
        }

        iv_fp.onOneClick {
            showBiometricPrompt(
                callback = {
                    putPermanentBoolean("IS_FP_ENABLE", true)
                    callLoginApi(retrievePermanentString("MPIN"))
                    isManual = false
                },
                cancelCallBack = {}
            )
        }

        viewModel.login.observe(this, Observer {
            if (it.success) {
                putModel("loginInfo", it.info)
                if (isManual) putPermanentString("MPIN", enter_mpin.text.toString())
                launchActivity<HomeActivity> { }
                finishAffinity()
            } else {
                it.description?.let { it1 -> showSnack(it1) }
                enter_mpin.text?.clear()
            }
        })
    }

    private fun callLoginApi(mpin: String) {
        viewModel.login(mpin)
    }

    private fun initUI() {
        content.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up))
        setBackgroundBlur(enter_mpin_touchId_blur_view)

        Handler().postDelayed({
            if (canAuthenticateWithBiometrics(this) && retrievePermanentBoolean("IS_FP_ENABLE")) {
                ll_fingerprint.visibility = View.VISIBLE
                showBiometricPrompt(
                    callback = {
                        putPermanentBoolean("IS_FP_ENABLE", true)
                        callLoginApi(retrievePermanentString("MPIN"))
                    },
                    cancelCallBack = {}
                )
            } else {
                ll_fingerprint.visibility = View.GONE
            }
        }, 1000)

        iv_showenterPin.onOneClick {
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

        iv_options.setOnClickListener {
            val popup = PopupMenu(this, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.options_menu, popup.menu)
            popup.setOnMenuItemClickListener(this)
            popup.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.forgotMPIN -> {
                forgotMPIN()
                true
            }
            R.id.contactUs -> {
                contactUs()
                true
            }
            else -> false
        }
    }

    private fun contactUs() {
        val email = arrayOf("support@Rummyjacks.com")
        sendEmail(email)
    }

    private fun forgotMPIN() {
        viewModel.forgotMPIN().observe(this, Observer {
            if (it.success) {
                launchActivity<OtpActivity> {
                    putExtra("MOBILE", retrieveString("MOBILE"))
                    putExtra("FROM_FORGOT_MPIN", true)
                }
            } else {
                showSnack(it.description)
            }
        })
    }

    private fun sendEmail(email: Array<String>) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email)

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            showToast("There is no email client installed.")
        }
    }
}