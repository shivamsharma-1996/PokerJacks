package com.gtgt.pokerjacks.ui.splash_screen

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.util.Log
import com.gtgt.pokerjacks.extensions.log
import java.lang.ref.WeakReference

class DownloadFileWithProgress(
    val activity: WeakReference<Activity>,
    val url: String,
    val destinationUri: Uri,
    val notifyAfterComplete: Boolean,
    val progress: (Int) -> Unit,
    val complete: (Boolean) -> Unit
) : Thread() {
    private lateinit var cursor: Cursor
    private lateinit var query: DownloadManager.Query
    private var lastBytesDownloadedSoFar: Int = 0
    private var totalBytes: Int = 0
    private var downloadPercentage = 0

    private val manager =
        activity.get()?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var downloadId = 0L

    override fun run() {
        super.run()

        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("")
        request.setTitle("")

        if (notifyAfterComplete) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        //set destination
        request.setDestinationUri(destinationUri)
        if (url.endsWith("apk")) {
            request.setMimeType("application/vnd.android.package-archive")
        }
        // get download service and enqueue file
        downloadId = manager.enqueue(request)
        query = DownloadManager.Query()
        query.setFilterById(downloadId)

        Log.i("downloadId", downloadId.toString())

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                activity.get()?.let { activity ->
                    val action = intent.action

                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE && id == downloadId) {
                        downloadId = -1L
                        activity.runOnUiThread { complete(true/*validDownload()*/) }
                        activity.unregisterReceiver(this)
                    }
                }
            }
        }

        val onClicked = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                activity.get()?.let { activity ->
                    val action = intent.action
                    if (action == DownloadManager.ACTION_NOTIFICATION_CLICKED) {
                        var intentAppInstall: Intent?
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intentAppInstall = Intent(Intent.ACTION_INSTALL_PACKAGE)
                            intentAppInstall.setData(destinationUri)
                            intentAppInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        } else {
                            intentAppInstall = Intent(Intent.ACTION_VIEW)
                            intentAppInstall.setDataAndType(
                                destinationUri,
                                "application/vnd.android.package-archive"
                            )
                        }
                        ctxt.startActivity(intentAppInstall)
                    }
                }
            }
        }

        activity.get()
            ?.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        /*activity.get()
            ?.registerReceiver(onClicked, IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED))*/

        while (downloadId > 0L) {
            try {
                log("downloadUrl", totalBytes)

                //Thread.sleep(200)
                cursor = manager.query(query)
                if (cursor.moveToFirst()) {

                    //get total bytes of the file
                    if (totalBytes <= 0) {
                        totalBytes =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    }

                    val bytesDownloadedSoFar =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                    if (bytesDownloadedSoFar == totalBytes && totalBytes > 0) {
                        this.interrupt()
                    } else {
                        //update progress bar
                        activity.get()?.let {
                            it.runOnUiThread {
                                downloadPercentage =
                                    (bytesDownloadedSoFar / totalBytes.toFloat() * 100).toInt()
                                progress(downloadPercentage)
                                //mProgressBar.setProgress(mProgressBar.getProgress() + (bytesDownloadedSoFar - lastBytesDownloadedSoFar))
                            }
                            lastBytesDownloadedSoFar = bytesDownloadedSoFar
                        }
                    }
                }
                cursor.close()
            } catch (e: Exception) {
                return
            }
        }
    }

    fun cancelDownload() {
        manager.remove(downloadId)
        downloadId = -1L
    }

    private fun validDownload(): Boolean {
        val c = manager.query(DownloadManager.Query().setFilterById(downloadId))

        if (c.moveToFirst()) {
            val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            return status == DownloadManager.STATUS_SUCCESSFUL
        }
        return false
    }

}