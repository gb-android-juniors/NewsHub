package com.example.newsgb.main.ui

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.newsgb.App
import com.example.newsgb.R
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb.databinding.MainActivityBinding
import com.example.newsgb.utils.Constants
import com.example.newsgb.utils.PrivateSharedPreferences
import com.example.newsgb.utils.network.OnlineLiveData
import com.example.newsgb.utils.ui.AlertDialogFragment
import com.example.newsgb.utils.ui.Countries
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    val newsStore: NewsStore by inject()
    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: MainActivityBinding
    private var isNetworkAvailable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApplicationThemeMode()
        setApplicationLocale()
        getCountryCodeFromPreferences()
        installSplashScreen().run {
            viewModel.getInitialData()
            this.setKeepOnScreenCondition { true }
            Thread.sleep(1000)
            this.setKeepOnScreenCondition { false }
        }
        setTheme(R.style.CustomThemeIndigo)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //проверяем наличие интернет-подключения на старте
        isNetworkAvailable =
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true
        //запускаем главный фрагмент
        startMainScreen()
        //подписываемся на изменение наличия интернет-подключения
        subscribeToNetworkChange()
    }

    private fun setApplicationThemeMode() {
        PrivateSharedPreferences(
            context = this,
            prefName = Constants.APP_PREFERENCES_THEME_MODE
        ).readInt().let { index ->
            when (index) {
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun setApplicationLocale() {
        PrivateSharedPreferences(
            context = this,
            prefName = Constants.APP_PREFERENCES_LANGUAGE
        ).readString().let { localeToSet ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val local = localeToSet?.let { Locale(it) }
                    ?: Resources.getSystem().configuration.locales[0]
                val localeListToSet = LocaleList(local)
                LocaleList.setDefault(localeListToSet)
                resources.configuration.setLocales(localeListToSet)
            } else {
                val local =
                    localeToSet?.let { Locale(it) } ?: Resources.getSystem().configuration.locale
                resources.configuration.setLocale(local)
            }
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        }
    }

    /**
     * метод который устанавливает код страны для запроса, сохраненный в SharedPreferences или по умолчанию
     */
    private fun getCountryCodeFromPreferences() {
        val position = PrivateSharedPreferences(
            context = this,
            prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
        ).readInt()
        App.countryCode = Countries.values()[position].countryCode
    }

    /**
     * если интернет недоступен, уведомляем пользователя с помощью ImageView в активити и AlertDialog,
     * иначе запускаем основной фрагмент
     * */
    private fun startMainScreen() {
        if (!isNetworkAvailable && isDialogNull()) {
            showNoInternetConnectionInfo()
        } else if (isNetworkAvailable) {
            binding.networkLostImage.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, NavigationFragment.newInstance())
                .commit()
        }
    }

    /**
     * метод проверяет, что AlertDialog не отображается в данный момент
     **/
    private fun isDialogNull(): Boolean =
        supportFragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) == null

    /**
     * Метод делает картинку отсутствия интернета видимой в макете и запускает AlertDialog
     * */
    private fun showNoInternetConnectionInfo() {
        binding.networkLostImage.visibility = View.VISIBLE
        binding.mainContainer.visibility = View.GONE
        showNoInternetConnectionDialog()
    }

    private fun showNoInternetConnectionDialog() {
        showAlertDialog(
            getString(R.string.dialog_title_device_is_offline),
            getString(R.string.dialog_message_device_is_offline)
        )
    }

    private fun showAlertDialog(title: String?, message: String?) =
        AlertDialogFragment.newInstance(title, message)
            .show(supportFragmentManager, DIALOG_FRAGMENT_TAG)

    private fun subscribeToNetworkChange() {
        OnlineLiveData(this).observe(this@MainActivity) {
            isNetworkAvailable = it
            startMainScreen()
        }
    }

    companion object {
        const val DIALOG_FRAGMENT_TAG = "74a54328-5d62-46bf-ab6b-cbf5d8c79522"
    }
}