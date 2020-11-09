package com.gtgt.pokerjacks.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.retrofit.ApiInterfaceBonus
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlatform
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlayWallet
import com.gtgt.pokerjacks.retrofit.ApiInterfaceWallet
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

abstract class BaseViewModel : ViewModel(), KodeinAware {
    override val kodein by kodein(MyApplication.appContext!!)

    var activity: BaseActivity? = null
    var context: Context? = null
    protected val apiServicesPlatform: ApiInterfacePlatform by instance()
    protected val apiInterfaceBonus: ApiInterfaceBonus by instance()
    protected val apiServicesWallet: ApiInterfaceWallet by instance()
    protected val apiServicesPlayWallet: ApiInterfacePlayWallet by instance()
}