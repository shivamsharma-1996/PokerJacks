package com.gtgt.pokerjacks.ui.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.activity_manage_bank_account.*
import kotlinx.android.synthetic.main.add_bank_acc_popup.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ManageBankAccountActivity : AppCompatActivity() {

    private val ADD_ACC_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_bank_account)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Manage Bank Accounts"

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

        dialogView.btn_closeD.onOneClick {
            dialog.dismiss()
        }
        dialog.show()
    }
}