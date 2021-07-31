package com.emamagic.message_input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ReplyBar extends RelativeLayout {

    private ImageView closeBtn;
    private FontTextView title;
    private FontTextView summery;

    public ReplyBar(Context context) {
        super(context);
        init();
    }

    public ReplyBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReplyBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
        inflate(getContext(), R.layout.reply_header_layout, this);
        ViewGroup.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewHelper.dpToPx(getContext(),48)
        );
        setLayoutParams(params);

        closeBtn = findViewById(R.id.close);
        title = findViewById(R.id.title);
        summery = findViewById(R.id.summery);
    }

    public void setText(String text) {
        summery.setText(text);
    }

    public void enterEditMode() {
        title.setText(R.string.edit_message);
    }

    public void enterReplyMode(String userName) {
        title.setText(userName);
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        closeBtn.setOnClickListener(e -> onCloseListener.onReplyBarDismiss());
    }

    public interface OnCloseListener {
        void onReplyBarDismiss();
    }
}
