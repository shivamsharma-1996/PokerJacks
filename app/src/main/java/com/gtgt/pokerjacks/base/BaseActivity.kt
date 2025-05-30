package com.gtgt.pokerjacks.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.retrofit.ApiInterfaceBonus
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlatform
import com.gtgt.pokerjacks.retrofit.ApiInterfaceTableManager
import com.gtgt.pokerjacks.retrofit.ApiInterfaceWallet
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

var currentActivity: AppCompatActivity? = null

abstract class BaseActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein(MyApplication.appContext!!)

    val apiServicesPlatform: ApiInterfacePlatform by instance()
    val apiInterfaceBonus: ApiInterfaceBonus by instance()
    val apiInterfaceTableManager: ApiInterfaceTableManager by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentActivity = this
    }

    override fun onResume() {
        super.onResume()
        currentActivity = this
    }

}

