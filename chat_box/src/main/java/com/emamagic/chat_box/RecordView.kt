package com.emamagic.chat_box

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.emamagic.chat_box.Const.DEFAULT_CANCEL_BOUNDS
import io.supercharge.shimmerlayout.ShimmerLayout
import java.io.IOException

class RecordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private lateinit var smallBlinkingMic: ImageView
    private lateinit var basketImg: ImageView
    private var counterTime: Chronometer? = null
    private lateinit var slideToCancel: TextView
    private lateinit var slideToCancelLayout: ShimmerLayout
    private lateinit var arrow: ImageView
    private var initialX = 0f
    private var basketInitialY = 0f
    private var difX = 0f
    private var cancelBounds = DEFAULT_CANCEL_BOUNDS.toFloat()
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var recordListener: OnRecordListener? = null
    private var recordPermissionHandler: RecordPermissionHandler? = null
    private var isSwiped = false
    private var isLessThanSecondAllowed: Boolean = false
    private var isSoundEnabled = true
    private var RECORD_START: Int = R.raw.record_start
    private var RECORD_FINISHED: Int = R.raw.record_finished
    private var RECORD_ERROR: Int = R.raw.record_error
    private var player: MediaPlayer? = null
    private var animationHelper: AnimationHelper? = null
    private var isRecordButtonGrowingAnimationEnabled = true
    private var shimmerEffectEnabled = true
    private var timeLimit: Long = -1
    private lateinit var runnable: Runnable
    private lateinit var recordButton: RecordButton
    private var mHandler: Handler? = null
    private var canRecord = true

    init {
        when {
            attrs == null -> { init(context, null, -1, -1) }
            defStyleAttr == 0 -> { init(context, attrs, -1, -1) }
            else -> { init(context, attrs, defStyleAttr, -1) }
        }
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val view = inflate(context, R.layout.record_view_layout, null)
        addView(view)
        val viewGroup = view.parent as ViewGroup
        viewGroup.clipChildren = false
        arrow = view.findViewById(R.id.arrow)
        slideToCancel = view.findViewById(R.id.slide_to_cancel)
        smallBlinkingMic = view.findViewById(R.id.glowing_mic)
        counterTime = view.findViewById(R.id.counter_tv)
        basketImg = view.findViewById(R.id.basket_img)
        slideToCancelLayout = view.findViewById(R.id.shimmer_layout)
        hideViews(true)
        if (attrs != null && defStyleAttr == -1 && defStyleRes == -1) {
            val typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.RecordView,
                defStyleAttr, defStyleRes
            )
            val slideArrowResource =
                typedArray.getResourceId(R.styleable.RecordView_slide_to_cancel_arrow, -1)
            val slideToCancelText =
                typedArray.getString(R.styleable.RecordView_slide_to_cancel_text)
            val slideMarginRight =
                typedArray.getDimension(R.styleable.RecordView_slide_to_cancel_margin_right, 30f)
                    .toInt()
            val counterTimeColor =
                typedArray.getColor(R.styleable.RecordView_counter_time_color, -1)
            val arrowColor =
                typedArray.getColor(R.styleable.RecordView_slide_to_cancel_arrow_color, -1)
            val cancelBounds =
                typedArray.getDimensionPixelSize(R.styleable.RecordView_slide_to_cancel_bounds, -1)
            if (cancelBounds != -1) setCancelBounds(
                cancelBounds.toFloat(),
                false
            ) //don't convert it to pixels since it's already in pixels
            if (slideArrowResource != -1) {
                val slideArrow = AppCompatResources.getDrawable(getContext(), slideArrowResource)
                arrow.setImageDrawable(slideArrow)
            }
            if (slideToCancelText != null) slideToCancel.text = slideToCancelText
            if (counterTimeColor != -1) setCounterTimeColor(counterTimeColor)
            if (arrowColor != -1) setSlideToCancelArrowColor(arrowColor)
            setMarginRight(slideMarginRight, true)
            typedArray.recycle()
        }
        animationHelper = AnimationHelper(
            context,
            basketImg,
            smallBlinkingMic,
            isRecordButtonGrowingAnimationEnabled
        )
    }

    private fun isTimeLimitValid(): Boolean {
        return timeLimit > 0
    }

    private fun initTimeLimitHandler() {
        mHandler = Handler()
        runnable = Runnable {
            if (recordListener != null && !isSwiped) recordListener?.onFinish(elapsedTime, true)
            removeTimeLimitCallbacks()
            animationHelper?.setStartRecorded(false)
            if (!isSwiped) playSound(RECORD_FINISHED)
            resetRecord(recordButton)
            isSwiped = true
        }
    }


    private fun hideViews(hideSmallMic: Boolean) {
        slideToCancelLayout.visibility = GONE
        counterTime?.visibility = GONE
        if (hideSmallMic) smallBlinkingMic.visibility = GONE
    }

    private fun showViews() {
        slideToCancelLayout.visibility = VISIBLE
        smallBlinkingMic.visibility = VISIBLE
        counterTime?.visibility = VISIBLE
    }


    private fun isLessThanOneSecond(time: Long): Boolean {
        return time <= 1000
    }


    private fun playSound(soundRes: Int) {
        if (isSoundEnabled) {
            if (soundRes == 0) return
            try {
                player = MediaPlayer()
                val afd = context?.resources?.openRawResourceFd(soundRes) ?: return
                player?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                player?.prepare()
                player?.start()
                player?.setOnCompletionListener { mp -> mp.release() }
                player?.isLooping = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    fun onActionDown(recordBtn: RecordButton, motionEvent: MotionEvent) {
        if (!isRecordPermissionGranted()) {
            return
        }
        recordButton = recordBtn
        if (recordListener != null) recordListener?.onStart()
        if (isTimeLimitValid()) {
            removeTimeLimitCallbacks()
            mHandler?.postDelayed(runnable, timeLimit)
        }
        animationHelper?.setStartRecorded(true)
        animationHelper?.resetBasketAnimation()
        animationHelper?.resetSmallMic()
        if (isRecordButtonGrowingAnimationEnabled) {
            recordBtn.startScale()
        }
        if (shimmerEffectEnabled) {
            slideToCancelLayout.startShimmerAnimation()
        }
        initialX = recordBtn.x
        basketInitialY = basketImg.y + 90
        playSound(RECORD_START)
        showViews()
        animationHelper?.animateSmallMicAlpha()
        counterTime?.base = SystemClock.elapsedRealtime()
        startTime = System.currentTimeMillis()
        counterTime?.start()
        isSwiped = false
    }


    fun onActionMove(recordBtn: RecordButton, motionEvent: MotionEvent) {
        if (!canRecord) {
            return
        }
        val time = System.currentTimeMillis() - startTime
        if (!isSwiped) {

            //Swipe To Cancel
            if (slideToCancelLayout.x != 0f && slideToCancelLayout.x <= counterTime?.right!! + cancelBounds) {

                //if the time was less than one second then do not start basket animation
                if (isLessThanOneSecond(time)) {
                    hideViews(true)
                    animationHelper?.clearAlphaAnimation(false)
                    animationHelper?.onAnimationEnd()
                } else {
                    hideViews(false)
                    animationHelper?.animateBasket(basketInitialY)
                }
                animationHelper?.moveRecordButtonAndSlideToCancelBack(
                    recordBtn,
                    slideToCancelLayout,
                    initialX,
                    difX
                )
                counterTime?.stop()
                if (shimmerEffectEnabled) {
                    slideToCancelLayout.stopShimmerAnimation()
                }
                isSwiped = true
                animationHelper?.setStartRecorded(false)
                if (recordListener != null) recordListener?.onCancel()
                if (isTimeLimitValid()) {
                    removeTimeLimitCallbacks()
                }
            } else {


                //if statement is to Prevent Swiping out of bounds
                if (motionEvent.rawX < initialX) {
                    recordBtn.animate()
                        .x(motionEvent.rawX)
                        .setDuration(0)
                        .start()
                    if (difX == 0f) difX = initialX - slideToCancelLayout.x
                    slideToCancelLayout.animate()
                        .x(motionEvent.rawX - difX)
                        .setDuration(0)
                        .start()
                }
            }
        }
    }

    fun onActionUp(recordBtn: RecordButton) {
        if (!canRecord) {
            return
        }
        elapsedTime = System.currentTimeMillis() - startTime
        if (!isLessThanSecondAllowed && isLessThanOneSecond(elapsedTime) && !isSwiped) {
            if (recordListener != null) recordListener?.onLessThanSecond()
            removeTimeLimitCallbacks()
            animationHelper?.setStartRecorded(false)
            playSound(RECORD_ERROR)
        } else {
            if (recordListener != null && !isSwiped) recordListener?.onFinish(elapsedTime, false)
            removeTimeLimitCallbacks()
            animationHelper?.setStartRecorded(false)
            if (!isSwiped) playSound(RECORD_FINISHED)
        }
        resetRecord(recordBtn)
    }

    private fun resetRecord(recordBtn: RecordButton) {
        //if user has swiped then do not hide SmallMic since it will be hidden after swipe Animation
        hideViews(!isSwiped)
        if (!isSwiped) animationHelper?.clearAlphaAnimation(true)
        animationHelper?.moveRecordButtonAndSlideToCancelBack(
            recordBtn,
            slideToCancelLayout,
            initialX,
            difX
        )
        counterTime?.stop()
        if (shimmerEffectEnabled) {
            slideToCancelLayout.stopShimmerAnimation()
        }
    }

    private fun removeTimeLimitCallbacks() {
        if (isTimeLimitValid()) {
            mHandler?.removeCallbacks(runnable)
        }
    }


    private fun isRecordPermissionGranted(): Boolean {
        if (recordPermissionHandler == null) {
            canRecord = true
        }
        canRecord = recordPermissionHandler?.isPermissionGranted() == true
        return canRecord
    }

    private fun setMarginRight(marginRight: Int, convertToDp: Boolean) {
        val layoutParams = slideToCancelLayout.layoutParams as LayoutParams
        if (convertToDp) {
            layoutParams.rightMargin = Utility.toPixel(marginRight.toFloat(), context).toInt()
        } else layoutParams.rightMargin = marginRight
        slideToCancelLayout.layoutParams = layoutParams
    }


    fun setOnRecordListener(recrodListener: OnRecordListener?) {
        recordListener = recrodListener
    }

    fun setRecordPermissionHandler(recordPermissionHandler: RecordPermissionHandler?) {
        this.recordPermissionHandler = recordPermissionHandler
    }

    fun setOnBasketAnimationEndListener(onBasketAnimationEndListener: OnBasketAnimationEnd) {
        animationHelper?.setOnBasketAnimationEndListener(onBasketAnimationEndListener)
    }

    fun setSoundEnabled(isEnabled: Boolean) {
        isSoundEnabled = isEnabled
    }

    fun setLessThanSecondAllowed(isAllowed: Boolean) {
        isLessThanSecondAllowed = isAllowed
    }

    fun setSlideToCancelText(text: String?) {
        slideToCancel.text = text
    }

    fun setSlideToCancelTextColor(color: Int) {
        slideToCancel.setTextColor(color)
    }

    fun setSmallMicColor(color: Int) {
        smallBlinkingMic.setColorFilter(color)
    }

    fun setSmallMicIcon(icon: Int) {
        smallBlinkingMic.setImageResource(icon)
    }

    fun setSlideMarginRight(marginRight: Int) {
        setMarginRight(marginRight, true)
    }


    fun setCustomSounds(startSound: Int, finishedSound: Int, errorSound: Int) {
        //0 means do not play sound
        RECORD_START = startSound
        RECORD_FINISHED = finishedSound
        RECORD_ERROR = errorSound
    }

    fun getCancelBounds(): Float {
        return cancelBounds
    }

    fun setCancelBounds(cancelBounds: Float) {
        setCancelBounds(cancelBounds, true)
    }

    //set Chronometer color
    fun setCounterTimeColor(color: Int) {
        counterTime?.setTextColor(color)
    }

    fun setSlideToCancelArrowColor(color: Int) {
        arrow.setColorFilter(color)
    }


    private fun setCancelBounds(cancelBounds: Float, convertDpToPixel: Boolean) {
        val bounds = if (convertDpToPixel) Utility.toPixel(cancelBounds, context) else cancelBounds
        this.cancelBounds = bounds
    }

    fun isRecordButtonGrowingAnimationEnabled(): Boolean {
        return isRecordButtonGrowingAnimationEnabled
    }

    fun setRecordButtonGrowingAnimationEnabled(recordButtonGrowingAnimationEnabled: Boolean) {
        isRecordButtonGrowingAnimationEnabled = recordButtonGrowingAnimationEnabled
        animationHelper?.setRecordButtonGrowingAnimationEnabled(recordButtonGrowingAnimationEnabled)
    }

    fun isShimmerEffectEnabled(): Boolean {
        return shimmerEffectEnabled
    }

    fun setShimmerEffectEnabled(shimmerEffectEnabled: Boolean) {
        this.shimmerEffectEnabled = shimmerEffectEnabled
    }

    fun getTimeLimit(): Long {
        return timeLimit
    }

    fun setTimeLimit(timeLimit: Long) {
        this.timeLimit = timeLimit
        if (mHandler != null) {
            removeTimeLimitCallbacks()
        }
        initTimeLimitHandler()
    }

    fun setTrashIconColor(color: Int) {
        animationHelper?.setTrashIconColor(color)
    }

}