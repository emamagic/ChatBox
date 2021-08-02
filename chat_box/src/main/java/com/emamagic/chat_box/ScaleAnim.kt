package com.emamagic.chat_box

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator

class ScaleAnim (private val view: View?) {

    @SuppressLint("ObjectAnimatorBinding")
    fun start() {
        val set = AnimatorSet()
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 2.0f)
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 2.0f)
        set.duration = 150
        set.interpolator = AccelerateDecelerateInterpolator()
        set.playTogether(scaleY, scaleX)
        set.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun stop() {
        val set = AnimatorSet()
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f)
        scaleY.duration = 250
        scaleY.interpolator = DecelerateInterpolator()
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f)
        scaleX.duration = 250
        scaleX.interpolator = DecelerateInterpolator()
        set.duration = 150
        set.interpolator = AccelerateDecelerateInterpolator()
        set.playTogether(scaleY, scaleX)
        set.start()
    }
}