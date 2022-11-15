package com.robivan.newsgb.main.ui

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.robivan.newsgb.App
import com.robivan.newsgb.R
import com.robivan.newsgb._core.ui.store.NewsStore
import com.robivan.newsgb.databinding.MainActivityBinding
import com.robivan.newsgb.utils.Constants
import com.robivan.newsgb.utils.ContextUtils
import com.robivan.newsgb.utils.PreferencesHelper
import com.robivan.newsgb.utils.ui.Countries
import com.robivan.newsgb.utils.ui.ThemeModes
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    @Suppress("unused")
    val newsStore: NewsStore by inject()
    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApplicationThemeMode()
        setCountryCodeForApi()
        installSplashScreen().run {
            viewModel.getInitialData()
            this.setKeepOnScreenCondition { true }
            Thread.sleep(1000)
            this.setKeepOnScreenCondition { false }
        }
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.CustomThemeIndigo)
        startMainScreen()
        checkIsItFirstLaunchApp()
    }

    override fun attachBaseContext(newBase: Context) {
        val selectedLang = PreferencesHelper(
            context = newBase,
            prefName = Constants.APP_PREFERENCES_LANGUAGE
        ).readString()
        val localUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, selectedLang)
        super.attachBaseContext(localUpdatedContext)
    }

    private fun setApplicationThemeMode() = PreferencesHelper(
        context = this,
        prefName = Constants.APP_PREFERENCES_THEME_MODE
    ).readString().let { themeMode ->
        when (themeMode) {
            ThemeModes.LIGHT_MODE.name -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            ThemeModes.DARK_MODE.name -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeModes.BATTERY_MODE.name -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            )
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setCountryCodeForApi() = PreferencesHelper(
        context = this,
        prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
    ).readInt().let { index ->
        App.countryCode = Countries.values()[index].countryCode
    }

    private fun startMainScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, NavigationFragment.newInstance())
            .commit()
    }

    private fun checkIsItFirstLaunchApp() {
        val prefHelper = PreferencesHelper(context = this, prefName = Constants.APP_PREFERENCES_FIRST_LAUNCH_APP)
        val isFirstAppLaunch = prefHelper.readBoolean()
        if (isFirstAppLaunch) {
            prefHelper.save(false)
            showAttentionAlertDialog()
        }
    }

    private fun showAttentionAlertDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.first_launch_app_alert_dialog_fragment)
            .setCancelable(true)
            .create()
            .show()
    }
}