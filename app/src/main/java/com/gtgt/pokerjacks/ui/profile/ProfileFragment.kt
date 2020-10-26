package com.gtgt.pokerjacks.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.launchActivity
import com.gtgt.pokerjacks.extensions.onOneClick
import com.gtgt.pokerjacks.ui.profile.verify_address.VerifyAddressActivity
import com.gtgt.pokerjacks.ui.profile.verify_pan.VerifyPanActivity
import com.gtgt.pokerjacks.ui.profile.vrify_email.VerifyEmailActivity
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_email_verification.onOneClick {
            launchActivity<VerifyEmailActivity> { }
        }

        btn_pan_verification.onOneClick {
            launchActivity<VerifyPanActivity> { }
        }

        btn_address_verification.onOneClick {
            launchActivity<VerifyAddressActivity> { }
        }

        cv_responsibleGaming.onOneClick {
            launchActivity<ResponsibleGamingActivity> {  }
        }

        cv_manageBankAcc.onOneClick {
            launchActivity<ManageBankAccountActivity> {  }
        }
    }
}