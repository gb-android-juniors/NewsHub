package com.robivan.newsgb.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.*

class ContextUtils(base: Context) : ContextWrapper(base) {

    companion object {
        fun updateLocale(c: Context, selectedLang: String?): ContextWrapper {
            var context = c
            val resources: Resources = context.resources
            val configuration: Configuration = resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeToSwitchTo = selectedLang?.let { Locale(it) }
                    ?: Resources.getSystem().configuration.locales[0]
                val localList = LocaleList(localeToSwitchTo)
                configuration.setLocales(localList)
            } else @Suppress("DEPRECATION") {
                val localeToSwitchTo =
                    selectedLang?.let { Locale(it) } ?: Resources.getSystem().configuration.locale
                configuration.locale = localeToSwitchTo
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context = context.createConfigurationContext(configuration)
            } else @Suppress("DEPRECATION") {
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
            return ContextUtils(context)
        }

    }
}