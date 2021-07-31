package com.emamagic.messageinput;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import im.limoo.emoji.view.EmojiEditText;

public class FontEditText extends EmojiEditText {

    private KeyPreImeListener keyPreImeListener;

    public FontEditText(Context context) {
        super(context);
        init();
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyPreImeListener != null
                && keyCode == KeyEvent.KEYCODE_BACK) {
            return keyPreImeListener.onHideKeyboard();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setKeyPreImeListener(KeyPreImeListener keyPreImeListener) {
        this.keyPreImeListener = keyPreImeListener;
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        setInputType(getInputType() | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        setImeOptions(getImeOptions() | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        TypeFaceHelper.applyTypeface(this);
    }

    @SuppressLint("SetTextI18n")
    public void setText(CharSequence text, BufferType type) {
        try {
            super.setText(text, type);
        } catch (Exception e) {
            setText("I tried, but your OEM just sucks because they modify the framework components and therefore causing the app to crash!" + ".\nFastHub");
           // Logger.exception(e);
            //TODO: log this exception
        }
    }

    public interface KeyPreImeListener {

        /**
         * Fires when KEYCODE_BACK key pressed
         */
        boolean onHideKeyboard();
    }
}
