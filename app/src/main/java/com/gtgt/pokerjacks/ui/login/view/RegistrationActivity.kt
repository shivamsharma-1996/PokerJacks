package com.gtgt.pokerjacks.ui.login.view

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.login.viewModel.RegistrationViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    lateinit var googleApiClient: GoogleApiClient
    private val viewModel: RegistrationViewModel by viewModel()

    val RESOLVE_HINT = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initUI()

        btn_nxt.onOneClick {
            Dexter.withActivity(this)
                .withPermissions(Manifest.permission.RECEIVE_SMS)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        callCreateUserApi()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()


        }

        viewModel.createUserByMobileResponse.observe(this, Observer {
            if (it.success) {
                putString("USER_ID", it.info.user_unique_id)
                launchActivity<OtpActivity> {
                    putExtra("MOBILE", "${et_mobileNumber.text}")
                    putExtra("FROM_FORGOT_MPIN", false)
                }
            } else {
                it.description?.let { it1 -> showToast(it1) }
            }
        })

        et_mobileNumber.setOnFocusChangeListener { v, hasFocus ->
            reg_blurView.setBackgroundResource(if (hasFocus) R.color.dark else android.R.color.transparent)
        }

        et_mobileNumber.afterTextChanged {
            btn_nxt.isEnabled = it.length == 10

            btn_nxt.alpha = if (it.length == 10) 1f else 1f
        }

        et_mobileNumber.onDone {
            callCreateUserApi()
        }


        //create a google client builder

        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .enableAutoManage(this, this)
            .addOnConnectionFailedListener(this)
            .addApi(Auth.CREDENTIALS_API)
            .build()

        requestPhoneNumberHint()


    }

    private fun initUI() {
        content.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up))
        setBackgroundBlur(reg_blurView)
    }

    private fun callCreateUserApi() {
        val mobileNumber = et_mobileNumber.text.toString()
        if (mobileNumber.isNotEmpty()) {
            if (mobileNumber.isValidPhoneNumber())
                viewModel.createUserByMobile(mobileNumber)
            else
                showSnack("You have entered an invalid Mobile Number")
        }
    }

    private fun requestPhoneNumberHint() {

        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        //create a pending intent
        val pendingIntent: PendingIntent =
            Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest)

        try {
            startIntentSenderForResult(pendingIntent.intentSender, RESOLVE_HINT, null, 0, 0, 0)

        } catch (e: Exception) {

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                val phoneNumber: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)
                et_mobileNumber.setText(phoneNumber.id.removePrefix("+91"))
                btn_nxt.performClick()
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnected(p0: Bundle?) {

    }


}
