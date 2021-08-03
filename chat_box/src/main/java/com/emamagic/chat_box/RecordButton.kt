package com.emamagic.chat_box

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView

class RecordButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), View.OnTouchListener, View.OnClickListener {

    private lateinit var scaleAnim: ScaleAnim
    private lateinit var recordView: RecordView
    private lateinit var onRecordClickListener: OnRecordClickListener
    private var listenForRecord = true

    init { init() }

    fun setRecordView(recordView: RecordView) {
        this.recordView = recordView
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        scaleAnim = ScaleAnim(this)
        this.setOnTouchListener(this)
        this.setOnClickListener(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setClip(this)
    }

    private fun setClip(v: View) {
        if (v.parent == null) {
            return
        }
        if (v is ViewGroup) {
            v.clipChildren = false
            v.clipToPadding = false
        }
        if (v.parent is View) {
            setClip(v.parent as View)
        }
    }


    fun setTheImageResource(imageResource: Int) {
        val image = AppCompatResources.getDrawable(context, imageResource)
        setImageDrawable(image)
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (isListenForRecord()) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> recordView.onActionDown(
                    v as RecordButton,
                    event
                )
                MotionEvent.ACTION_MOVE -> recordView.onActionMove(
                    v as RecordButton,
                    event
                )
                MotionEvent.ACTION_UP -> recordView.onActionUp(v as RecordButton)
            }
        }
        return isListenForRecord()
    }


    fun startScale() {
        scaleAnim.start()
    }

    fun stopScale() {
        scaleAnim.stop()
    }

    fun setListenForRecord(listenForRecord: Boolean) {
        this.listenForRecord = listenForRecord
    }

    fun isListenForRecord(): Boolean {
        return listenForRecord
    }

    fun setOnRecordClickListener(onRecordClickListener: OnRecordClickListener) {
        this.onRecordClickListener = onRecordClickListener
    }


    override fun onClick(v: View) {
        onRecordClickListener.onRecordClick(v)
    }
}