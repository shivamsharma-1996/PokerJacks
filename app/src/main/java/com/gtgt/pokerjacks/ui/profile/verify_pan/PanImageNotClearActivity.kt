package com.gtgt.pokerjacks.ui.profile.verify_pan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.bitMap
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.showSnack
import com.gtgt.pokerjacks.extensions.store
import kotlinx.android.synthetic.main.activity_pan_image_not_clear.*
import kotlinx.android.synthetic.main.pan_verification_result_dialog.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class PanImageNotClearActivity : BaseActivity() {

    private val panViewModel: PanViewModel by store()

    var userPanNumber = ""
    var userPanName = ""
    var userDob = ""
    var reUploadedImg: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pan_image_not_clear)

        tv_toolbar_title.text = "PAN Verification"


        reUploadedImg = bitMap

        if (intent != null) {
            userPanName = intent.getStringExtra("ENTERED_USER_PAN_NAME") ?: ""
            userPanNumber = intent.getStringExtra("ENTERED_USER_PAN_NUMBER") ?: ""
            userDob = intent.getStringExtra("ENTERED_USER_DOB") ?: ""

            if (intent.hasExtra("DETECTED_USER_PAN_NAME")) {
                weRecognisedTV.visibility = View.VISIBLE
                weRecognisedLL.visibility = View.VISIBLE
                mistake.text = "The details you entered does not match"

                tv_recognised_pan_number.text =
                    intent.getStringExtra("DETECTED_USER_PAN_NUMBER") ?: ""
                tv_recognised_pan_user_name.text =
                    intent.getStringExtra("DETECTED_USER_PAN_NAME") ?: ""
                tv_recognised_pan_dob.text = intent.getStringExtra("DETECTED_USER_DOB") ?: ""
            }
        }

        tv_entered_pan_number.text = userPanNumber
        tv_entered_pan_user_name.text = userPanName
        tv_entered_pan_dob.text = userDob
        iv_upload_pan_image.setImageBitmap(reUploadedImg)

        btn_proceed.onOneClick {
            when {
                btn_reupload.isChecked -> {
                    finish()
                }
                btn_manual_verification.isChecked -> {
                    showManualVerificationDialog()
                }
                else -> {
                    showSnack("Please Select atleast one option to proceed")
                }
            }
        }
        panViewModel.uploadedPanDetails.observe(this, Observer {
            showSnack(it.message)
        })
    }

    @SuppressLint("RestrictedApi")
    private fun showManualVerificationDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.pan_verification_result_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        panViewModel.submitPanDetails(
            true,
            reUploadedImg!!,
            tv_entered_pan_user_name.text.toString(),
            tv_entered_pan_number.text.toString(),
            tv_entered_pan_dob.text.toString()
        ).observe(this, Observer {
            if (it.success) {
                dialogView.tv_verification_under_progress.visibility = View.GONE
                dialogView.tv_verifying.visibility = View.GONE
                dialogView.tv_successfully_verified.visibility = View.GONE
                dialogView.tv_verified.visibility = View.VISIBLE
                dialogView.tv_verified.playAnimation()

                dialogView.tv_manual_verification_submitted.visibility = View.VISIBLE

                Handler().postDelayed({
                    alertDialog.dismiss()
                    (intent.getParcelableExtra("resultReceiver") as ResultReceiver)?.send(
                        100,
                        Bundle()
                    )
                    finish()
                }, 2000)
            } else {
                alertDialog.dismiss()
                showSnack(it.description)
            }
        })
    }
}