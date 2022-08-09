package com.example.newsgb.main.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.newsgb.App
import com.example.newsgb.R
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb._core.ui.store.NewsStoreHolder
import com.example.newsgb.databinding.MainActivityBinding
import com.example.newsgb.splash.ui.CustomSplashScreenFragment
import com.example.newsgb.utils.Constants
import com.example.newsgb.utils.PrivateSharedPreferences
import com.example.newsgb.utils.network.OnlineLiveData
import com.example.newsgb.utils.ui.AlertDialogFragment
import com.example.newsgb.utils.ui.Countries
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(), NewsStoreHolder {

    override val newsStore: NewsStore by inject()
    private val viewModel: MainViewModel by viewModel { parametersOf(newsStore) }

    private lateinit var binding: MainActivityBinding
    private var isNetworkAvailable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setApplicationTheme()
        super.onCreate(savedInstanceState)
        setTheme(R.style.CustomThemeIndigo)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startSplashScreen()
        //проверяем наличие интернет-подключения на старте
        isNetworkAvailable =
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true
        getCountryCodeFromPreferences()
        viewModel.getInitialData()
        //подписываемся на изменение наличия интернет-подключения
        subscribeToNetworkChange()
    }

    private fun setApplicationTheme() {
        PrivateSharedPreferences(
            context = this,
            prefName = Constants.APP_PREFERENCES_THEME_MODE
        ).read().let { index ->
            when (index) {
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    /**
     * метод который устанавливает код страны для запроса, сохраненный в SharedPreferences или по умолчанию
     */
    private fun getCountryCodeFromPreferences() {
        val position = PrivateSharedPreferences(
            context = this,
            prefName = Constants.APP_PREFERENCES_COUNTRY_CODE
        ).read()
        App.countryCode = Countries.values()[position].countryCode
    }

    /**
     * если интернет недоступен, уведомляем пользователя с помощью ImageView в активити и AlertDialog,
     * иначе запускаем основной фрагмент
     * */
    private fun startSplashScreen() {
        if (!isNetworkAvailable && isDialogNull()) {
            showNoInternetConnectionInfo()
        } else if (isNetworkAvailable) {
            binding.networkLostImage.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, CustomSplashScreenFragment.newInstance())
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
            startSplashScreen()
        }
    }

    companion object {
        const val DIALOG_FRAGMENT_TAG = "74a54328-5d62-46bf-ab6b-cbf5d8c79522"
    }
}