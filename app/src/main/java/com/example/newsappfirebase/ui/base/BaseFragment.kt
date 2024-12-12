package com.example.newsappfirebase.ui.base

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView

open class BaseFragment: Fragment() {
    protected var lottieLoading: LottieAnimationView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    protected fun showLoadingOnce(lottieLoading: LottieAnimationView, speed: Float = 2.5f) {
        lottieLoading.speed = speed
        lottieLoading.visibility = View.VISIBLE
        lottieLoading.playAnimation()

        lottieLoading.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                lottieLoading.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
                lottieLoading.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
    }


}