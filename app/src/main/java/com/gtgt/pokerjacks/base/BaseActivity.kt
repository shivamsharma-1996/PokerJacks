package com.gtgt.pokerjacks.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlatform
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

abstract class BaseActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein(MyApplication.appContext!!)

    val apiServicesPlatform: ApiInterfacePlatform by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}

