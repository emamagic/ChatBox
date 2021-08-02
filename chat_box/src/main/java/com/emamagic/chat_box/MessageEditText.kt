package com.emamagic.chat_box

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.emamagic.emoji.view.EmojiMultiAutoCompleteTextView
import java.lang.Exception

class MessageEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : EmojiMultiAutoCompleteTextView(context, attrs) {


    init { init() }

    private var keyPreImeListener: KeyPreImeListener? = null

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyPreImeListener != null
            && keyCode == KeyEvent.KEYCODE_BACK
        ) {
            keyPreImeListener!!.onHideKeyboard()
        } else super.onKeyPreIme(keyCode, event)
    }

    fun setKeyPreImeListener(keyPreImeListener: KeyPreImeListener?) {
        this.keyPreImeListener = keyPreImeListener
    }

    private fun init() {
        if (isInEditMode) {
            return
        }
        inputType =
            inputType or EditorInfo.IME_FLAG_NO_EXTRACT_UI or EditorInfo.IME_FLAG_NO_FULLSCREEN
        imeOptions = imeOptions or EditorInfo.IME_FLAG_NO_FULLSCREEN
    }

    @SuppressLint("SetTextI18n")
    override fun setText(text: CharSequence?, type: BufferType?) {
        try {
            super.setText(text, type)
        } catch (e: Exception) {
            setText(
                """
                I tried, but your OEM just sucks because they modify the framework components and therefore causing the app to crash!.
                FastHub
                """.trimIndent()
            )
        }
    }

    interface KeyPreImeListener {
        fun onHideKeyboard(): Boolean
    }
}