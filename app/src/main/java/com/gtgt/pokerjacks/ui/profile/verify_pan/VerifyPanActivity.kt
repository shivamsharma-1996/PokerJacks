package com.gtgt.pokerjacks.ui.profile.verify_pan

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
import com.bruce.pickerview.popwindow.DatePickerPopWin
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.profile.verify_pan.model.PANErrorCodes
import com.gtgt.pokerjacks.ui.profile.verify_pan.model.UploadedPanDetailsInfo
import com.gtgt.pokerjacks.utils.ImagePickerActivity
import kotlinx.android.synthetic.main.activity_verify_pan.*
import kotlinx.android.synthetic.main.pan_verification_result_dialog.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.to

class VerifyPanActivity : ImagePickerActivity() {

    var panImage: Bitmap? = null

    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, get(Calendar.YEAR) - 18)
    }
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private val panViewModel: PanViewModel by viewModel()
    override fun onImagePicked(bitmap: Bitmap?) {
        if (bitmap != null) {
            iv_upload_pan_img.visibility = View.VISIBLE
            tv_upload_pan_img.visibility = View.GONE
            iv_upload_pan_img.setImageBitmap(bitmap)
            panImage = bitmap
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_pan)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_toolbar_title.text="PAN Verification"

        et_user_dob.onOneClick {
            val time = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(calendar.time)
            hideKeyboard()

            val datePickerDialog = DatePickerPopWin.Builder(
                this,
                DatePickerPopWin.OnDatePickedListener { year, month, day, dateDesc ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month - 1)
                    calendar.set(Calendar.DAY_OF_MONTH, day)

                    et_user_dob.text = dateFormat.format(calendar.time)

                }).textConfirm("CONFIRM") //text of confirm button
                .textCancel("CANCEL") //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(40) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(Calendar.getInstance().get(Calendar.YEAR) - 100)
                .maxYear(Calendar.getInstance().get(Calendar.YEAR) - 17) // max year in loop
                .showDayMonthYear(true) // shows like dd mm yyyy (default is false)
                .dateChose(time) // date chose when init popwindow
                .build()

            datePickerDialog.showPopWin(this)
        }

        img_upload_ll.onOneClick {
            choosePicture()
        }

        btn_submit_pan.onOneClick {
            try {
                when {
                    et_user_pan_number.text.isEmpty() -> {
                        showSnack("Please Enter Your Pan Number")
                    }
                    et_user_pan_name.text.isEmpty() -> {
                        showSnack("Please Enter Your Pan Name")
                    }
                    et_user_dob.text.isEmpty() -> {
                        showSnack("Please Enter Your DOB")
                    }
                    panImage == null -> {
                        showSnack("Please Upload Your Pan Card Image to Proceed")
                    }
                    else -> {
                        showPanVerificationDialog()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showPanVerificationDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.pan_verification_result_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        panViewModel.submitPanDetails(
            false,
            panImage!!,
            et_user_pan_name.text.toString(),
            et_user_pan_number.text.toString(),
            et_user_dob.text.toString()
        ).observe(this, androidx.lifecycle.Observer {
            if (it.success) {
                val info = it.info.to<UploadedPanDetailsInfo>()
                bitMap = panImage

                when (info.code) {
                    PANErrorCodes.VERIFICATION_SUCCESSFULLY.code -> {
                        dialogView.tv_verification_under_progress.visibility = View.GONE
                        dialogView.tv_verifying.visibility = View.GONE
                        dialogView.tv_successfully_verified.visibility = View.VISIBLE
                        dialogView.tv_verified.visibility = View.VISIBLE
                        dialogView.tv_verified.playAnimation()

                        Handler().postDelayed({
                            finish()
                            launchActivity<PanVerificationStatusActivity>()
                        }, 2000)
                    }
                    PANErrorCodes.NOT_MATCHED.code -> {
                        launchActivity<PanImageNotClearActivity> {
                            putExtra("ENTERED_USER_PAN_NAME", et_user_pan_name.text.toString())
                            putExtra("ENTERED_USER_PAN_NUMBER", et_user_pan_number.text.toString())
                            putExtra("ENTERED_USER_DOB", et_user_dob.text.toString())

                            putExtra("DETECTED_USER_PAN_NAME", info.Name)
                            putExtra("DETECTED_USER_PAN_NUMBER", info.Number)
                            putExtra("DETECTED_USER_DOB", info.DOB)

                            putExtra("resultReceiver", object : ResultReceiver(null) {
                                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                                    if (resultCode == 100) {
                                        finish()
                                    }
                                }
                            })
                        }
                    }
                    PANErrorCodes.IMG_NOT_CLEAR.code -> {
                        launchActivity<PanImageNotClearActivity> {
                            putExtra("ENTERED_USER_PAN_NAME", et_user_pan_name.text.toString())
                            putExtra("ENTERED_USER_PAN_NUMBER", et_user_pan_number.text.toString())
                            putExtra("ENTERED_USER_DOB", et_user_dob.text.toString())

                            putExtra("resultReceiver", object : ResultReceiver(null) {
                                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                                    if (resultCode == 100) {
                                        finish()
                                    }
                                }
                            })
                        }
                    }
                    else -> {
                        showSnack(it.description)
                    }
                }
            } else {
                when (it.errorCode) {
                    PANErrorCodes.CHOOSE_PAN.code -> {
                        showSnack(it.description)
                    }
                    PANErrorCodes.ASSIGNED_TO_OTHERS.code -> {
                        showSnack(it.description)
                    }
                    else -> {
                        showSnack(it.description)
                    }
                }
            }

            alertDialog.dismiss()
        })
    }
}