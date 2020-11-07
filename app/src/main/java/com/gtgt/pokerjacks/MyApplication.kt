package com.gtgt.pokerjacks

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import androidx.lifecycle.LifecycleObserver
import com.androidisland.vita.startVita
import com.gtgt.pokerjacks.retrofit.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class MyApplication : Application(), LifecycleObserver, KodeinAware {
    override val kodein: Kodein = Kodein.lazy {

        bind() from singleton { WebServicesPlatform.retrofit.create(ApiInterfacePlatform::class.java) }
        bind() from singleton { WebServicesBonus.retrofitBonusService.create(ApiInterfaceBonus::class.java) }
        bind() from singleton { WebServicesBonus.retrofitBonusService.create(ApiInterfaceWallet::class.java) }
        bind() from singleton { WebServicesBonus.retrofitBonusService.create(ApiInterfacePlayWallet::class.java) }

        bind() from singleton {
            this@MyApplication.applicationContext!!.getSharedPreferences(
                "poker_jacks",
                Context.MODE_PRIVATE
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        startVita()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        instance = this

        appContext = applicationContext
    }

    companion object {
        var isSplashScreenOpen = false

        //for match_remainder_feature
        val TAG = "MyApplication"

        @SuppressLint("StaticFieldLeak")
        @get:Synchronized
        var instance: MyApplication? = null
            private set
        var appContext: Context? = null
            private set

        val sharedPreferences: SharedPreferences by lazy {
            appContext!!.getSharedPreferences(
                "poker_jacks",
                Context.MODE_PRIVATE
            )
        }

        val sharedPreferencesDontClear: SharedPreferences by lazy {
            appContext!!.getSharedPreferences(
                "poker_jacks_permanent",
                Context.MODE_PRIVATE
            )
        }
    }


}