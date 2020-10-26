package com.gtgt.pokerjacks.extensions

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.gtgt.pokerjacks.R
import kotlinx.android.synthetic.main.error_dialog.view.*

fun Activity.showErrorDialog(
    message: String = "server_error",
    shouldLogout: Boolean = false
) {
    try {
        if (!isRunning())
            return
        val errorDialogView = LayoutInflater.from(this).inflate(R.layout.error_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(errorDialogView)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (!isRunning())
            return
        alertDialog.show()

        errorDialogView.tv_msgError.text = message

        errorDialogView.btn_okErrorDialog.onOneClick {
            alertDialog.dismiss()

            /*if (shouldLogout) {
                val intent = Intent(
                    this,
                    RegistrationActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                clearUserSavedData()
                finishAffinity()
            } else {
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                clearUserSavedData()
                finishAffinity()
            }*/
        }

        errorDialogView.btn_closeED.onOneClick {
            /*if (shouldLogout) {
                val intent = Intent(
                    this,
                    RegistrationActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                clearUserSavedData()
                finishAffinity()
            } else {
                alertDialog.dismiss()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                clearUserSavedData()
                finishAffinity()
            }*/
        }
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
    }
}