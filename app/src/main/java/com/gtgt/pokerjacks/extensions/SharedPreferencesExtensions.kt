package com.gtgt.pokerjacks.extensions

import android.app.Activity
import com.gtgt.pokerjacks.MyApplication

fun putString(key: String, value: String) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putString(key, value).apply()
}

fun putResponse(key: String, value: String?) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putString(key, value).apply()
}

fun putModel(key: String, value: Any) {
    val json = gson.toJson(value)
    val sp = MyApplication.sharedPreferences
    sp.edit().putString(key, json).apply()
}

fun retrieveResponse(key: String): String? {
    val sp = MyApplication.sharedPreferences
    return sp.getString(key, "")
}

fun putInt(key: String, value: Int) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putInt(key, value).apply()
}

fun putLong(key: String, value: Long) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putLong(key, value).apply()
}

fun putBoolean(key: String, value: Boolean) {
    val sp = MyApplication.sharedPreferences
    sp.edit().putBoolean(key, value).apply()
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

fun retrieveBoolean(key: String, default: Boolean = false): Boolean {
    val sp = MyApplication.sharedPreferences
    return sp.getBoolean(key, default)
}

fun Activity.removeSharedPrefByKey(key: String) {
    val sp = MyApplication.sharedPreferences
    sp.edit().remove(key).apply()
}

fun retrievePermanentString(key: String, default: String = ""): String {
    val sp = MyApplication.sharedPreferencesDontClear
    return sp.getString(key, default)!!
}

fun putPermanentString(key: String, value: String) {
    val sp = MyApplication.sharedPreferencesDontClear
    sp.edit().putString(key, value).apply()
}

fun putPermanentBoolean(key: String, value: Boolean) {
    val sp = MyApplication.sharedPreferencesDontClear
    sp.edit().putBoolean(key, value).apply()
}

fun retrievePermanentBoolean(key: String, default: Boolean = false): Boolean {
    val sp = MyApplication.sharedPreferencesDontClear
    return sp.getBoolean(key, default)
}

fun putPermanentInt(key: String, value: Int) {
    val sp = MyApplication.sharedPreferencesDontClear
    sp.edit().putInt(key, value).apply()
}

fun retrievePermanentInt(key: String, default: Int = 0): Int {
    val sp = MyApplication.sharedPreferencesDontClear
    return sp.getInt(key, default)
}