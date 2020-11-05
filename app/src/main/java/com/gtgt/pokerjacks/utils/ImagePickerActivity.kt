package com.gtgt.pokerjacks.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.gtgt.pokerjacks.base.BaseActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException

abstract class ImagePickerActivity : BaseActivity() {
    private val REQUEST_IMAGE = 100

    abstract fun onImagePicked(bitmap: Bitmap?)

    fun choosePicture() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions()
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

    private fun showImagePickerOptions() {
        ImageProcessActivity.showImagePickerOptions(
            this,
            object : ImageProcessActivity.PickerOptionListener{

                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    launchGalleryIntent()
                }
            })
    }

    private fun launchCameraIntent() {
        val intent = Intent(this, ImageProcessActivity::class.java)
        intent.putExtra(
            ImageProcessActivity.INTENT_IMAGE_PICKER_OPTION,
            ImageProcessActivity.REQUEST_IMAGE_CAPTURE
        )

        // setting aspect ratio
        intent.putExtra(ImageProcessActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImageProcessActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImageProcessActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(ImageProcessActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImageProcessActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImageProcessActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(this, ImageProcessActivity::class.java)
        intent.putExtra(
            ImageProcessActivity.INTENT_IMAGE_PICKER_OPTION,
            ImageProcessActivity.REQUEST_GALLERY_IMAGE
        )

        // setting aspect ratio
        intent.putExtra(ImageProcessActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImageProcessActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImageProcessActivity.INTENT_ASPECT_RATIO_Y, 1)
        startActivityForResult(intent, REQUEST_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.getParcelableExtra<Uri>("path")
                try {
                    if (this.applicationContext != null && uri != null) {
                        try {
                            ImageUtils.normalizeImageForUri(this, uri)?.let {
                                onImagePicked(it)
                            }
                        } catch (ex: Exception) {
                            MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                uri
                            )?.let {
                                onImagePicked(it)
                            }
                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }
}