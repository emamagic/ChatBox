package com.emamagic.chat_box

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat

class AnimationHelper(
    private val context: Context,
    private val basketImg: ImageView,
    private val smallBlinkingMic: ImageView,
    private val recordButtonGrowingAnimationEnabled: Boolean
) {
    private lateinit var animatedVectorDrawable: AnimatedVectorDrawableCompat
    private lateinit var alphaAnimation: AlphaAnimation
    private lateinit var onBasketAnimationEndListener: OnBasketAnimationEnd
    private var isBasketAnimating = false
    private var isStartRecorded: Boolean = false
    private var micX = 0f
    private var micY: Float = 0f
    private lateinit var micAnimation: AnimatorSet
    private lateinit var translateAnimation1: TranslateAnimation
    private lateinit var translateAnimation2: TranslateAnimation
    private lateinit var handler1: Handler
    private lateinit var handler2: Handler


    init {
        animatedVectorDrawable =
            AnimatedVectorDrawableCompat.create(context, R.drawable.recv_basket_animated)
    }

    fun setTrashIconColor(color: Int) {
        animatedVectorDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setRecordButtonGrowingAnimationEnabled(recordButtonGrowingAnimationEnabled: Boolean) {
        this.recordButtonGrowingAnimationEnabled = recordButtonGrowingAnimationEnabled
    }

    @SuppressLint("RestrictedApi")
    fun animateBasket(basketInitialY: Float) {
        isBasketAnimating = true
        clearAlphaAnimation(false)

        //save initial x,y values for mic icon
        if (micX == 0f) {
            micX = smallBlinkingMic.x
            micY = smallBlinkingMic.y
        }
        micAnimation = AnimatorInflaterCompat.loadAnimator(
            context,
            R.animator.delete_mic_animation
        ) as AnimatorSet
        micAnimation.setTarget(smallBlinkingMic) // set the view you want to animate
        translateAnimation1 = TranslateAnimation(0f, 0f, basketInitialY, basketInitialY - 90)
        translateAnimation1.duration = 250
        translateAnimation2 = TranslateAnimation(0f, 0f, basketInitialY - 130, basketInitialY)
        translateAnimation2.duration = 350
        micAnimation.start()
        basketImg.setImageDrawable(animatedVectorDrawable)
        handler1 = Handler()
        handler1.postDelayed({
            basketImg.visibility = View.VISIBLE
            basketImg.startAnimation(translateAnimation1)
        }, 350)
        translateAnimation1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                animatedVectorDrawable.start()
                handler2 = Handler()
                handler2.postDelayed({
                    basketImg.startAnimation(translateAnimation2)
                    smallBlinkingMic.visibility = View.INVISIBLE
                    basketImg.visibility = View.INVISIBLE
                }, 450)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        translateAnimation2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                basketImg.visibility = View.INVISIBLE
                isBasketAnimating = false

                //if the user pressed the record button while the animation is running
                // then do NOT call on Animation end
                if (!isStartRecorded) {
                    onBasketAnimationEndListener.onAnimationEnd()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }


    //if the user started a new Record while the Animation is running
    // then we want to stop the current animation and revert views back to default state
    fun resetBasketAnimation() {
        if (isBasketAnimating) {
            translateAnimation1.reset()
            translateAnimation1.cancel()
            translateAnimation2.reset()
            translateAnimation2.cancel()
            micAnimation.cancel()
            smallBlinkingMic.clearAnimation()
            basketImg.clearAnimation()
            handler1.removeCallbacksAndMessages(null)
            handler2.removeCallbacksAndMessages(null)
            basketImg.visibility = View.INVISIBLE
            smallBlinkingMic.x = micX
            smallBlinkingMic.y = micY
            smallBlinkingMic.visibility = View.GONE
            isBasketAnimating = false
        }
    }


    fun clearAlphaAnimation(hideView: Boolean) {
        alphaAnimation.cancel()
        alphaAnimation.reset()
        smallBlinkingMic.clearAnimation()
        if (hideView) {
            smallBlinkingMic.visibility = View.GONE
        }
    }

    fun animateSmallMicAlpha() {
        alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 500
        alphaAnimation.repeatMode = Animation.REVERSE
        alphaAnimation.repeatCount = Animation.INFINITE
        smallBlinkingMic.startAnimation(alphaAnimation)
    }

    fun moveRecordButtonAndSlideToCancelBack(
        recordBtn: RecordButton,
        slideToCancelLayout: FrameLayout,
        initialX: Float,
        difX: Float
    ) {
        val positionAnimator = ValueAnimator.ofFloat(recordBtn.x, initialX)
        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        positionAnimator.addUpdateListener { animation ->
            val x = animation.animatedValue as Float
            recordBtn.x = x
        }
        if (recordButtonGrowingAnimationEnabled) {
            recordBtn.stopScale()
        }
        positionAnimator.duration = 0
        positionAnimator.start()


        // if the move event was not called ,then the difX will still 0 and there is no need to move it back
        if (difX != 0f) {
            val x = initialX - difX
            slideToCancelLayout.animate()
                .x(x)
                .setDuration(0)
                .start()
        }
    }

    fun resetSmallMic() {
        smallBlinkingMic.alpha = 1.0f
        smallBlinkingMic.scaleX = 1.0f
        smallBlinkingMic.scaleY = 1.0f
    }

    fun setOnBasketAnimationEndListener(onBasketAnimationEndListener: OnBasketAnimationEnd) {
        this.onBasketAnimationEndListener = onBasketAnimationEndListener
    }

    fun onAnimationEnd() {
        onBasketAnimationEndListener.onAnimationEnd()
    }

    //check if the user started a new Record by pressing the RecordButton
    fun setStartRecorded(startRecorded: Boolean) {
        isStartRecorded = startRecorded
    }

}