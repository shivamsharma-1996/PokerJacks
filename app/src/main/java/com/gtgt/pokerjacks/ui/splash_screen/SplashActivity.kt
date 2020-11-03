package com.gtgt.pokerjacks.ui.splash_screen

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.isRunning
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.retrieveBoolean
import com.gtgt.pokerjacks.ui.MainActivity
import com.gtgt.pokerjacks.ui.login.view.EnterMpinOrTouchIdActivity
import com.gtgt.pokerjacks.ui.login.view.RegistrationActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

//    val viewModel: SplashViewModel by viewModel()
//    val PERMISSION_REQUEST_CODE = 0
    var dialogView: View? = null
    var alertDialog: AlertDialog? = null
//    var downloadUrl = ""
//    var fl_per_width = 0
//    var params: ViewGroup.MarginLayoutParams? = null
//    var downloadFileWithProgress: DownloadFileWithProgress? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

    if(BuildConfig.FLAVOR == "production"){
        iv_dev_img.visibility = View.GONE
    }


    Handler().postDelayed({
            navigateToNextScreen()
        }, 1000)
    }

    fun navigateToNextScreen() {
        Handler().postDelayed({
            if (retrieveBoolean("IS_USER_LOGIN")) {
                launchActivity<EnterMpinOrTouchIdActivity> { }
//                launchActivity<HomeActivity> { }
            } else {
                launchActivity<RegistrationActivity> { }
            }
            this.finish()
        }, 1000)
    }

    private fun showUpdatePopup(info: String) {
        dialogView = LayoutInflater.from(this).inflate(R.layout.update_popup, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        alertDialog = builder.create()
        alertDialog?.setCancelable(false)
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        /*if (dialogView != null) {
            if (info.forceUpdate) {
                dialogView!!.btn_updateLater.visibility = View.GONE
            } else if (info.recommendedUpdate) {
                dialogView!!.btn_updateLater.visibility = View.VISIBLE
            }

            dialogView!!.tv_updateMsg.text = info.features ?: ""

            dialogView!!.btn_updateLater.onOneClick {
                navigateToNextScreen()
            }

            dialogView!!.btn_update.onOneClick {
                val cellWidth = dialogView?.width
//            val fl_per_width = pxToDp(cellWidth) - dialogView.dip(40)
                fl_per_width = pxToDp(cellWidth!!) - 40

                params =
                    dialogView?.tv_updatePercentage?.layoutParams as ViewGroup.MarginLayoutParams
//            params?.leftMargin= dpToPx(fl_per_width-30)
                checkStoragePermissions()
            }
        }*/

        if (!isRunning())
            return
        alertDialog?.show()
    }
}