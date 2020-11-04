package com.gtgt.pokerjacks.ui.profile.profile

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.base.BaseFragment
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlatform
import com.gtgt.pokerjacks.ui.MainActivity
import com.gtgt.pokerjacks.ui.profile.manage_account.ManageBankAccountActivity
import com.gtgt.pokerjacks.ui.profile.profile.viewModel.ProfileViewModel
import com.gtgt.pokerjacks.ui.profile.suspend_account.ResponsibleGamingActivity
import com.gtgt.pokerjacks.ui.profile.verify_address.VerifyAddressActivity
import com.gtgt.pokerjacks.ui.profile.verify_pan.VerifyPanActivity
import com.gtgt.pokerjacks.ui.profile.vrify_email.VerifyEmailActivity
import com.gtgt.pokerjacks.utils.Constants
import com.gtgt.pokerjacks.utils.ImagePickFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar_layout_nav.*

class ProfileFragment : ImagePickFragment() {

    private val profilePicRequestCode = 100
    private val profileViewModel: ProfileViewModel by viewModel()
    private var isEmailVerified = false
    private var isPanVerified = false
    private var panVerifiedStatus = ""
    var winningsAmt = 0
    lateinit var v: View
    lateinit var alertDialog: AlertDialog
    private val REQUEST_CODE_USERNAME = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onImagePicked(bitmap: Bitmap?, requestCode: Int) {
        bitmap?.let {
            iv_profile.loadBitmap(bitmap)
            profileViewModel.uploadProfilePic(bitmap).observe(this, Observer {
                showSnack(it)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_settings.setOnClickListener {
            if (ll_settings.visibility == View.VISIBLE) {
                ll_settings.visibility = View.GONE
                tv_settings.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_gray, 0)
            } else {
                ll_settings.visibility = View.VISIBLE
                tv_settings.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_less_gray, 0)
            }
        }

        if (retrievePermanentBoolean("IS_FP_ENABLE")) {
            fp_switch.isChecked = true
        }

        tv_changeMPIN.onOneClick {
            launchActivity<ChangeMPINActivity> { }
        }

        fp_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                context?.let {
                    if (canAuthenticateWithBiometrics(it)) {  // Check whether this device can authenticate with biometrics
                        activity?.showBiometricPrompt(
                            callback = {
                                putPermanentBoolean("IS_FP_ENABLE", true)
                            },
                            cancelCallBack = {
                                fp_switch.isChecked = false
                            }
                        )
                    } else {
                        // Cannot use biometric prompt
                        showSnack("Cannot use biometric")
                    }
                }
            } else {
                putPermanentBoolean("IS_FP_ENABLE", false)
            }
        }

