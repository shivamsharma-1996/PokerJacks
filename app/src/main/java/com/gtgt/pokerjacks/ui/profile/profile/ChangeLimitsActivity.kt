package com.gtgt.pokerjacks.ui.profile.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.base.BaseActivity
import com.gtgt.pokerjacks.extensions.*
import com.gtgt.pokerjacks.ui.profile.profile.viewModel.ProfileViewModel
import com.gtgt.pokerjacks.ui.profile.verify_pan.VerifyPanActivity
import com.gtgt.pokerjacks.ui.profile.vrify_email.VerifyEmailActivity
import com.gtgt.pokerjacks.utils.Constants
import kotlinx.android.synthetic.main.activity_change_limits.*
import kotlin.math.roundToInt

class ChangeLimitsActivity : BaseActivity() {

    private var isEmailVerified = false
    private var isPanVerified = false
    private var panVerifiedStatus = ""
    private var minLimit = 0.0
    private var maxLimit = 0.0
    private var userLimit = 0.0
    private val profileViewModel: ProfileViewModel by viewModel()
    val stepSize = 100
    var progressDecAmount = 0.0
    var progressIncAmount = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_limits)

        btn_closeDialog.onOneClick {
            setResult(0)
            finish()
        }

        if (intent != null) {
            isEmailVerified = intent.getBooleanExtra("isEmailVerified", false)
            isPanVerified = intent.getBooleanExtra("isPanVerified", false)
            panVerifiedStatus = intent.getStringExtra("panVerifiedStatus") ?: ""
            minLimit = intent.getDoubleExtra("minLimit", 0.0)
            maxLimit = intent.getDoubleExtra("maxLimit", 0.0)
            userLimit = intent.getDoubleExtra("userLimit", 0.0)

            progressDecAmount = minLimit
            progressIncAmount = userLimit
        }

        rb_increase.isChecked = true
        checkPanBank()
        setDefaulValues()
        increaseLimitUI()
        decreaseLimitUI()

        rg_checkLimits.setOnCheckedChangeListener { group, checkedId ->
            // checkedId is the RadioButton selected
            if (rb_decrease.isChecked) {
                ll_decreaseLimit.visibility = View.VISIBLE
                ll_increaseLimit.visibility = View.GONE
                ll_verifyEmailPan.visibility = View.GONE
            }
            if (rb_increase.isChecked) {
                ll_decreaseLimit.visibility = View.GONE
                checkPanBank()
            }
        }

        btn_verifyEmail.onOneClick {
            if (!isEmailVerified) {
                launchActivity<VerifyEmailActivity> { }
            } else if (!isPanVerified) {
                when (panVerifiedStatus) {
                    Constants.DocumentErrorCodes.USER_DETAILS_NORECORD.code -> {
                        launchActivity<VerifyPanActivity>()
                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_PENDING.code -> {
                        launchActivity<VerifyPanActivity>()
                    }
                    Constants.DocumentErrorCodes.USER_DETAILS_REJECTED.code -> {
                        launchActivity<VerifyPanActivity>()
                    }
                    else -> {
                        launchActivity<VerifyPanActivity> { }
                    }
                }
            } else {
                launchActivity<VerifyEmailActivity> { }
            }
            finish()
        }
    }

    private fun setDefaulValues() {
        seekbar_decrease.max = userLimit.roundToInt()
        seekbar_decrease.progress = minLimit.toInt()
        tv_decreasePlus.text = userLimit.numberCalculation()
        tv_decreaseMinus.text = minLimit.numberCalculation()
        tv_decreaseLimit.text = minLimit.numberCalculation()

        seekbar_increase.max = maxLimit.roundToInt()
        seekbar_increase.progress = userLimit.toInt()
        tv_increasePlus.text = maxLimit.numberCalculation()
        tv_increaseMinus.text = userLimit.numberCalculation()
        tv_increaseLimit.text = userLimit.numberCalculation()
    }

    fun increaseLimitUI() {
        iv_increaseMinus.setOnClickListener {
            seekbar_increase.progress = seekbar_increase.progress - 100
            progressIncAmount = seekbar_increase.progress.toDouble()
        }
        iv_increasePlus.setOnClickListener {
            seekbar_increase.progress = seekbar_increase.progress + 100
            progressIncAmount = seekbar_increase.progress.toDouble()
        }

        seekbar_increase.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                progressIncAmount =
                    ((progress / stepSize).toDouble().roundToInt() * stepSize).toDouble()
                if (progress <= userLimit) {
                    seekbar_increase.progress = userLimit.toInt()
                } else {
                    seekbar_increase.progress = progressIncAmount.toInt()
                }
                tv_increaseLimit.text = "₹${progressIncAmount.numberCalculation()}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    fun decreaseLimitUI() {
        iv_decreaseMinus.setOnClickListener {
            seekbar_decrease.progress = seekbar_decrease.progress - 100
            progressDecAmount = seekbar_decrease.progress.toDouble()
        }
        iv_decreasePlus.setOnClickListener {
            seekbar_decrease.progress = seekbar_decrease.progress + 100
            progressDecAmount = seekbar_decrease.progress.toDouble()
        }

        seekbar_decrease.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {

                progressDecAmount =
                    ((progress / stepSize).toDouble().roundToInt() * stepSize).toDouble()
                if (progress <= minLimit) {
                    seekbar_decrease.progress = minLimit.toInt()
                } else {
                    seekbar_decrease.progress = progressDecAmount.toInt()
                }
                tv_decreaseLimit.text = "₹${progressDecAmount.numberCalculation()}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        btn_decreaseLimits.onOneClick {
            profileViewModel.updateDepositLimits(progressDecAmount, "0")
        }

        btn_increaseLimits.onOneClick {
            profileViewModel.updateDepositLimits(progressIncAmount, "1")
        }

        profileViewModel.updateDepositLimits.observe(this, Observer {
            showToast(it.description)
            if (it.success) {
                setResult(1)
                finish()
            }
        })
    }

    fun checkPanBank() {
        if ((!isEmailVerified || !isPanVerified) && userLimit == maxLimit) {
            ll_increaseLimit.visibility = View.GONE
            ll_verifyEmailPan.visibility = View.VISIBLE

            if (!isPanVerified && isEmailVerified) {
                tv_verifyPanEmail.text = "*Please verify your PAN"
                btn_verifyEmail.text = "Verify PAN"
            } else if (isPanVerified && !isEmailVerified) {
                tv_verifyPanEmail.text = "*Please verify your Email"
                btn_verifyEmail.text = "Verify Email"
            } else {
                tv_verifyPanEmail.text = "*Please verify your Email and PAN"
                btn_verifyEmail.text = "Verify Email"
            }
        } else {
            ll_increaseLimit.visibility = View.VISIBLE
            ll_verifyEmailPan.visibility = View.GONE
        }
    }
}