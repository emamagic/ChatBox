package com.emamagic.chat_box

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import java.lang.Exception

object Utility {

    @JvmStatic
    fun setCursor(drawable: Drawable?, messageInput: MessageEditText) {
        if (drawable == null) return
        try {
            @SuppressLint("SoonBlockedPrivateApi") val drawableResField =
                TextView::class.java.getDeclaredField("mCursorDrawableRes")
            drawableResField.isAccessible = true
            val drawableFieldOwner: Any
            val drawableFieldClass: Class<*>
            val editorField = TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            drawableFieldOwner = editorField[messageInput]
            drawableFieldClass = drawableFieldOwner.javaClass
            val drawableField = drawableFieldClass.getDeclaredField("mCursorDrawable")
            drawableField.isAccessible = true
            drawableField[drawableFieldOwner] =
                arrayOf(drawable, drawable)
        } catch (ignored: Exception) {
        }
    }

    @JvmStatic
    fun hideKeyboard(view: View) {
        val inputManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @JvmStatic
    fun toPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    @JvmStatic
    fun toDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

}