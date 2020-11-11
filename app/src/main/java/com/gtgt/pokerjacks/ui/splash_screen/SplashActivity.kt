package com.gtgt.pokerjacks.ui.splash_screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.HomeActivity
import com.gtgt.pokerjacks.ui.login.view.EnterMpinOrTouchIdActivity
import com.gtgt.pokerjacks.ui.login.view.RegistrationActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.update_popup.view.*
import java.io.File
import java.lang.ref.WeakReference

class SplashActivity : BaseActivity() {

    val viewModel: SplashViewModel by viewModel()

        val PERMISSION_REQUEST_CODE = 0
    var dialogView: View? = null
    var alertDialog: AlertDialog? = null
    var downloadUrl = ""

        var fl_per_width = 0
    var params: ViewGroup.MarginLayoutParams? = null
    var downloadFileWithProgress: DownloadFileWithProgress? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (BuildConfig.FLAVOR == "production") {
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

        viewModel.checkUpdate().observe(this, Observer {
            if (it.success) {
                downloadUrl = it.info.productUrl.toString()
                putBoolean("forceUpdate", it.info.forceUpdate)
                putBoolean("recommendedUpdate", it.info.recommendedUpdate)
                if (!it.info.forceUpdate && !it.info.recommendedUpdate) {
                    navigateToNextScreen()
                } else {
                    showUpdatePopup(it.info)
                }
            } else {
                putBoolean("forceUpdate", false)
                putBoolean("recommendedUpdate", false)
                navigateToNextScreen()
            }
        })

        /*Handler().postDelayed({
            navigateToNextScreen()
        }, 1000)*/
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

    private fun showUpdatePopup(info: CheckUpdateInfo) {
        dialogView = LayoutInflater.from(this).inflate(R.layout.update_popup, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        alertDialog = builder.create()
        alertDialog?.setCancelable(false)
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (dialogView != null) {
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
        }

        if (!isRunning())
            return
        alertDialog?.show()
    }

    private fun checkStoragePermissions() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
//                        downloadFile()
                        /*CoroutineScope(Dispatchers.IO).launch {
                            downloadAPK()
                        }*/
//                        downloadUrl = "http://androhub.com/demo/demo.mp4"
                        dialogView?.ll_updateProgress?.visibility = View.VISIBLE
                        dialogView?.ll_updateButtons?.visibility = View.GONE
                        downloadFile()
                    } else {
                        // TODO - handle permission denied case
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun downloadFile() {
        if (downloadFileWithProgress == null) {
//            ll_download.visibility = View.VISIBLE
            var destination =
                "${getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/"
            val fileName = resources.getString(R.string.apk_name)
            destination = destination.plus(fileName)
            val uri = Uri.parse("file://$destination")

            //Delete update file if exists
            val file = File(destination)
            if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
                file.delete()

            downloadFileWithProgress =
                DownloadFileWithProgress(
                    WeakReference(this), downloadUrl, uri, false,
                    {

                        dialogView?.let { view ->
                            view.update_progressBar.progress = it
                            view.tv_updatePercentage.text = "$it%"
                            params?.leftMargin = dpToPx(it * 3)
                        }
                    }, { status ->
                        if (status) {
//                            ll_download.visibility = View.INVISIBLE

                            var intentAppInstall: Intent?
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                val apkUri =
                                    FileProvider.getUriForFile(
                                        this,
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        file
                                    )
                                intentAppInstall = Intent(Intent.ACTION_INSTALL_PACKAGE)
                                intentAppInstall.setData(apkUri)
                                intentAppInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            } else {
                                val apkUri = Uri.fromFile(file)
                                intentAppInstall = Intent(Intent.ACTION_VIEW)
                                intentAppInstall.setDataAndType(
                                    apkUri,
                                    "application/vnd.android.package-archive"
                                )
                            }

                            /*val intentAppInstall = Intent(Intent.ACTION_VIEW)
                            intentAppInstall.setDataAndType(uri, "application/vnd.android.package-archive")
                            intentAppInstall.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP*/
                            startActivity(intentAppInstall)
                            finish()
                        } else {
                            downloadFileWithProgress = null
//                            ll_download.visibility = View.GONE
                            messageAlert(
                                "Downloading failed",
                                "Your download failed.",
                                "RETRY"
                            ) { isAccepted, alertDialog ->
                                if (isAccepted) {
                                    alertDialog.dismiss()
                                    downloadFile()
                                } else {
                                    finish()
                                }
                            }
                        }
                    })
            downloadFileWithProgress?.start()
        }
    }
}