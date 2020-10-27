package com.gtgt.pokerjacks.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.gtgt.pokerjacks.MyApplication
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

abstract class BaseViewModel : ViewModel(), KodeinAware {
    override val kodein by kodein(MyApplication.appContext!!)

    var activity: BaseActivity? = null
    var context: Context? = null
}