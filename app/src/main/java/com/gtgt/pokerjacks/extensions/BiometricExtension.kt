package com.gtgt.pokerjacks.extensions

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

fun Activity.showBiometricPrompt(callback: () -> Unit, cancelCallBack: () -> Unit) {
    val authenticationCallback = getAuthenticationCallback(this, callback = {
        callback()
    },
        cancelCallBack = {
            cancelCallBack()
        })
    val mBiometricPrompt =
        BiometricPrompt(this as FragmentActivity, getMainThreadExecutor(), authenticationCallback)

    // Set prompt info
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setDescription("Description")
        .setTitle("Rummy")
//            .setSubtitle("Subtitle")
        .setNegativeButtonText("Cancel")
        // Allows device pin
//            .setDeviceCredentialAllowed(true)
        .build()

    // Show biometric prompt
    mBiometricPrompt.authenticate(promptInfo)
}

private fun getAuthenticationCallback(
    context: Context,
    callback: () -> Unit,
    cancelCallBack: () -> Unit
): BiometricPrompt.AuthenticationCallback {
    // Callback for biometric authentication result
    return object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            cancelCallBack()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            /*vibrate(context, onVibrate = {
                callback()
            })*/
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
//            log("onAuthFailed", "onAuthFailed")
        }
    }
}

private fun getMainThreadExecutor(): Executor {
    return MainThreadExecutor()
}

private class MainThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())

    override fun execute(r: Runnable) {
        handler.post(r)
    }
}