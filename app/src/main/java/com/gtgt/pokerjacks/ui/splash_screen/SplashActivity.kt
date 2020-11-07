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
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.MainActivity
import com.gtgt.pokerjacks.ui.login.view.EnterMpinOrTouchIdActivity
import com.gtgt.pokerjacks.ui.login.view.RegistrationActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    val viewModel: SplashViewModel by viewModel()
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

    viewModel.getWebPageUrls().observe(this, Observer {
        if (it.success) {
            MyApplication.sharedPreferencesDontClear.edit().apply {
                putString("game_guide", it.info.how_to_play_url).apply()
                putString("points_system", it.info.points_system_url).apply()
                putString("terms_conditions", it.info.terms_and_conditions_url).apply()
                putString("refer_terms_conditions", it.info.refer_and_earn_url).apply()
                putString("privacy_policy", it.info.privacy_policy_url).apply()
                putString("faq", it.info.FAQs_url).apply()
            }
        }
    })

//    viewModel.checkUpdate().observe(this, Observer {
//        if (it.success) {
//            downloadUrl = it.info.productUrl.toString()
//            putBoolean("forceUpdate", it.info.forceUpdate)
//            putBoolean("recommendedUpdate", it.info.recommendedUpdate)
//            if (!it.info.forceUpdate && !it.info.recommendedUpdate) {
//                navigateToNextScreen()
//            } else {
//                showUpdatePopup(it.info)
//            }
//        } else {
//            putBoolean("forceUpdate", false)
//            putBoolean("recommendedUpdate", false)
//            navigateToNextScreen()
//        }
//    })

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