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
import kotlinx.android.synthetic.main.fragment_pass_port_uploader.*

class PassPortUploaderFragment : ImagePickFragment() {

    private val passportImageRequestCode = 500

    var ref_variable: SendFragmentDataToActivity? = null

    override fun onImagePicked(bitmap: Bitmap?, requestCode: Int) {

        iv_upload_passport_placeholder.visibility = View.GONE
        tv_upload_here.visibility = View.GONE
        iv_upload_passport.visibility = View.VISIBLE
        iv_upload_passport.setImageBitmap(bitmap)

        ref_variable!!.exchangeData("front_doc", bitmap!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pass_port_uploader, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ref_variable = activity as SendFragmentDataToActivity

        upload_passport.onOneClick {
            choosePicture(passportImageRequestCode)
        }

    }


}