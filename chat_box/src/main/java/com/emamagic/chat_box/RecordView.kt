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
import io.supercharge.shimmerlayout.ShimmerLayout
import java.io.IOException

class RecordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    val DEFAULT_CANCEL_BOUNDS = 8 //8dp

    private var smallBlinkingMic: ImageView? = null
    private  var basketImg:android.widget.ImageView? = null
    private var counterTime: Chronometer? = null
    private var slideToCancel: TextView? = null
    private var slideToCancelLayout: ShimmerLayout? = null
    private var arrow: ImageView? = null
    private var initialX =
        0f, private  var basketInitialY:kotlin.Float = 0f, private  var difX:kotlin.Float = 0f
    private var cancelBounds = DEFAULT_CANCEL_BOUNDS.toFloat()
    private var startTime: Long = 0, private  var elapsedTime:kotlin.Long = 0
    private var context: Context? = null
    private var recordListener: OnRecordListener? = null
    private var recordPermissionHandler: RecordPermissionHandler? = null
    private var isSwiped = false, private  var isLessThanSecondAllowed:kotlin.Boolean = false
    private var isSoundEnabled = true
    private var RECORD_START: Int = R.raw.record_start
    private var RECORD_FINISHED: Int = R.raw.record_finished
    private var RECORD_ERROR: Int = R.raw.record_error
    private var player: MediaPlayer? = null
    private var animationHelper: AnimationHelper? = null
    private var isRecordButtonGrowingAnimationEnabled = true
    private var shimmerEffectEnabled = true
    private var timeLimit: Long = -1
    private var runnable: Runnable? = null
    private var handler: Handler? = null
    private var recordButton: com.emamagic.record_view.RecordButton? = null

    private var canRecord = true

    fun RecordView(context: Context) {
        super(context)
        this.context = context
        init(context, null, -1, -1)
    }


    fun RecordView(context: Context, attrs: AttributeSet?) {
        super(context, attrs)
        this.context = context
        init(context, attrs, -1, -1)
    }

    fun RecordView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        super(context, attrs, defStyleAttr)
        this.context = context
        init(context, attrs, defStyleAttr, -1)
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
        basketImg = view.findViewById<ImageView>(R.id.basket_img)
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
            if (slideToCancelText != null) slideToCancel.setText(slideToCancelText)
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
        handler = Handler()
        runnable = Runnable {
            if (recordListener != null && !isSwiped) recordListener.onFinish(elapsedTime, true)
            removeTimeLimitCallbacks()
            animationHelper.setStartRecorded(false)
            if (!isSwiped) playSound(RECORD_FINISHED)
            if (recordButton != null) {
                resetRecord(recordButton)
            }
            isSwiped = true
        }
    }


    private fun hideViews(hideSmallMic: Boolean) {
        slideToCancelLayout.setVisibility(GONE)
        counterTime!!.visibility = GONE
        if (hideSmallMic) smallBlinkingMic!!.visibility = GONE
    }

    private fun showViews() {
        slideToCancelLayout.setVisibility(VISIBLE)
        smallBlinkingMic!!.visibility = VISIBLE
        counterTime!!.visibility = VISIBLE
    }


    private fun isLessThanOneSecond(time: Long): Boolean {
        return time <= 1000
    }


    private fun playSound(soundRes: Int) {
        if (isSoundEnabled) {
            if (soundRes == 0) return
            try {
                player = MediaPlayer()
                val afd = context!!.resources.openRawResourceFd(soundRes) ?: return
                player!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                player!!.prepare()
                player!!.start()
                player!!.setOnCompletionListener { mp -> mp.release() }
                player!!.isLooping = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    fun onActionDown(recordBtn: com.emamagic.record_view.RecordButton, motionEvent: MotionEvent?) {
        if (!isRecordPermissionGranted()) {
            return
        }
        recordButton = recordBtn
        if (recordListener != null) recordListener.onStart()
        if (isTimeLimitValid()) {
            removeTimeLimitCallbacks()
            handler!!.postDelayed(runnable!!, timeLimit)
        }
        animationHelper.setStartRecorded(true)
        animationHelper.resetBasketAnimation()
        animationHelper.resetSmallMic()
        if (isRecordButtonGrowingAnimationEnabled) {
            recordBtn.startScale()
        }
        if (shimmerEffectEnabled) {
            slideToCancelLayout.startShimmerAnimation()
        }
        initialX = recordBtn.getX()
        basketInitialY = basketImg.getY() + 90
        playSound(RECORD_START)
        showViews()
        animationHelper.animateSmallMicAlpha()
        counterTime!!.base = SystemClock.elapsedRealtime()
        startTime = System.currentTimeMillis()
        counterTime!!.start()
        isSwiped = false
    }


    fun onActionMove(recordBtn: com.emamagic.record_view.RecordButton, motionEvent: MotionEvent) {
        if (!canRecord) {
            return
        }
        val time = System.currentTimeMillis() - startTime
        if (!isSwiped) {

            //Swipe To Cancel
            if (slideToCancelLayout.getX() != 0f && slideToCancelLayout.getX() <= counterTime!!.right + cancelBounds) {

                //if the time was less than one second then do not start basket animation
                if (isLessThanOneSecond(time)) {
                    hideViews(true)
                    animationHelper.clearAlphaAnimation(false)
                    animationHelper.onAnimationEnd()
                } else {
                    hideViews(false)
                    animationHelper.animateBasket(basketInitialY)
                }
                animationHelper.moveRecordButtonAndSlideToCancelBack(
                    recordBtn,
                    slideToCancelLayout,
                    initialX,
                    difX
                )
                counterTime!!.stop()
                if (shimmerEffectEnabled) {
                    slideToCancelLayout.stopShimmerAnimation()
                }
                isSwiped = true
                animationHelper.setStartRecorded(false)
                if (recordListener != null) recordListener.onCancel()
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
                    if (difX == 0f) difX = initialX - slideToCancelLayout.getX()
                    slideToCancelLayout.animate()
                        .x(motionEvent.rawX - difX)
                        .setDuration(0)
                        .start()
                }
            }
        }
    }

    fun onActionUp(recordBtn: com.emamagic.record_view.RecordButton?) {
        if (!canRecord) {
            return
        }
        elapsedTime = System.currentTimeMillis() - startTime
        if (!isLessThanSecondAllowed && isLessThanOneSecond(elapsedTime) && !isSwiped) {
            if (recordListener != null) recordListener.onLessThanSecond()
            removeTimeLimitCallbacks()
            animationHelper.setStartRecorded(false)
            playSound(RECORD_ERROR)
        } else {
            if (recordListener != null && !isSwiped) recordListener.onFinish(elapsedTime, false)
            removeTimeLimitCallbacks()
            animationHelper.setStartRecorded(false)
            if (!isSwiped) playSound(RECORD_FINISHED)
        }
        resetRecord(recordBtn)
    }

    private fun resetRecord(recordBtn: com.emamagic.record_view.RecordButton?) {
        //if user has swiped then do not hide SmallMic since it will be hidden after swipe Animation
        hideViews(!isSwiped)
        if (!isSwiped) animationHelper.clearAlphaAnimation(true)
        animationHelper.moveRecordButtonAndSlideToCancelBack(
            recordBtn,
            slideToCancelLayout,
            initialX,
            difX
        )
        counterTime!!.stop()
        if (shimmerEffectEnabled) {
            slideToCancelLayout.stopShimmerAnimation()
        }
    }

    private fun removeTimeLimitCallbacks() {
        if (isTimeLimitValid()) {
            handler!!.removeCallbacks(runnable!!)
        }
    }


    private fun isRecordPermissionGranted(): Boolean {
        if (recordPermissionHandler == null) {
            canRecord = true
        }
        canRecord = recordPermissionHandler.isPermissionGranted()
        return canRecord
    }

    private fun setMarginRight(marginRight: Int, convertToDp: Boolean) {
        val layoutParams = slideToCancelLayout.getLayoutParams() as LayoutParams
        if (convertToDp) {
            layoutParams.rightMargin = DpUtil.toPixel(marginRight.toFloat(), context)
        } else layoutParams.rightMargin = marginRight
        slideToCancelLayout.setLayoutParams(layoutParams)
    }


    fun setOnRecordListener(recrodListener: OnRecordListener?) {
        recordListener = recrodListener
    }

    fun setRecordPermissionHandler(recordPermissionHandler: RecordPermissionHandler?) {
        this.recordPermissionHandler = recordPermissionHandler
    }

    fun setOnBasketAnimationEndListener(onBasketAnimationEndListener: OnBasketAnimationEnd?) {
        animationHelper.setOnBasketAnimationEndListener(onBasketAnimationEndListener)
    }

    fun setSoundEnabled(isEnabled: Boolean) {
        isSoundEnabled = isEnabled
    }

    fun setLessThanSecondAllowed(isAllowed: Boolean) {
        isLessThanSecondAllowed = isAllowed
    }

    fun setSlideToCancelText(text: String?) {
        slideToCancel!!.text = text
    }

    fun setSlideToCancelTextColor(color: Int) {
        slideToCancel!!.setTextColor(color)
    }

    fun setSmallMicColor(color: Int) {
        smallBlinkingMic!!.setColorFilter(color)
    }

    fun setSmallMicIcon(icon: Int) {
        smallBlinkingMic!!.setImageResource(icon)
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
        counterTime!!.setTextColor(color)
    }

    fun setSlideToCancelArrowColor(color: Int) {
        arrow!!.setColorFilter(color)
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
        animationHelper.setRecordButtonGrowingAnimationEnabled(recordButtonGrowingAnimationEnabled)
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
        if (handler != null && runnable != null) {
            removeTimeLimitCallbacks()
        }
        initTimeLimitHandler()
    }

    fun setTrashIconColor(color: Int) {
        animationHelper.setTrashIconColor(color)
    }

}