        profileViewModel.userProfileInfo.observe(viewLifecycleOwner, Observer {
            tv_username.text = it.username
            tv_user_phone_number.text = it.mobile
            tv_user_email.text = it.email
            iv_profile.loadURL(it.profile_pic_path, R.drawable.ic_profile_placeholder)

            this.isEmailVerified = it.isEmailVerified
            this.isPanVerified =
                it.isPanVerified == Constants.DocumentErrorCodes.USER_DETAILS_APPROVED.code
            panVerifiedStatus=it.isPanVerified

            if (!it.isEmailVerified) {
                btn_start_verification.text = "Verify Email"
                btn_email_verification.onOneClick {
                    launchActivity<VerifyEmailActivity>()
                }

                btn_start_verification.onOneClick {
                    launchActivity<VerifyEmailActivity>()
                }

                btn_pan_verification.onOneClick {
                    showSnack("Please Verify Email to Proceed")
                }

                btn_address_verification.onOneClick {
                    showSnack("Please Verify Email to Proceed")
                }

            } else {
                iv_email_verification_status.visibility = View.VISIBLE
                iv_email_verification_status.setImageResource(R.drawable.ic_approved)
                btn_email_verification.onOneClick {
                    launchActivity<VerifyEmailActivity> {
                        putExtra("EMAIL_VERIFIED", true)
                        putExtra("USER_EMAIL", it.email)
                    }
                }

                when (it.isPanVerified) {
                    Constants.DocumentErrorCodes.USER_DETAILS_NORECORD.code -> {
                        btn_pan_verification.alpha = 1f
                        btn_start_verification.text = "Verify PAN"
                        btn_pan_verification.onOneClick {
                            launchActivity<VerifyPanActivity>()
                        }

                        btn_start_verification.onOneClick {
                            launchActivity<VerifyPanActivity>()
                        }

                        btn_address_verification.onOneClick {
                            showSnack("Please Verify PAN to Proceed")
                        }

                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_PENDING.code -> {
                        iv_pan_verification_status.visibility = View.VISIBLE
                        iv_pan_verification_status.setImageResource(R.drawable.pending_icon)
                        btn_pan_verification.alpha = 1F
                        btn_start_verification.text = "Verify PAN"
                        btn_pan_verification.onOneClick {
                            launchActivity<VerifyPanActivity>()
                        }

                        btn_start_verification.onOneClick {
                            launchActivity<VerifyPanActivity>()
                        }
                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_REJECTED.code -> {
                        iv_pan_verification_status.visibility = View.VISIBLE
                        iv_pan_verification_status.setImageResource(R.drawable.rejected_icon)
                        btn_start_verification.text = "Verify PAN"
                        btn_pan_verification.alpha = 1F
                        btn_pan_verification.onOneClick {
                            launchActivity<VerifyPanActivity>()
                        }

                        btn_start_verification.onOneClick {
                            launchActivity<VerifyPanActivity>()
                        }
                    }
                    else -> {
                        iv_pan_verification_status.visibility = View.VISIBLE
                        iv_pan_verification_status.setImageResource(R.drawable.ic_approved)
                        btn_pan_verification.alpha = 1F
                        btn_pan_verification.onOneClick {
                            launchActivity<VerifyPanActivity> { }
                        }

                        when (it.isAddressVerified) {
                            Constants.DocumentErrorCodes.USER_DETAILS_NORECORD.code, null -> {
                                btn_start_verification.text = "Verify Address"
                                btn_pan_verification.alpha = 1F
                                btn_start_verification.onOneClick {
                                    launchActivity<VerifyAddressActivity>()
                                }

                                btn_address_verification.onOneClick {
                                    launchActivity<VerifyAddressActivity> { }
                                }
                            }
                            Constants.DocumentErrorCodes.USER_DETAILS_PENDING.code -> {
                                iv_address_verification_status.visibility = View.VISIBLE
                                iv_address_verification_status.setImageResource(R.drawable.pending_icon)
                                btn_start_verification.text = "Verify Address"
                                btn_address_verification.alpha = 1F
                                btn_start_verification.onOneClick {
                                    launchActivity<VerifyAddressActivity>()
                                }

                                btn_address_verification.onOneClick {
                                    launchActivity<VerifyAddressActivity> { }
                                }
                            }
                            Constants.DocumentErrorCodes.USER_DETAILS_REJECTED.code -> {
                                iv_address_verification_status.visibility = View.VISIBLE
                                iv_address_verification_status.setImageResource(R.drawable.rejected_icon)
                                btn_start_verification.text = "Verify Address"
                                btn_address_verification.alpha = 1F
                                btn_start_verification.onOneClick {
                                    launchActivity<VerifyAddressActivity>()
                                }

                                btn_address_verification.onOneClick {
                                    launchActivity<VerifyAddressActivity> { }
                                }
                            }
                            else -> {
                                iv_address_verification_status.visibility = View.VISIBLE
                                iv_address_verification_status.setImageResource(R.drawable.ic_approved)
                                btn_address_verification.alpha = 1F
                                btn_start_verification.text = "Approved"
                                btn_address_verification.onOneClick {
                                    launchActivity<VerifyAddressActivity> { }
                                }
                            }
                        }


                    }
                }
            }
        })

        profileViewModel.getDespositeLimit.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                tv_deposit.text =
                    "${resources.getString(R.string.rupee)}${it.info.deposits.numberCalculation()} Used"
                tv_limit.text =
                    "${resources.getString(R.string.rupee)}${it.info.limit.numberCalculation()}"
                tv_renewalDate.text = "Renews on ${it.info.renewalDate}"

                progressBarLimit.max = it.info.limit.toInt()
                progressBarLimit.progress = it.info.deposits.toInt()

                btn_changeLimits.onOneClick {
                    launchActivity<ChangeLimitsActivity>(requestCode = 1) {
                        putExtra("isEmailVerified", isEmailVerified)
                        putExtra("isPanVerified", isPanVerified)
                        putExtra("panVerifiedStatus", panVerifiedStatus)
                        putExtra("minLimit", it.info.minLimit)
                        putExtra("maxLimit", it.info.maxLimit)
                        putExtra("userLimit", it.info.limit)
                    }
                }
            }
        })

        cv_responsibleGaming.onOneClick {
            launchActivity<ResponsibleGamingActivity> {  }
        }

        cv_manageBankAcc.onOneClick {
            launchActivity<ManageBankAccountActivity> {  }
        }

        iv_hb.onOneClick {
            (activity as MainActivity).drawer_layout.openDrawer(GravityCompat.START)
        }

        iv_profile.onOneClick {
            choosePicture(profilePicRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            profileViewModel.getDepositLimit()
        }

        if (resultCode == REQUEST_CODE_USERNAME) {
            profileViewModel.getUserProfileDetailsInfo()
        }
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getUserProfileDetailsInfo()
        profileViewModel.getDepositLimit()
    }
}