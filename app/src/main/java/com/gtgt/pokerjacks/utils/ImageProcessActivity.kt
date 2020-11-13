package com.gtgt.pokerjacks.utils

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageProcessActivity : BaseActivity() {
    private val TAG = ImageProcessActivity::class.java.simpleName

    companion object {
        val INTENT_IMAGE_PICKER_OPTION = "image_picker_option"
        val INTENT_ASPECT_RATIO_X = "aspect_ratio_x"
        val INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y"
        val INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio"
        val INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality"
        val INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height"
        val INTENT_BITMAP_MAX_WIDTH = "max_width"
        val INTENT_BITMAP_MAX_HEIGHT = "max_height"


        val REQUEST_IMAGE_CAPTURE = 0
        val REQUEST_GALLERY_IMAGE = 1

        private var lockAspectRatio = false
        var setBitmapMaxWidthHeight = false
        private var ASPECT_RATIO_X = 16
        var ASPECT_RATIO_Y = 9
        var bitmapMaxWidth = 1000
        var bitmapMaxHeight = 1000
        private var IMAGE_COMPRESSION = 80
        var fileName: String? = null

        fun showImagePickerOptions(context: Context, listener: PickerOptionListener) {
            // setup the alert builder
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.set_profile_photo_title))

            // add a list
            val animals = arrayOf<String>(
                context.getString(R.string.take_picture_title),
                context.getString(R.string.choose_frm_gallery_title)
            )
            builder.setItems(animals) { _, which ->
                when (which) {
                    0 -> listener.onTakeCameraSelected()
                    1 -> listener.onChooseGallerySelected()
                }
            }

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    interface PickerOptionListener {
        fun onTakeCameraSelected()

        fun onChooseGallerySelected()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)


        supportActionBar?.hide()

        val intent = intent
        if (intent == null) {
            Toast.makeText(applicationContext, "toast_image_intent_null", Toast.LENGTH_LONG).show()
            return
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X)
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y)
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION)
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false)
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false)
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth)
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight)

        val requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage()
        } else {
            chooseImageFromGallery()
        }
    }

    private fun takeCameraImage() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        fileName = System.currentTimeMillis().toString() + ".jpg"
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            getCacheImagePath(fileName!!)
                        )
                        if (takePictureIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
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

    private fun chooseImageFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        try {
                            val pickPhoto = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
                        } catch (ex: java.lang.Exception) {
                            val pickPhoto = Intent()
                            pickPhoto.setTypeAndNormalize("image/*")
                            pickPhoto.addCategory(Intent.CATEGORY_OPENABLE)
                            pickPhoto.putExtra(
                                Intent.EXTRA_MIME_TYPES,
                                arrayOf("image/jpeg", "image/png")
                            )
                            pickPhoto.action = Intent.ACTION_GET_CONTENT
                            startActivityForResult(
                                Intent.createChooser(pickPhoto, "Choose Picture"),
                                REQUEST_GALLERY_IMAGE
                            )
                        }
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> if (resultCode == Activity.RESULT_OK) {
                    cropImage(getCacheImagePath(fileName!!))
                } else {
                    setResultCancelled()
                }
                REQUEST_GALLERY_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                    val imageUri = data!!.data
                    cropImage(imageUri)
                } else {
                    setResultCancelled()
                }
                UCrop.REQUEST_CROP -> if (resultCode == Activity.RESULT_OK) {
                    handleUCropResult(data)
                } else {
                    setResultCancelled()
                }
                UCrop.RESULT_ERROR -> {
                    val cropError = UCrop.getError(data!!)
                    setResultCancelled()
                }
                else -> setResultCancelled()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cropImage(sourceUri: Uri?) {
        val destinationUri = Uri.fromFile(File(cacheDir, queryName(contentResolver, sourceUri)))
        val options = UCrop.Options()
//        options.setCompressionQuality(IMAGE_COMPRESSION)

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setFreeStyleCropEnabled(true)
//        options.setCompressionQuality(90)

        /*if (lockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X.toFloat(), ASPECT_RATIO_Y.toFloat())

        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)*/

        UCrop.of(sourceUri!!, destinationUri)
            .withOptions(options)
            .start(this)
    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val resultUri = UCrop.getOutput(data)
        setResultOk(resultUri)
    }

    private fun setResultOk(imagePath: Uri?) {
        val intent = Intent()
        intent.putExtra("path", imagePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setResultCancelled() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun getCacheImagePath(fileName: String): Uri {
        val path = File(externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return FileProvider.getUriForFile(this@ImageProcessActivity, "$packageName.provider", image)
    }

    private fun queryName(resolver: ContentResolver, uri: Uri?): String {
        val returnCursor = resolver.query(uri!!, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    fun clearCache(context: Context) {
        val path = File(context.externalCacheDir, "camera")
        if (path.exists() && path.isDirectory) {
            for (child in path.listFiles()) {
                child.delete()
            }
        }
    }
}