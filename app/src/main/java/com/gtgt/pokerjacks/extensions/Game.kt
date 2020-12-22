package com.gtgt.pokerjacks.extensions

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.ui.game.viewModel.selectedTheme
import kotlinx.android.synthetic.main.game_popup.view.*

val popupTags = mutableSetOf<String>()

fun Activity.gameAlert(
    title: String,
    message: String,
    buttonText: String = "Yes",
    negativeText: String = "Cancel",
    autoClose: Long = -1,
    showCross: Boolean = false,
    tag: String? = null,
    dialog: ((AlertDialog) -> Unit)? = null,
    callback: (Boolean, AlertDialog) -> Unit
) {
    try {
        runOnMain {
            if (!isRunning())
                return@runOnMain

            if (tag != null && popupTags.contains(tag)) {
                return@runOnMain
            }

            val dialogView = LayoutInflater.from(this).inflate(R.layout.game_popup, null)
            //Now we need an AlertDialog.Builder object
            val builder = AlertDialog.Builder(this)
            //setting the view of the builder to our custom view that we already inflated
            builder.setView(dialogView)

            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            alertDialog.setOnDismissListener {
                if (tag != null && popupTags.contains(tag)) {
                    popupTags.remove(tag)
                }
            }
            alertDialog.setOnCancelListener {
                if (tag != null && popupTags.contains(tag)) {
                    popupTags.remove(tag)
                }
            }

            dialog?.invoke(alertDialog)

            selectedTheme?.let {
//                dialogView.content.background = it.popupDrawable
//                dialogView.nagitive_button.background = it.popupBtn
                dialogView.positive_button.background = it.btnDrawable
//                dialogView.cross.setImageResource(R.drawable.cross)
                dialogView.cross.background =
                    it.popupDrawable.apply { cornerRadius = dpToPxFloat(15) }
            }

            if (!isRunning())
                return@runOnMain
            try {
                alertDialog.show()
            } catch (ex: Exception) {

            }

            dialogView.title.text = title
            dialogView.message.text = message
            dialogView.positive_button.text = buttonText

            if (negativeText.isNotEmpty()) {
                dialogView.nagitive_button.visibility = View.VISIBLE
                dialogView.nagitive_button.text = negativeText
                dialogView.nagitive_button.onOneClick {
                    callback(false, alertDialog!!)
                    alertDialog.dismiss()
                }

            } else {
                dialogView.nagitive_button.visibility = View.GONE
            }

            dialogView.cross.visibility = if (showCross) VISIBLE else GONE

            dialogView.cross.onOneClick {
                callback(false, alertDialog!!)
                alertDialog.dismiss()
            }


            dialogView.positive_button.onOneClick {
                callback(true, alertDialog!!)
            }

            if (autoClose > -1) {
                Handler().postDelayed({
                    if (isRunning() && alertDialog.isShowing) {
                        alertDialog.dismiss()
                    }
                }, autoClose)
            }

        }
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
    }
}