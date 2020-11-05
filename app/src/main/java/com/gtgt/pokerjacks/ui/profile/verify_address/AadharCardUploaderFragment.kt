package com.gtgt.pokerjacks.ui.profile.verify_address

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.utils.ImagePickFragment
import kotlinx.android.synthetic.main.fragment_aadhar_card_uploader.*

class AadharCardUploaderFragment : ImagePickFragment() {
    override fun onImagePicked(bitmap: Bitmap?, requestCode: Int) {
        if (bitmap != null) {
            if (requestCode == aadharFrontImageRequestCode) {
                iv_document_front.visibility = View.VISIBLE
                iv_document_front.setImageBitmap(bitmap)
                iv_document_placeholder_front.visibility = View.GONE
                tv_front.visibility = View.GONE

                ref_variable!!.exchangeData("front_doc", bitmap)
            } else {
                iv_document_back.visibility = View.VISIBLE
                iv_document_back.setImageBitmap(bitmap)
                iv_document_placeholder_back.visibility = View.GONE
                tv_back.visibility = View.GONE

                ref_variable!!.exchangeData(
                    "back_doc", bitmap
                )
            }
        }
    }

    private val aadharFrontImageRequestCode = 200
    private val aadharBackImageRequestCode = 300

    var ref_variable: SendFragmentDataToActivity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aadhar_card_uploader, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ref_variable = activity as SendFragmentDataToActivity

        btn_upload_front.onOneClick {
            choosePicture(aadharFrontImageRequestCode)
        }

        btn_upload_back.onOneClick {
            choosePicture(aadharBackImageRequestCode)
        }
    }


}