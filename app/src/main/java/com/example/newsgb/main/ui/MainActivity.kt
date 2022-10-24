package com.example.newsgb.main.ui

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.newsgb.App
import com.example.newsgb.R
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.databinding.MainActivityBinding
import com.example.newsgb.utils.Constants
import com.example.newsgb.utils.ContextUtils
import com.example.newsgb.utils.PreferencesHelper
import com.example.newsgb.utils.ui.Countries
import com.example.newsgb.utils.ui.ThemeModes
import com.yandex.mobile.ads.common.MobileAds
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

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
        initAdMob()
    }

    private fun initAdMob() {
        MobileAds.initialize(this) { Log.d("TAG", "SDK initialized") }
    }

    /**
     * Настраиваем язык приложения выбранный пользователем
     */
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

    /**
     * метод который устанавливает код страны для запроса, сохраненный в SharedPreferences или по умолчанию
     */
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
}