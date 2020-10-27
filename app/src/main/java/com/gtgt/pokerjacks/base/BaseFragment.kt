package com.gtgt.pokerjacks.base

import androidx.fragment.app.Fragment
import com.gtgt.pokerjacks.MyApplication
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

abstract class BaseFragment : Fragment(), KodeinAware {

    override val kodein by kodein(MyApplication.appContext!!)
}