package com.emamagic.chat_box

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

abstract class Style protected constructor(private var context: Context, attrs: AttributeSet) {

    private var resources: Resources = context.resources

    protected fun getDimension(@DimenRes dimen: Int): Int {
        return resources.getDimensionPixelSize(dimen)
    }

    protected fun getColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    protected fun getDrawable(@DrawableRes drawable: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawable)
    }

    protected fun getSelector(
        @ColorInt normalColor: Int, @ColorInt pressedColor: Int,
        @ColorInt disabledColor: Int, @DrawableRes shape: Int
    ): Drawable {
        val drawable = DrawableCompat.wrap(
            getVectorDrawable(shape)!!
        ).mutate()
        DrawableCompat.setTintList(
            drawable,
            ColorStateList(
                arrayOf(
                    intArrayOf(
                        R.attr.state_enabled,
                        -R.attr.state_pressed
                    ),
                    intArrayOf(R.attr.state_enabled, R.attr.state_pressed),
                    intArrayOf(-R.attr.state_enabled)
                ), intArrayOf(normalColor, pressedColor, disabledColor)
            )
        )
        return drawable
    }

    private fun getVectorDrawable(@DrawableRes drawable: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawable)
    }



}