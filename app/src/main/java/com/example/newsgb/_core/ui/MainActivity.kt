package com.example.newsgb._core.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.newsgb.R
import com.example.newsgb._core.data.AppState
import com.example.newsgb._core.viewmodel.BaseViewModel
import com.example.newsgb.databinding.MainActivityBinding
import com.example.newsgb.utils.network.OnlineLiveData
import com.example.newsgb.utils.ui.AlertDialogFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private var isNetworkAvailable: Boolean = true

    private val model: BaseViewModel<AppState>
        get() = TODO("заменить BaseViewModel на MainViewModel")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setDefaultSplashScreen()
        } else {
            setCustomSplashScreen()
        }

        subscribeToNetworkChange(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!isNetworkAvailable && isDialogNull()) {
            showNoInternetConnectionDialog()
        }
    }

    private fun subscribeToNetworkChange(savedInstanceState: Bundle?) {
        OnlineLiveData(this).observe(this@MainActivity) {
            isNetworkAvailable = it
            if (isNetworkAvailable) {
                startNewsFragment(savedInstanceState)
            } else {
                binding.mainActivityText.text = getString(R.string.dialog_message_device_is_offline)

                Toast.makeText(
                    this,
                    R.string.dialog_message_device_is_offline,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startNewsFragment(savedInstanceState: Bundle?) {
//        if (savedInstanceState == null) {
//        binding.mainActivityText.visibility = View.GONE
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container, NewsFragment.newInstance()) //TODO переделать в зависимости от реализации фрагмента
//                .commitNow()
//        }
    }


    private fun isDialogNull(): Boolean =
        supportFragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) == null

    private fun showNoInternetConnectionDialog() {
        showAlertDialog(
            getString(R.string.dialog_title_device_is_offline),
            getString(R.string.dialog_message_device_is_offline)
        )
    }

    private fun showAlertDialog(title: String?, message: String?) =
        AlertDialogFragment.newInstance(title, message)
            .show(supportFragmentManager, DIALOG_FRAGMENT_TAG)

    private fun setCustomSplashScreen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            startActivity(Intent(this@MainActivity, CustomSplashScreenActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setDefaultSplashScreen() {
        setSplashScreenHideAnimation()
        setSplashScreenDuration()
    }

    /** Прописываем анимацию */
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

    /** настройка времени отображения splash screen*/
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