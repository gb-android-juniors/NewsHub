package com.example.newsgb._core.ui

import android.animation.Animator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.example.newsgb.R

@SuppressLint("CustomSplashScreen")
class CustomSplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_splash_screen)
        setCustomSplashScreenAnimation()
    }

    private fun setCustomSplashScreenAnimation() {
        findViewById<AppCompatImageView>(R.id.splash_image_view).animate()
            .alpha(1f)
            .setInterpolator(LinearInterpolator())
            .setDuration(SPLASHSCREEN_ANIMATION_DURATION)
            .setListener(object : Animator.AnimatorListener {

                override fun onAnimationEnd(animation: Animator?) {
                    finish()
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
    }

    companion object {
        const val SPLASHSCREEN_ANIMATION_DURATION = 2000L
    }
}