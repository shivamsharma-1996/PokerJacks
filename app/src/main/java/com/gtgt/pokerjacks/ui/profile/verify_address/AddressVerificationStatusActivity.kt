package com.gtgt.pokerjacks.ui.profile.verify_address

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.string
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.utils.Constants
import kotlinx.android.synthetic.main.activity_address_verification_status.*
import kotlinx.android.synthetic.main.activity_verify_address.*
import kotlinx.android.synthetic.main.activity_verify_address.btn_submit_address
import kotlinx.android.synthetic.main.activity_verify_address.et_user_pincode
import kotlinx.android.synthetic.main.activity_verify_address.tv_address_verification_status
import kotlinx.android.synthetic.main.activity_verify_address.tv_document_type
import kotlinx.android.synthetic.main.fragment_bank_statement_uploader.*
import kotlinx.android.synthetic.main.fragment_bank_statement_uploader.iv_upload_bank_statement
import kotlinx.android.synthetic.main.toolbar_layout.*

class AddressVerificationStatusActivity : BaseActivity() {

    fun getPincodeAddress() {
        apiServicesPlatform.updateLocation(jsonObject("pincode" to et_user_pincode.text.toString()))
            .execute(this, true) { it ->
                if (it.success) {
                    tv_place.visibility = View.VISIBLE
                    tv_place.text = it.info["address"].string
                } else {
                    tv_place.visibility = View.VISIBLE
                    tv_place.text =
                        "Oops! PIN not recognized. If your PIN code is not listed please enter 000000 and proceed"
                    tv_place.setTextColor(Color.RED)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_verification_status)

        iv_back.onOneClick {
            finish()
        }

        supportActionBar?.hide()
        if (intent.hasExtra("PIN_CODE")) {
            et_user_pincode.setText(intent.getStringExtra("PIN_CODE") ?: "")

            if (et_user_pincode.text.length == 6) {
                hideKeyboard()
                et_user_pincode.isCursorVisible = false
                getPincodeAddress()
            }
        }

        apiServicesPlatform.getUserDocumentDetails().execute(this, true) {
            if (it.success) {

                tv_document_type.text = it.info.fileType
                et_user_pincode.setText(it.info.pinCode)
                getPincodeAddress()

                when (it.info.status) {
                    Constants.DocumentErrorCodes.USER_DETAILS_PENDING.code -> {
                        ll_address.alpha = 0.5F
                        btn_submit_address.setBackgroundColor(Color.parseColor("#656565"))
                        btn_submit_address.text = "Submitted"
                        tv_address_verification_status.text = it.info.comments
                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_REJECTED.code -> {
                        tv_address_verification_status.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_rejected_20dp,
                            0,
                            0,
                            0
                        )
                        tv_address_verification_status.text = it.info.comments
                        btn_submit_address.text = "Re-Submit"

                        btn_submit_address.onOneClick {
                            launchActivity<VerifyAddressActivity> {
                                putExtra("PIN_CODE", et_user_pincode.text.toString())
                                putExtra("docId", it.info.document_id)
                            }
                            finish()
                        }
                    }
                    else -> {
                        ll_address.alpha = 0.5F
                        btn_submit_address.visibility = View.INVISIBLE
                        tv_address_verification_status.text = it.info.comments
                        tv_address_verification_status.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_approved_20dp,
                            0,
                            0,
                            0
                        )
                    }
                }

                if (it.info.fileType == "AADHARCARD" || it.info.fileType == "DRIVINGLICENSE") {

                    ll_single_document.visibility = View.GONE
                    iv_document_front.loadURL(it.info.file1)
                    iv_document_back.loadURL((it.info.file2))

                } else if (it.info.fileType == "BANK" || it.info.fileType == "PASSPORT") {
                    ll_dual_document.visibility = View.GONE
                    iv_upload_bank_statement.loadURL(it.info.file1)
                } else {
                    showToast("Sorry No record.")
                }
            }
        }

    }

}