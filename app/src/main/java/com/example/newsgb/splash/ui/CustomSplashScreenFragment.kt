package com.example.newsgb.splash.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.newsgb.R
import com.example.newsgb._core.ui.BaseFragment
import com.example.newsgb.databinding.CustomSplashScreenFragmentBinding
import com.example.newsgb.main.ui.NavigationFragment

@SuppressLint("CustomSplashScreen")
class CustomSplashScreenFragment : BaseFragment<CustomSplashScreenFragmentBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomSplashScreenAnimation()
    }

    private fun setCustomSplashScreenAnimation() {
        binding.splashImageView.animate()
            .alpha(1f)
            .setInterpolator(LinearInterpolator())
            .setDuration(SPLASHSCREEN_ANIMATION_DURATION)
            .setListener(object : Animator.AnimatorListener {

                override fun onAnimationEnd(animation: Animator?) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(this@CustomSplashScreenFragment)
                        .add(R.id.main_container, NavigationFragment.newInstance())
                        .commit()
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
    }

    companion object {
        const val SPLASHSCREEN_ANIMATION_DURATION = 2000L
        @JvmStatic
        fun newInstance() = CustomSplashScreenFragment()

    }

    override fun getViewBinding() = CustomSplashScreenFragmentBinding.inflate(layoutInflater)
}