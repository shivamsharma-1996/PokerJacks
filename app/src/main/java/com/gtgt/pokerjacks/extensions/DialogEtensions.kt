package com.gtgt.pokerjacks.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.R
import kotlinx.android.synthetic.main.banned_state_bottom_sheet.view.*
import kotlinx.android.synthetic.main.banned_state_dialog.view.*
import kotlinx.android.synthetic.main.banned_state_dialog.view.tv_cash
import kotlinx.android.synthetic.main.coming_soon_dialog.view.*
import kotlinx.android.synthetic.main.network_error_dialog.view.*
import kotlinx.android.synthetic.main.server_error_dialog.view.*
import okhttp3.Request
import okio.Buffer

fun Activity.showServerErrorDialog(requestInfo: Request, message: String? = null) {
    try {
        if (!isRunning())
            return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.server_error_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (!isRunning())
            return
        alertDialog.show()

        if (BuildConfig.DEBUG) {
            dialogView.btn_ok.onOneClick {
                alertDialog.dismiss()
            }
        } else {
            dialogView.btn_ok.text = "Share Error"
            val buffer = Buffer()
            requestInfo.body?.writeTo(buffer)
            val payload = buffer.readUtf8()

            val url = requestInfo.url.toString()

            message?.let { dialogView.messageError.text = "$url\n$it" }

            dialogView.btn_ok.onOneClick {
                /*val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        """
                        Url: $url,
                        
                        Method: ${requestInfo.method},
                         
                        Authorization: ${getModel<JsonElement>("loginInfo")?.let {
                            if (BuildConfig.DEBUG) {
                                log("Authorization", "Bearer " + it["token"].string)
                            }
                            it["token"].string
                        }
                            ?: ""}
                        
                        DeviceId: ${retrieveString("UNIQUE_ID")}

                        ${if (payload.isEmpty()) "" else "Payload: $payload"}

                        Response: $message
                    """.trimIndent()
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)*/

                alertDialog.dismiss()
//            finish()
            }
        }

        dialogView.btn_closeSD.onOneClick {
            alertDialog.dismiss()
//            finish()
        }
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
    }
}

fun Activity.networkErrorDialog(closeActivity: Boolean = true, refresh: () -> Unit) {
    runOnMain {
        try {
            if (!isRunning())
                return@runOnMain
            val dialogView = LayoutInflater.from(this).inflate(R.layout.network_error_dialog, null)
            //Now we need an AlertDialog.Builder object
            val builder = AlertDialog.Builder(this)
            //setting the view of the builder to our custom view that we already inflated
            builder.setView(dialogView)

            //finally creating the alert dialog and displaying it
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (!isRunning())
                return@runOnMain
            alertDialog.show()

            dialogView.btn_close.onOneClick {
                alertDialog.dismiss()
                if (closeActivity) {
                    finish()
                }
            }
            dialogView.btn_retry.onOneClick {
                refresh()
                alertDialog.dismiss()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

fun Context.showComingSoonDialog(
    onPopupClosed: () -> Unit
) {
    if (BuildConfig.FLAVOR == "production") {
        //Creation of Logout confirmation dialog
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.coming_soon_dialog, null)
        //Now we need an AlertDialog.Builder object
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        dialogView.iv_closeComingSoon.onOneClick {
            onPopupClosed()
            alertDialog.dismiss()
        }
    }
}

fun Context.showBannedStatesDialog() {
    //Creation of Logout confirmation dialog
    val dialogView =
        LayoutInflater.from(this).inflate(R.layout.banned_state_dialog, null)
    //Now we need an AlertDialog.Builder object
    val builder = androidx.appcompat.app.AlertDialog.Builder(this)

    //setting the view of the builder to our custom view that we already inflated
    builder.setView(dialogView)

    //finally creating the alert dialog and displaying it
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()

    dialogView.tv_cash.paintFlags = dialogView.tv_cash.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

    dialogView.iv_closeDialog.onOneClick {
        alertDialog.dismiss()
    }

    runOnMain {
        if (alertDialog.isShowing) {
            Handler().postDelayed({
                alertDialog.dismiss()
            }, 3000)
        }
    }
}

fun Context.showBannedStatesBottomSheet() {
    val dialogView =
        LayoutInflater.from(this).inflate(R.layout.banned_state_bottom_sheet, null)
    val dialog = BottomSheetDialog(this)
    dialog.setContentView(dialogView)
    dialog.setCancelable(true)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    dialogView.tv_cash.paintFlags = dialogView.tv_cash.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

    dialogView.btn_locationOk.onOneClick {
        dialog.dismiss()
    }
}