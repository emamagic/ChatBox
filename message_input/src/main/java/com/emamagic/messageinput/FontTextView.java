package com.emamagic.messageinput;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.emamagic.chatbox.ViewHelper;

public class FontTextView extends AppCompatTextView {

    /**
     * custom gravity for handle both rtl and ltr based on FontEditText gravity attr
     */
    public static final int START = 1;
    public static final int END = 2;
    public static final int START_CENTER_VERTICAL = 3;
    public static final int END_CENTER_VERTICAL = 4;

    public FontTextView(Context context) {
        this(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("SetTextI18n")
    public void setText(CharSequence text, BufferType type) {
        try {
            super.setText(text, type);
        } catch (Exception e) {
            setText("");
            //Logger.exception(e);
            //TODO: log this exception
        }
    }

    @SuppressLint("RtlHardcoded")
    private void init(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray tp = context.obtainStyledAttributes(attributeSet, R.styleable.FontTextView);
            try {
                int color = tp.getColor(R.styleable.FontTextView_drawableColor, 0);
                int gravity = tp.getColor(R.styleable.FontTextView_text_gravity, -1);
                tintDrawables(color);
                setResponsiveGravity(gravity);
            } finally {
                tp.recycle();
            }
        }
        if (isInEditMode()) {
            return;
        }
        setFreezesText(true);
        TypeFaceHelper.applyTypeface(this);
    }

    public void tintDrawables(@ColorInt int color) {
        if (color != 0) {
            Drawable[] drawables = getCompoundDrawablesRelative();
            for (Drawable drawable : drawables) {
                if (drawable == null) continue;
                ViewHelper.tintDrawable(drawable, color);
            }
        }
    }

    private void setResponsiveGravity(int gravity) {
        if (gravity == START) {
            setGravity(ViewHelper.isRTL() ? Gravity.RIGHT : Gravity.LEFT);
        } else if (gravity == END) {
            setGravity(ViewHelper.isRTL() ? Gravity.LEFT : Gravity.RIGHT);
        } else if (gravity == START_CENTER_VERTICAL) {
            setGravity((ViewHelper.isRTL() ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        } else if (gravity == END_CENTER_VERTICAL) {
            setGravity((ViewHelper.isRTL() ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL);
        }
    }

    public void setEventsIcon(@DrawableRes int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), drawableRes);
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, width / 2, height / 2);
        ScaleDrawable sd = new ScaleDrawable(drawable, Gravity.CENTER, 0.6f, 0.6f);
        sd.setLevel(8000);
        ViewHelper.tintDrawable(drawable, ViewHelper.getTertiaryTextColor(getContext()));
        setCompoundDrawablesWithIntrinsicBounds(sd, null, null, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Object text = this.getText();
        if (text instanceof Spanned) {
            Spanned buffer = (Spanned) text;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= this.getTotalPaddingLeft();
                y -= this.getTotalPaddingTop();

                x += this.getScrollX();
                y += this.getScrollY();

                Layout layout = this.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off,
                        ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(this);
                    }
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }
}
