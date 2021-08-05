package com.emamagic.chat_box

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat

class CircleColorImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    fun setCircleColor(circleColor: Int) {
        val circle = ContextCompat.getDrawable(context, R.drawable.circle_tintable)
        circle?.setColorFilter(circleColor, PorterDuff.Mode.SRC_IN)
        background = circle
    }

    init {
        var circleColor = Color.WHITE
        if (attrs != null) {
            val typedArray =
                context.theme.obtainStyledAttributes(attrs, R.styleable.CircleColorImageView, 0, 0)
            circleColor =
                typedArray.getColor(R.styleable.CircleColorImageView_circleColor, Color.WHITE)
            val tintColor =
                typedArray.getColor(R.styleable.CircleColorImageView_foregroundImageTintColor, 0)
            if (tintColor != 0) setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
            typedArray.recycle()
        }
        val circle = ContextCompat.getDrawable(context, R.drawable.circle_tintable)
        circle?.setColorFilter(circleColor, PorterDuff.Mode.SRC_IN)
        background = circle
    }
}