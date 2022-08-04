package com.example.newsgb.utils

import android.content.Context

class PrivateSharedPreferences(context: Context) {

    private val prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
    private var sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun save(countryPosition: Int) {
        sharedPreferences.edit().putInt(prefName, countryPosition).apply()
    }

    fun read(): Int  = sharedPreferences.getInt(prefName, 0)
}