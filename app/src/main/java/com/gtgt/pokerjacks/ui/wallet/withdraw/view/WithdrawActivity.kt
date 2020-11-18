package com.gtgt.pokerjacks.ui.wallet.withdraw.view

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.profile.manage_account.ManageBankAccountActivity
import com.gtgt.pokerjacks.ui.profile.manage_account.model.GetBankDetailsInfo
import com.gtgt.pokerjacks.ui.profile.manage_account.viewModel.ManageAccountViewModel
import com.gtgt.pokerjacks.ui.profile.profile.viewModel.ProfileViewModel
import com.gtgt.pokerjacks.ui.profile.verify_address.AddressVerificationStatusActivity
import com.gtgt.pokerjacks.ui.profile.verify_address.VerifyAddressActivity
import com.gtgt.pokerjacks.ui.profile.verify_pan.PanVerificationStatusActivity
import com.gtgt.pokerjacks.ui.profile.verify_pan.VerifyPanActivity
import com.gtgt.pokerjacks.ui.profile.vrify_email.VerifyEmailActivity
import com.gtgt.pokerjacks.ui.wallet.withdraw.adapter.BankAccountsAdapter
import com.gtgt.pokerjacks.ui.wallet.withdraw.viewModel.WithdrawViewModel
import com.gtgt.pokerjacks.utils.Constants
import kotlinx.android.synthetic.main.activity_withdraw.*
import kotlinx.android.synthetic.main.existing_acc_popup.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class WithdrawActivity : BaseActivity() {
    private val viewModel: ManageAccountViewModel by viewModel()
    private val withdrawViewModel: WithdrawViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by store()
    private val ADD_ACC_REQUEST_CODE = 1
    private var detailId = ""
    private var withdrawableAmt = 0.0
    private var isAccountAdded = false
    private var isEmailVerified = false
    private var isPanVerified = false
    private var isAddressVerified = false
    private var panVerifiedStatus = ""
    private var addressVerifiedStatus = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text="Withdraw"

        if (intent != null) {
            withdrawableAmt = intent.getDoubleExtra("WinningsAmt", 0.0)
            tv_withdrawable_money.text =
                "${resources.getString(R.string.ruppe)}${withdrawableAmt.toDecimalFormat()}"
        }

        tv_manageAccount.onOneClick {
            launchActivity<ManageBankAccountActivity> { }
        }

        profileViewModel.getUserProfileDetailsInfo()
        profileViewModel.userProfileInfo.observe(this, Observer {
            isEmailVerified = it.isEmailVerified
            isPanVerified =
                it.isPanVerified == Constants.DocumentErrorCodes.USER_DETAILS_APPROVED.code
            isAddressVerified =
                it.isAddressVerified == Constants.DocumentErrorCodes.USER_DETAILS_APPROVED.code
            panVerifiedStatus=it.isPanVerified
            addressVerifiedStatus=it.isAddressVerified
            viewModel.getBankDetails()
        })

        viewModel.getBankDetails.observe(this, Observer {
            if (it.success) {
                if (it.info != null) {
                    val accountList = it.info.filter { it.status == "VERIFIED" }
                    if (accountList.isNotEmpty()) {
                        isAccountAdded = true
                        rl_AccountInfo.visibility = View.VISIBLE
                        if (retrieveString("bank_detailId").isEmpty()) {
                            putString("bank_detailId", accountList[0].detailId)
                            setSelectedAccount(accountList[0])
                        } else {
                            for (i in accountList.indices) {
                                if (retrieveString("bank_detailId") == accountList[i].detailId) {
                                    setSelectedAccount(accountList[i])
                                    break
                                }
                            }
                        }
                        tv_changeBankAcc.onOneClick {
                            showAvailableAccounts(accountList)
                        }
                    }
                } else {
                    rl_AccountInfo.visibility = View.GONE
                }
            } else {
                rl_AccountInfo.visibility = View.GONE
            }
            showWithdrawable()
        })

        btn_withdraw.onOneClick {
            if (!isEmailVerified) {
                launchActivity<VerifyEmailActivity> { }
            } else if (!isPanVerified) {
                when (panVerifiedStatus) {
                    Constants.DocumentErrorCodes.USER_DETAILS_NORECORD.code -> {
                        launchActivity<VerifyPanActivity>()
                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_PENDING.code -> {
                        launchActivity<PanVerificationStatusActivity>()
                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_REJECTED.code -> {
                        launchActivity<PanVerificationStatusActivity>()
                    }
                    else -> {
                        launchActivity<VerifyPanActivity> { }
                    }
                }
            } else if (!isAddressVerified) {
                when (addressVerifiedStatus) {
                    Constants.DocumentErrorCodes.USER_DETAILS_NORECORD.code -> {
                        launchActivity<VerifyAddressActivity>()
                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_PENDING.code -> {
                        launchActivity<AddressVerificationStatusActivity>()
                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_REJECTED.code -> {
                        launchActivity<AddressVerificationStatusActivity>()
                    }
                    else -> {
                        launchActivity<VerifyAddressActivity> { }
                    }
                }
            } else if (!isAccountAdded) {
                launchActivity<ManageBankAccountActivity> { }
            } else {
                val withdrawAmount = et_withdrawAmount.text.toString()
                if (withdrawAmount.isNotEmpty()) {
                    if (detailId != "") {
                        if (withdrawAmount.toInt() in 200..withdrawableAmt.toInt()) {
                            withdrawViewModel.withdrawalmoney(
                                detailId = detailId,
                                withdrawAmount = withdrawAmount.toInt()
                            )
                        } else {
                            showSnack("check your Minimum and Maximum amount")
                        }
                    } else {
                        showSnack("add bank account")
                    }
                }
            }
        }

        withdrawViewModel.withdrawMoney.observe(this, Observer {
            if (it.success) {
                showToast("Your withdrawal request has been placed successfully")
                this.finish()
            } else {
                showSnack(it.description)
            }
        })
    }

    private fun showWithdrawable() {
        if (isEmailVerified && isPanVerified && isAddressVerified && isAccountAdded) {
            ll_validForWithdraw.visibility = View.VISIBLE
            ll_invalidForWithdraw.visibility = View.GONE

            btn_withdraw.text = "Withdraw"
        } else {
            ll_validForWithdraw.visibility = View.GONE
            ll_invalidForWithdraw.visibility = View.VISIBLE

            if (!isEmailVerified) {
                btn_withdraw.text = "Verify Email"
            } else if (!isPanVerified) {
                btn_withdraw.text = "Verify PAN"
            } else if (!isAddressVerified) {
                btn_withdraw.text = "Verify Address"
            } else if (!isAccountAdded) {
                btn_withdraw.text = "Add Account"
            } else {
                btn_withdraw.text = "Withdraw"
            }

            if (!isEmailVerified) {
                tv_emailVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            if (!isPanVerified) {
                tv_panVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            if (!isAddressVerified) {
                tv_addressVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            if (!isAccountAdded) {
                tv_accountPresent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    private fun showAvailableAccounts(info: List<GetBankDetailsInfo>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.existing_acc_popup, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        dialog.setCancelable(true)
        (dialogView.parent as View).setBackgroundColor(Color.TRANSPARENT)
        var lastCheckedPos = 0
        var defaultCheckedPos = 0

        if (retrieveString("bank_detailId").isNotEmpty()) {
            for (i in info.indices) {
                if (retrieveString("bank_detailId") == info[i].detailId) {
                    defaultCheckedPos = i
                    break
                }
            }
        }

        val bankAccountsAdapter =
            BankAccountsAdapter(
                this,
                defaultPos = defaultCheckedPos,
                onChangeSelection = { selectedPos ->
                    defaultCheckedPos = selectedPos
                })
        dialogView.btn_changeAcc.onOneClick {
            lastCheckedPos = defaultCheckedPos
            putString("bank_detailId", info[lastCheckedPos].detailId)
            setSelectedAccount(info[lastCheckedPos])
            dialog.dismiss()
        }
        dialogView.rv_existingAccounts.adapter = bankAccountsAdapter
        bankAccountsAdapter.addList(info)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setSelectedAccount(getBankDetailsInfo: GetBankDetailsInfo) {
        detailId = getBankDetailsInfo.detailId
        var desc = ""
        desc = when (getBankDetailsInfo.AccountNameByType) {
            "UPI" -> "UPI Id:"
            "PAYTM" -> "PAYTM Id:"
            else -> getBankDetailsInfo.bank_name.toString()
        }
        rb_accountType.text = getBankDetailsInfo.AccountNameByType
        tv_BankAccount.text =
            "$desc - ${getBankDetailsInfo.formattedAccNumber}"
    }

    override fun onResume() {
        super.onResume()
        if (ll_invalidForWithdraw.isVisible) {
            profileViewModel.getUserProfileDetailsInfo()
        }
    }
}