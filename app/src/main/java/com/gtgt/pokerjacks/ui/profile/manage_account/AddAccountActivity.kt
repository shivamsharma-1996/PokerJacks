package com.gtgt.pokerjacks.ui.profile.manage_account

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.profile.manage_account.viewModel.ManageAccountViewModel
import com.gtgt.pokerjacks.ui.profile.profile.viewModel.ProfileViewModel
import com.gtgt.pokerjacks.utils.Constants
import kotlinx.android.synthetic.main.activity_add_account.*
import kotlinx.android.synthetic.main.activity_coupons_activty.*
import kotlinx.android.synthetic.main.add_account_verification_dialog.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

class AddAccountActivity : BaseActivity() {
    private val ACCOUNT_TYPE by lazy { intent.getIntExtra("ACCOUNT_TYPE", 0) }
    private val viewModel: ManageAccountViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private var alertDialog: AlertDialog? = null
    private var ifscCheck = false
    private var bankName: String? = null
    private var branchName: String? = null
    private var isAccountAdded = false
    private var isEmailVerified = false
    private var isPanVerified = false
    private var isAddressVerified = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)

        iv_back.onOneClick {
            onBackPressed()
        }
        when (ACCOUNT_TYPE) {
            0 -> {
                tv_commonTitle.text = "Bank Account"
                sv_addBankAccount.visibility = View.VISIBLE
            }
            1 -> {
                tv_commonTitle.text = "UPI"
                ll_addUPI.visibility = View.VISIBLE
            }
            2 -> {
                tv_commonTitle.text = "Paytm"
                et_paytmNumber.setText(retrieveString("MOBILE"))
                ll_addPaytmAccount.visibility = View.VISIBLE
            }
            else -> {
                tv_commonTitle.text = "Bank Account"
                sv_addBankAccount.visibility = View.VISIBLE
            }
        }

        et_ifsc.afterTextChanged {
            var s: String = it
            if (s != s.toUpperCase(Locale.getDefault())) {
                s = s.toUpperCase(Locale.getDefault())
                et_promoCode.setText(s)
                et_promoCode.setSelection(s.length)
            }
            if (it.length == 11) {
                hideKeyboard()
                if (it.isValidIFSCCode()) {
                    viewModel.getBankDetailsFromIFSC(it)
                } else {
                    showToast("Invalid IFSC Code")
                }
            } else {
                clearIFSC()
            }
        }

        viewModel.bankFromIFSC.observe(this, androidx.lifecycle.Observer {
            tv_ifscValidation.visibility = View.VISIBLE
            if (it.success) {
                tv_ifscValidation.setTextColor(resources.getColor(R.color.text_blue))
                tv_ifscValidation.text = "${it.info.BankName}, ${it.info.City}, ${it.info.State}"
                ll_ifscInValid.visibility = View.GONE
                ifscCheck = true
                bankName = it.info.BankName
                branchName = it.info.City
            } else {
                tv_ifscValidation.setTextColor(resources.getColor(R.color.red))
                tv_ifscValidation.text = it.description
                ll_ifscInValid.visibility = View.VISIBLE
                ifscCheck = false
            }
        })

        profileViewModel.getUserProfileDetailsInfo()
        profileViewModel.userProfileInfo.observe(this, androidx.lifecycle.Observer {
            isEmailVerified = it.isEmailVerified
            isPanVerified =
                it.isPanVerified == Constants.DocumentErrorCodes.USER_DETAILS_APPROVED.code
            isAddressVerified =
                it.isAddressVerified == Constants.DocumentErrorCodes.USER_DETAILS_APPROVED.code
            viewModel.getBankDetails()
        })

        viewModel.getBankDetails.observe(this, androidx.lifecycle.Observer {
            if (it.success) {
                if (it.info != null) {
                    isAccountAdded = true
                }
            }
        })

        btn_addBank.onOneClick {
            if (!isEmailVerified) {
                showSnack("Please verify your e-mail on settings page to proceed for adding of Bank Account")
            } else if (!isPanVerified) {
                showSnack("Please submit your PAN on settings page to proceed for adding of Bank Account")
            } else if (!isAddressVerified) {
                showSnack("Please submit your Address proof on settings page to proceed for adding of Bank Account")
            } else {
                if (cb_addBank.isChecked) {
                    when (ACCOUNT_TYPE) {
                        0 -> {
                            val userNameBank = et_userNameBank.text.toString()
                            val accNumber = et_accNumber.text.toString()
                            val ifsc = et_ifsc.text.toString()

                            if (userNameBank.isNotEmpty() && accNumber.isNotEmpty() && ifsc.isValidIFSCCode()) {
                                if (!ifscCheck) {
                                    bankName = et_bankName.text.toString()
                                    branchName = et_branchName.text.toString()
                                }
                                showAddAccVerificationDialog(
                                    userName = userNameBank,
                                    accNumber = accNumber,
                                    ifsc = ifsc,
                                    bankName = bankName,
                                    branchName = branchName
                                )
                            } else {
                                showToast("All fields are mandatory")
                            }
                        }
                        1 -> {
                            if (et_upi.text.toString().isValidUPI()) {
                                showAddAccVerificationDialog(upi = et_upi.text.toString())
                            }
                        }
                        2 -> {
                            if (et_paytmNumber.text.toString().isNotEmpty()) {
                                showAddAccVerificationDialog(paytmNumber = et_paytmNumber.text.toString())
                            }
                        }
                    }
                } else {
                    showToast("Please select T&C")
                }
            }
        }

        tv_changeNumber.onOneClick {
            et_paytmNumber.isEnabled = true
        }
    }

    private fun clearIFSC() {
        ifscCheck = false
        ll_ifscInValid.visibility = View.GONE
        tv_ifscValidation.visibility = View.GONE
        bankName = null
        branchName = null
    }

    private fun showAddAccVerificationDialog(
        userName: String? = null,
        accNumber: String? = null,
        ifsc: String? = null,
        bankName: String? = null,
        branchName: String? = null,
        upi: String? = null,
        paytmNumber: String? = null
    ) {

        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.add_account_verification_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        alertDialog = builder.create()
        with(alertDialog!!) {
            this.setCancelable(true)
            this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            this.show()
        }

        when (ACCOUNT_TYPE) {
            0 -> {
                viewModel.createBankDetails(
                    userName = userName,
                    accNumber = accNumber,
                    ifsc = ifsc,
                    bankName = bankName,
                    branchName = branchName
                )
            }
            1 -> {
                viewModel.createBankDetails(
                    upi = upi
                )
            }
            2 -> {
                viewModel.createBankDetails(
                    paytmNumber = paytmNumber
                )
            }
        }

        viewModel.createBankDetails.observe(this, androidx.lifecycle.Observer {
            if (it.success) {
                if (it.info != null) {
                    dialogView.tv_invalidBank.visibility = View.GONE
                    dialogView.tv_verifyingAcc.visibility = View.GONE
                    dialogView.tv_verification_under_progress.text = it.description
                    dialogView.tv_verified.visibility = View.VISIBLE
                    dialogView.tv_verified.playAnimation()
                    Handler().postDelayed({
                        alertDialog?.dismiss()
                        setResult(1)
                        this.finish()
                    }, 3000)
                }
            } else {
                dialogView.tv_invalidBank.text = it.description
                dialogView.ll_bankValid.visibility = View.GONE
                dialogView.ll_bankInvalid.visibility = View.VISIBLE

                dialogView.btn_closeInvalidAcc.onOneClick {
                    alertDialog?.dismiss()
                }
            }
        })

    }

    override fun onBackPressed() {
        setResult(0)
        this.finish()
    }

    override fun onStop() {
        super.onStop()
        alertDialog?.let {
            if (it.isShowing)
                it.dismiss()
        }
    }
}