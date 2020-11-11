package com.gtgt.pokerjacks.ui.splash_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.jsonObject
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.execute
import com.gtgt.pokerjacks.ui.login.models.WebPageUrls

class SplashViewModel : BaseViewModel() {
    fun getWebPageUrls(): LiveData<WebPageUrls> {
        val webPageUrls: MutableLiveData<WebPageUrls> = MutableLiveData()
        apiServicesPlatform.getUrls().execute {
            webPageUrls.value = it
        }
        return webPageUrls
    }

    fun checkUpdate(): LiveData<CheckUpdateResponse> {
        val response: MutableLiveData<CheckUpdateResponse> = MutableLiveData()
        apiServicesUpdate.checkUpdateAvailable(
            jsonObject(
                "productId" to "POKER",
                "productVersion" to BuildConfig.VERSION_NAME
            )
        ).execute {
            response.value = it
        }
        return response
    }
}