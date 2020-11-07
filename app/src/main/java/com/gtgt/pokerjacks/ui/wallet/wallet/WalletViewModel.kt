package com.gtgt.pokerjacks.ui.wallet.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.double
import com.github.salomonbrys.kotson.get
import com.gtgt.pokerjacks.base.AnyModel
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.extensions.isRunning
import com.gtgt.pokerjacks.extensions.showSnack

class WalletViewModel : BaseViewModel() {
    private val _walletDetailsResponse = MutableLiveData<WalletDetailsResponse>()
    val walletDetailsResponse: LiveData<WalletDetailsResponse> = _walletDetailsResponse

    val _playWalletDetailsResponse = MutableLiveData<Double>()
    val playWalletDetailsResponse: LiveData<Double> = _playWalletDetailsResponse

    fun getWalletDetailsByToken(callback: ((Double) -> Unit)? = null) {
        apiInterfaceWallet.getWalletDetailsByToken().execute(activity, true) {
            _walletDetailsResponse.value = it
            if (it.success) {
                callback?.invoke(it.info.total)
            } else {
                callback?.invoke(-1.0)
            }
        }
    }

    fun getPlayWalletDetailsByToken(callback: ((Double) -> Unit)? = null) {
        apiServicesPlayWallet.getPlayWalletDetailsByToken().execute(activity) {
            if (it.success) {
                _playWalletDetailsResponse.value = it.info["walletBalance"].double
                callback?.invoke(it.info["walletBalance"].double)
            } else {
                callback?.invoke(-1.0)
            }
        }
    }

    fun addChips(callback: (AnyModel) -> Unit) {
        apiServicesPlayWallet.addChips().execute(activity) {
            if (it.success && activity.isRunning()) {
                callback(it)
            } else {
                activity?.showSnack(it.description)
            }
        }
    }

}