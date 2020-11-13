package com.gtgt.pokerjacks.ui.profile.manage_account

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.profile.manage_account.AddAccountActivity
import com.gtgt.pokerjacks.ui.profile.manage_account.adapter.ExistingBankAccountsAdapter
import com.gtgt.pokerjacks.ui.profile.manage_account.model.GetBankDetailsInfo
import com.gtgt.pokerjacks.ui.profile.manage_account.viewModel.ManageAccountViewModel
import kotlinx.android.synthetic.main.activity_manage_bank_account.*
import kotlinx.android.synthetic.main.add_bank_acc_popup.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.io.Serializable

class ManageBankAccountActivity : BaseActivity() {

    private val viewModel: ManageAccountViewModel by viewModel()
    private val ADD_ACC_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_bank_account)

        iv_back.onOneClick {
            onBackPressed()
        }
        tv_commonTitle.text = "Manage Bank Accounts"

        viewModel.getBankDetails()
        viewModel.getBankDetails.observe(this, Observer {
            if (it.success) {
                ll_BankAccounts.visibility = View.VISIBLE
                tv_AccountsNotFound.visibility = View.GONE
                it.info?.let { it1 -> initAccountAdapter(it1) }
            } else {
                ll_BankAccounts.visibility = View.GONE
                tv_AccountsNotFound.visibility = View.VISIBLE
            }
        })

        viewModel.deleteBankAccount.observe(this, Observer {
            showToast(it.description)
            if (it.success) {
                viewModel.getBankDetails()
            }
        })

        viewModel.getVerification.observe(this, Observer {
            showSnack(it.description)
            if (it.success) {
                viewModel.getBankDetails()
            }
        })

        btn_AddBankAcc.onOneClick {
            showAccTypeDialog(
                onAccTypeSelected = {
                    when (it) {
                        0 -> launchActivity<AddAccountActivity>(ADD_ACC_REQUEST_CODE) {
                            putExtra("ACCOUNT_TYPE", 0)
                        }
                        1 -> launchActivity<AddAccountActivity>(ADD_ACC_REQUEST_CODE) {
                            putExtra("ACCOUNT_TYPE", 1)
                        }
                        2 -> launchActivity<AddAccountActivity>(ADD_ACC_REQUEST_CODE) {
                            putExtra("ACCOUNT_TYPE", 2)
                        }
                    }
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == ADD_ACC_REQUEST_CODE) {
            viewModel.getBankDetails()
        }
    }

    private fun onDidNotReceiveClciked(getBankDetailsInfo: GetBankDetailsInfo) {
        launchActivity<AccConfirmationActivity>(ADD_ACC_REQUEST_CODE) {
            putExtra("GET_BANK_DATA", getBankDetailsInfo as Serializable)
        }
    }

    private fun onConfirmClicked(getBankDetailsInfo: GetBankDetailsInfo) {
        viewModel.getVerification(getBankDetailsInfo.detailId)
    }

    private fun onDeleteClicked(getBankDetailsInfo: GetBankDetailsInfo) {
        viewModel.deleteBankDetails(getBankDetailsInfo.detailId)
    }

    private fun initAccountAdapter(info: List<GetBankDetailsInfo>) {
        val existingAccountsAdapter = ExistingBankAccountsAdapter(
            this,
            ::onDidNotReceiveClciked,
            ::onConfirmClicked,
            ::onDeleteClicked
        )
        rv_existingBankAccounts.adapter = existingAccountsAdapter
        existingAccountsAdapter.addList(info)
    }

    private fun showAccTypeDialog(onAccTypeSelected: (Int) -> Unit) {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_bank_acc_popup, null)
        builder.setView(dialogView)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.rb_addBankAcc.isChecked = true
        var accType = 0

        dialogView.rg_accType.setOnCheckedChangeListener { group, checkedId ->
            if (dialogView.rb_addBankAcc.isChecked) accType = 0
            if (dialogView.rb_addUPI.isChecked) accType = 1
            if (dialogView.rb_add_PaytmAcc.isChecked) accType = 2
        }

        dialogView.btn_addAccType.onOneClick {
            onAccTypeSelected(accType)
            dialog.dismiss()
        }
        dialog.show()
    }
}