package com.gtgt.pokerjacks.ui.profile.manage_account

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.extensions.retrieveString
import kotlinx.android.synthetic.main.activity_add_account.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class AddAccountActivity : AppCompatActivity() {
    private val ACCOUNT_TYPE by lazy { intent.getIntExtra("ACCOUNT_TYPE", 0) }
//    private val viewModel: ManageAccountViewModel by viewModel()
//    private val profileViewModel: ProfileViewModel by viewModel()
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
    }
}