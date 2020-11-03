package com.gtgt.pokerjacks.ui.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.salomonbrys.kotson.jsonObject
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.base.BaseModel
import com.gtgt.pokerjacks.base.BaseViewModel
import com.gtgt.pokerjacks.extensions.executeNoError

class HomeViewModel : BaseViewModel() {

    private val _logout: MutableLiveData<BaseModel?> = MutableLiveData()
    val logout: LiveData<BaseModel?> = _logout

    fun logout(callback: ((BaseModel?) -> Unit)? = null): LiveData<BaseModel?> {
        val tokendata = jsonObject(
            "deviceToken" to MyApplication.sharedPreferencesDontClear.getString(
                "regId",
                ""
            )
        )
        apiServicesPlatform.logout()
            .executeNoError(activity) {
                _logout.value = it
                callback?.invoke(it)
            }
        return logout
    }
}