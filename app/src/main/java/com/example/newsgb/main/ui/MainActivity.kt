package com.example.newsgb.main.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.newsgb.App
import com.example.newsgb.R
import com.example.newsgb._core.ui.store.NewsStore
import com.example.newsgb._core.ui.store.NewsStoreHolder
import com.example.newsgb.databinding.MainActivityBinding
import com.example.newsgb.splash.ui.CustomSplashScreenActivity
import com.example.newsgb.utils.PrivateSharedPreferences
import com.example.newsgb.utils.network.OnlineLiveData
import com.example.newsgb.utils.ui.AlertDialogFragment
import com.example.newsgb.utils.ui.Countries
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(), NewsStoreHolder {

    override val newsStore: NewsStore by inject()
    private val viewModel: MainViewModel by viewModel() { parametersOf(newsStore) }

    private lateinit var binding: MainActivityBinding
    private var isNetworkAvailable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.CustomThemeIndigo)
        setSplashScreen()
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //проверяем наличие интернет-подключения на старте
        isNetworkAvailable =
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true
        getCountryCodeFromPreferences()
        viewModel.getInitialData()
        //запускаем главный фрагмент
        startMainScreen(savedInstanceState)
        //подписываемся на изменение наличия интернет-подключения
        subscribeToNetworkChange(savedInstanceState)
    }

    /**
     * метод который устанавливает код страны для запроса, сохраненный в SharedPreferences или по умолчанию
     */
    private fun getCountryCodeFromPreferences() {
        val position = PrivateSharedPreferences(this).read()
        App.countryCode = Countries.values()[position].countryCode
    }

    /**
     * если интернет недоступен, уведомляем пользователя с помощью ImageView в активити и AlertDialog,
     * иначе запускаем основной фрагмент
     * */
    private fun startMainScreen(savedInstanceState: Bundle?) {
        if (!isNetworkAvailable && isDialogNull()) {
            showNoInternetConnectionInfo()
        } else if (isNetworkAvailable) {
            binding.networkLostImage.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, NavigationFragment.newInstance())
                    .commit()
            }
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

    private fun subscribeToNetworkChange(savedInstanceState: Bundle?) {
        OnlineLiveData(this).observe(this@MainActivity) {
            isNetworkAvailable = it
            startMainScreen(savedInstanceState)
        }
    }

    /**
     * Устанавливает дефолтный SplashScreen если версия андроид 12 и выше, и кастомный SplashScreen,
     * если у пользователя более ранняя версия андроид
     * */
    private fun setSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setDefaultSplashScreen()
        } else {
            setCustomSplashScreen()
        }
    }

    private fun setCustomSplashScreen() {
        startActivity(Intent(this@MainActivity, CustomSplashScreenActivity::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setDefaultSplashScreen() {
        setSplashScreenHideAnimation()
        setSplashScreenDuration()
    }

    /**
     * Прописываем анимацию
     * */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setSplashScreenHideAnimation() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideLeft = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_X,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideLeft.interpolator = AnticipateInterpolator()
            slideLeft.duration = SLIDE_LEFT_DURATION
            slideLeft.doOnEnd { splashScreenView.remove() }
            slideLeft.start()
        }
    }

    /**
     * настройка времени отображения splash screen
     * */
    private fun setSplashScreenDuration() {
        var isHideSplashScreen = false

        object : CountDownTimer(
            COUNTDOWN_DURATION, COUNTDOWN_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                isHideSplashScreen = true
            }
        }.start()
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isHideSplashScreen) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }

    companion object {
        const val DIALOG_FRAGMENT_TAG = "74a54328-5d62-46bf-ab6b-cbf5d8c79522"
        private const val SLIDE_LEFT_DURATION = 1000L
        private const val COUNTDOWN_DURATION = 2000L
        private const val COUNTDOWN_INTERVAL = 1000L
    }
}