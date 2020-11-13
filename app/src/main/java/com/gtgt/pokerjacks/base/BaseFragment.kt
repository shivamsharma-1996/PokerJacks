package com.gtgt.pokerjacks.base

import androidx.fragment.app.Fragment
import com.gtgt.pokerjacks.MyApplication
import com.gtgt.pokerjacks.retrofit.ApiInterfaceBonus
import com.gtgt.pokerjacks.retrofit.ApiInterfacePlatform
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

abstract class BaseFragment : Fragment(), KodeinAware {

    val apiServicesPlatform: ApiInterfacePlatform by instance()
    val apiInterfaceBonus: ApiInterfaceBonus by instance()

    override val kodein by kodein(MyApplication.appContext!!)
}