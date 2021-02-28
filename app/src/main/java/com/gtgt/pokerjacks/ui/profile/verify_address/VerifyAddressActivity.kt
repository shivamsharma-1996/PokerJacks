package com.gtgt.pokerjacks.ui.profile.verify_address

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.string
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlatform
import kotlinx.android.synthetic.main.activity_verify_address.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import okhttp3.MultipartBody

class VerifyAddressActivity : BaseActivity(), SendFragmentDataToActivity {

    var frontDocument: Bitmap? = null
    var backDocument: Bitmap? = null
    var selectedDocType = ""
    var isValidPinCode = false

    private val bankStatementUploader by lazy { BankStatementUploaderFragment() }
    private val drivingLicenseUploader by lazy { DrivingLicenseUploaderFragment() }
    private val aadharCardUploader by lazy { AadharCardUploaderFragment() }
    private val passPortUploader by lazy { PassPortUploaderFragment() }
    private val docId by lazy { intent.getStringExtra("docId") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_address)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_toolbar_title.text = "Address Verification"

        et_user_pincode.onOneClick {
            et_user_pincode.isCursorVisible = true
        }

        if (intent.hasExtra("PIN_CODE")) {
            et_user_pincode.setText(intent.getStringExtra("PIN_CODE") ?: "")
        }

        et_user_pincode.afterTextChanged { pincode ->
            if (pincode.length == 6) {
                hideKeyboard()
                et_user_pincode.isCursorVisible = false
                apiServicesPlatform.updateLocation(jsonObject("pincode" to pincode))
                    .execute(this, true) {
                        isValidPinCode = it.success

                        if (it.success) {
                            tv_place_name.visibility = View.VISIBLE
                            tv_place_name.text = it.info["address"].string
                        } else {
                            tv_place_name.visibility = View.VISIBLE
                            tv_place_name.text =
                                "Oops! PIN not recognized. If your PIN code is not listed please enter 000000 and proceed"
                            tv_place_name.setTextColor(Color.RED)
                        }
                    }
            }
        }

        btn_select_aadhar_card.onOneClick {
            btn_select_bank_statement.isChecked = false
            btn_select_driver_license.isChecked = false
            btn_select_passport.isChecked = false
            selectedDocType = "AADHARCARD"
            replaceFragment(aadharCardUploader, R.id.document_placeholder)
        }

        btn_select_driver_license.onOneClick {
            btn_select_bank_statement.isChecked = false
            btn_select_aadhar_card.isChecked = false
            btn_select_passport.isChecked = false
            selectedDocType = "DRIVINGLICENSE"
            replaceFragment(drivingLicenseUploader, R.id.document_placeholder)
        }

        btn_select_bank_statement.onOneClick {
            btn_select_aadhar_card.isChecked = false
            btn_select_driver_license.isChecked = false
            btn_select_passport.isChecked = false
            selectedDocType = "BANK"
            replaceFragment(bankStatementUploader, R.id.document_placeholder)
        }

        btn_select_passport.onOneClick {
            btn_select_aadhar_card.isChecked = false
            btn_select_driver_license.isChecked = false
            btn_select_bank_statement.isChecked = false
            selectedDocType = "PASSPORT"
            replaceFragment(passPortUploader, R.id.document_placeholder)
        }

