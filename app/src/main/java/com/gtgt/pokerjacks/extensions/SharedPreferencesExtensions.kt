package com.gtgt.pokerjacks.extensions

import android.app.Activity
import com.gtgt.pokerjacks.MyApplication

fun putString(key: String, value: String) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putString(key, value).commit()
}

fun putResponse(key: String, value: String?) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putString(key, value).commit()
}

fun putModel(key: String, value: Any) {
    val json = gson.toJson(value)
    val sp = MyApplication.sharedPreferences
    sp.edit().putString(key, json).commit()
}

fun retrieveResponse(key: String): String? {
    val sp = MyApplication.sharedPreferences
    return sp.getString(key, "")
}

fun putInt(key: String, value: Int) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putInt(key, value).commit()
}

fun putLong(key: String, value: Long) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putLong(key, value).commit()
}

fun putBoolean(key: String, value: Boolean) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putBoolean(key, value).commit()
}

fun retrieveInt(key: String, default: Int = -1): Int {
    val sp = MyApplication.sharedPreferences
    return sp.getInt(key, default)
}

fun retrieveLong(key: String): Long {
    val sp = MyApplication.sharedPreferences
    return sp.getLong(key, 0)
}

fun retrieveString(key: String, default: String = ""): String {
    val sp = MyApplication.sharedPreferences
    return sp.getString(key, default)!!
}

fun Activity.removeSharedPrefByKey(key: String) {
    val sp = MyApplication.sharedPreferences
    sp.edit().remove(key).commit()
}