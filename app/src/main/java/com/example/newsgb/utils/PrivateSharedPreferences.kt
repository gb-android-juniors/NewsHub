package com.example.newsgb.utils

import android.content.Context

class PrivateSharedPreferences(context: Context) {

    private val file = Constants.APP_PREFERENCES_COUNTRY
    private val key = "COUNTRY_PREF"
    private var savedCountry = " "
    private var sharedPreferences = context.getSharedPreferences(file, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun save(country: String) {
        savedCountry = country
        editor.putString(key, country)
        editor.apply()
    }

    fun read(): String {
        savedCountry = sharedPreferences.getString(key, "country").toString()
        return savedCountry
    }


}