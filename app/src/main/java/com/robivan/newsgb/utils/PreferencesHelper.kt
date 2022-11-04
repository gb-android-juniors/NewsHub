package com.robivan.newsgb.utils

import android.content.Context

class PreferencesHelper(context: Context, private val prefName: String) {

    private var sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun save(index: Int) {
        sharedPreferences.edit().putInt(prefName, index).apply()
    }

    fun save(parameter: String?) {
        sharedPreferences.edit().putString(prefName, parameter).apply()
    }

    fun save(parameter: Boolean) {
        sharedPreferences.edit().putBoolean(prefName, parameter).apply()
    }

    fun readInt(): Int  = sharedPreferences.getInt(prefName, 0)

    fun readString(): String? = sharedPreferences.getString(prefName, null)

    fun readBoolean(): Boolean = sharedPreferences.getBoolean(prefName, true)
}