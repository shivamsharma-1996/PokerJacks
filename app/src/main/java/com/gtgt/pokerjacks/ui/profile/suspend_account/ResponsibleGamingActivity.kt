package com.gtgt.pokerjacks.ui.profile.suspend_account

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import kotlinx.android.synthetic.main.activity_responsible_gaming.*
import kotlinx.android.synthetic.main.suspend_dialog.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ResponsibleGamingActivity : BaseActivity() {
    var blockType="DAY"
//    private val profileViewModel: ProfileViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responsible_gaming)

        iv_back.onOneClick {
            onBackPressed()
        }

        tv_commonTitle.text = "Responsible Gaming"

        btn_suspendAccount.onOneClick {
            showSuspendDialog()
        }
    }

    private fun showSuspendDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.suspend_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        dialogView.btn_closeDialog.onOneClick {
            alertDialog.dismiss()
        }

        dialogView.rg_suspend.setOnCheckedChangeListener { group, checkedId ->
            // checkedId is the RadioButton selected
            if(dialogView.rb_24hr.isChecked){
                blockType="DAY"
            }
            if(dialogView.rb_1week.isChecked){
                blockType="WEEK"
            }
        }

        btn_suspendAccount.onOneClick {
            if(dialogView.cb_condition.isChecked){
//                profileViewModel.blockMe("Temporary", blockType)
            }
        }
    }
}