        btn_submit_address.onOneClick {
            if (!isValidPinCode) {
                showSnack("Please provide a valid pincode")
                return@onOneClick
            }

            if (selectedDocType == "AADHARCARD" || selectedDocType == "DRIVINGLICENSE") {
                if (!(et_user_pincode.text.isNullOrEmpty() || frontDocument == null || backDocument == null)) {
                    if (docId == null) {
                        apiServicesPlatform.uploadMultipleDocuments(
                            pinCode = MultipartBody.Part.createFormData(
                                "pinCode",
                                et_user_pincode.text.toString()
                            ),
                            userId = MultipartBody.Part.createFormData(
                                "userId",
                                retrieveString("USER_ID")
                            ),
                            file1 = ApiInterfacePlatform.createRequestBody(
                                frontDocument!!,
                                "file1"
                            ),
                            file2 = ApiInterfacePlatform.createRequestBody(backDocument!!, "file2"),
                            doctype = MultipartBody.Part.createFormData("doctype", selectedDocType)
                        )
                    } else {
                        apiServicesPlatform.uploadMultipleDocumentsWithDocId(
                            doc_id = MultipartBody.Part.createFormData(
                                "doc_id",
                                docId
                            ),
                            pinCode = MultipartBody.Part.createFormData(
                                "pinCode",
                                et_user_pincode.text.toString()
                            ),
                            userId = MultipartBody.Part.createFormData(
                                "userId",
                                retrieveString("USER_ID")
                            ),
                            file1 = ApiInterfacePlatform.createRequestBody(
                                frontDocument!!,
                                "file1"
                            ),
                            file2 = ApiInterfacePlatform.createRequestBody(backDocument!!, "file2"),
                            doctype = MultipartBody.Part.createFormData("doctype", selectedDocType)
                        )
                    }.execute(this, true) {
                        if (it.success) {
                            finish()
                            launchActivity<AddressVerificationStatusActivity> {
                                putExtra("FROM_VERIFICATION_ACTIVITY", true)
                                putExtra("USER_PINCODE", et_user_pincode.text)
                                putExtra("VERIFICATION_TYPE", it.info.status)
                                putExtra("DOCUMENT_TYPE", it.info.fileType)
                                putExtra("PIN_CODE", et_user_pincode.text.toString())
                            }

                        } else {
                            showSnack(it.description)
                        }
                    }

                } else {
                    showSnack("Please Fill All Details To Submit")
                }
            } else {
                if (et_user_pincode.text.isNullOrEmpty() || frontDocument == null) {
                    showSnack("Please Fill all details to upload")
                } else {
                    if (docId == null) {
                        apiServicesPlatform.uploadSingleDocument(
                            pinCode = MultipartBody.Part.createFormData(
                                "pinCode",
                                et_user_pincode.text.toString()
                            ),
                            userId = MultipartBody.Part.createFormData(
                                "userId",
                                retrieveString("USER_ID")
                            ),
                            file1 = ApiInterfacePlatform.createRequestBody(
                                frontDocument!!,
                                "file1"
                            ),
                            doctype = MultipartBody.Part.createFormData("doctype", selectedDocType)
                        )
                    } else {
                        apiServicesPlatform.uploadSingleDocumentWithDocId(
                            doc_id = MultipartBody.Part.createFormData(
                                "doc_id",
                                docId
                            ),
                            pinCode = MultipartBody.Part.createFormData(
                                "pinCode",
                                et_user_pincode.text.toString()
                            ),
                            userId = MultipartBody.Part.createFormData(
                                "userId",
                                retrieveString("USER_ID")
                            ),
                            file1 = ApiInterfacePlatform.createRequestBody(
                                frontDocument!!,
                                "file1"
                            ),
                            doctype = MultipartBody.Part.createFormData("doctype", selectedDocType)
                        )
                    }.execute(this, true) {
                        if (it.success) {
                            finish()
                            launchActivity<AddressVerificationStatusActivity> {
                                putExtra("FROM_VERIFICATION_ACTIVITY", true)
                                putExtra("USER_PINCODE", et_user_pincode.text)
                                putExtra("VERIFICATION_TYPE", it.info.status)
                                putExtra("DOCUMENT_TYPE", it.info.fileType)
                                putExtra("PIN_CODE", et_user_pincode.text.toString())
                            }
                        } else {
                            showSnack(it.description)
                        }
                    }
                }

            }


        }


    }

    override fun exchangeData(type: String, documentPath: Bitmap) {
        if (type == "front_doc") {
            frontDocument = documentPath
        } else {
            backDocument = documentPath
        }
    }
}