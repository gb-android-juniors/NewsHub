package com.example.newsgb._core.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnEnd
import com.example.newsgb.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSplashScreen()
        setCustomSplashScreen()
    }

    /** Метод для отображения SplashScreen. Если версия андроид ниже VERSION.S - применяем кастомный сплэш экран, если версия выше - стандартный сплэш*/
    private fun setSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setDefaultSplashScreen()
        } else {
            setCustomSplashScreen()
        }
    }

    private fun setCustomSplashScreen() {
        setContentView(R.layout.activity_splash_screen)


        findViewById<AppCompatImageView>(R.id.splash_image_view).animate()
            .rotationBy(250f)
            .setInterpolator(LinearInterpolator())
            .setDuration(3000)
            .setListener(object : Animator.AnimatorListener {

                override fun onAnimationEnd(animation: Animator?) {
                    startMainActivity()
                    finish()
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setDefaultSplashScreen() {
        setSplashScreenHideAnimation()
        setSplashScreenDuration()
        startMainActivity()
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

        object : CountDownTimer(COUNTDOWN_DURATION, COUNTDOWN_INTERVAL) {
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

    private fun startMainActivity() {
        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
    }

    companion object {
        private const val SLIDE_LEFT_DURATION = 1000L
        private const val COUNTDOWN_DURATION = 2000L
        private const val COUNTDOWN_INTERVAL = 1000L
    }
}