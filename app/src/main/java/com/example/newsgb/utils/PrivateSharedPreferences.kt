package com.example.newsgb.utils

import android.content.Context

class PrivateSharedPreferences(context: Context, private val prefName: String) {

    private var sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun save(index: Int) {
        sharedPreferences.edit().putInt(prefName, index).apply()
    }

    fun read(): Int  = sharedPreferences.getInt(prefName, 0)
}