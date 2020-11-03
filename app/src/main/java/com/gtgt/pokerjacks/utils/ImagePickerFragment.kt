package com.gtgt.pokerjacks.utils


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.gtgt.pokerjacks.base.BaseFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException

abstract class ImagePickFragment : BaseFragment() {

    private var request_code = 0

    abstract fun onImagePicked(bitmap: Bitmap?, requestCode: Int)

    fun choosePicture(requestCode: Int) {
        Dexter.withActivity(activity)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        request_code = requestCode
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
            this.context!!,
            object : ImageProcessActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    launchGalleryIntent()
                }
            })
    }

    private fun launchCameraIntent() {
        val intent = Intent(this.context, ImageProcessActivity::class.java)
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

        startActivityForResult(intent, request_code)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(this.context, ImageProcessActivity::class.java)
        intent.putExtra(
            ImageProcessActivity.INTENT_IMAGE_PICKER_OPTION,
            ImageProcessActivity.REQUEST_GALLERY_IMAGE
        )

        // setting aspect ratio
        intent.putExtra(ImageProcessActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImageProcessActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImageProcessActivity.INTENT_ASPECT_RATIO_Y, 1)
        startActivityForResult(intent, request_code)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request_code) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.getParcelableExtra<Uri>("path")
                try {
                    if (context != null && uri != null) {
                        try {
                            ImageUtils.normalizeImageForUri(context!!, uri)?.let {
                                onImagePicked(it, requestCode)
                            }
                        } catch (ex: Exception) {
                            MediaStore.Images.Media.getBitmap(
                                context!!.contentResolver,
                                uri
                            )?.let {
                                onImagePicked(it, requestCode)
                            }
                        }
                    } else {
                    }
//                    onImagePicked(resized)

                } catch (e: IOException) {

                    e.printStackTrace()
                }

            }
        }
    }

}