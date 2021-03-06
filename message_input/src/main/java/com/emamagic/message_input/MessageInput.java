package com.emamagic.message_input;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.emamagic.record_view.AudioRecorder;
import com.emamagic.record_view.OnRecordClickListener;
import com.emamagic.record_view.OnRecordListener;
import com.emamagic.record_view.RecordButton;
import com.emamagic.record_view.RecordPermissionHandler;
import com.emamagic.record_view.RecordView;
import com.emamagic.record_view.ScaleAnim;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import com.emamagic.emoji.EmojIconActions;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

@SuppressWarnings({"WeakerAccess", "unused"})
public class MessageInput extends LinearLayout implements TextWatcher, View.OnFocusChangeListener,
        MessageEditText.KeyPreImeListener, View.OnTouchListener, RecordPermissionHandler, OnRecordListener {

    protected MessageEditText messageInput;
    protected ImageButton messageSendButton;
    protected ImageButton emojiButton;
    protected ImageButton attachmentButton;
    private TextView disableTextView;

    private Activity activity;
    private ScaleAnim scaleAnim;
    private RecordView recordView;
    private OnRecordClickListener onRecordClickListener;
    private AudioRecorder audioRecorder;
    private File recordFile;
    private VoiceListener voiceListener;

    private CharSequence input;
    private InputListener inputListener;
    private AttachmentsListener attachmentsListener;
    private boolean isTyping;
    private TypingListener typingListener;
    private int delayTypingStatusMillis;
    private int delaySavingDraftInMillis;
    private Runnable typingTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTyping) {
                isTyping = false;
                if (typingListener != null) typingListener.onStopTyping();
            }
        }
    };
    private boolean lastFocus;
    private boolean showAttachmentButton;
    private int keyboardHeight;
    private MessageFull messageToBeReturned;
    private MessageInputStyle messageInputStyle;
    private boolean canSubmit = true;
    private boolean isSaveDraftInQueue = false;
    private Runnable draftSaverRunnable = new Runnable() {
        @Override
        public void run() {
            isSaveDraftInQueue = false;
            if (inputListener != null)
                inputListener.onSaveDraft(input);
        }
    };


    public MessageInput(Context context) {
        super(context);
        init(context);
    }

    public MessageInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MessageInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }


    public void setAttachmentsListener(AttachmentsListener attachmentsListener) {
        this.attachmentsListener = attachmentsListener;
    }


    public EditText getInputEditText() {
        return messageInput;
    }


    public ImageButton getButton() {
        return messageSendButton;
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.messageSendButton) {
                if (canSubmit) {
                    canSubmit = false;
                    onSubmit();
                    clear();
                    canSubmit = true;
                    removeCallbacks(typingTimerRunnable);
                    post(typingTimerRunnable);
                } else if (onRecordClickListener != null) {
                    onRecordClickListener.onRecordClick(view);
                }
            } else if (id == R.id.attachmentButton) {
                onAddAttachments();
            }

        }
    };

    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        input = s;
        if (s.length() <= 0) {
            if (messageInputStyle.isShowMicIcon()) {
                this.messageSendButton.setImageDrawable(messageInputStyle.getInputVoiceIcon());
            } else {
                messageSendButton.setEnabled(false);
                messageSendButton.setVisibility(View.INVISIBLE);
            }
        }
        if (s.length() > 0) {
            if (!isTyping) {
                isTyping = true;
                if (typingListener != null) typingListener.onStartTyping();
            }
            removeCallbacks(typingTimerRunnable);
            postDelayed(typingTimerRunnable, delayTypingStatusMillis);
        }
        if (!isSaveDraftInQueue) {
            isSaveDraftInQueue = true;
            postDelayed(draftSaverRunnable, delaySavingDraftInMillis);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        if (after > 0) {
            if (!messageInputStyle.isShowMicIcon()) {
                messageSendButton.setEnabled(true);
                messageSendButton.setVisibility(VISIBLE);
            }
            this.messageSendButton.setImageDrawable((messageInputStyle.getInputButtonIcon()));

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //do nothing
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (lastFocus && !hasFocus && typingListener != null) {
            typingListener.onStopTyping();
        }
        lastFocus = hasFocus;
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(draftSaverRunnable);
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onHideKeyboard() {
        hideKeyboard(messageInput);
        messageInput.clearFocus();
        return true;
    }

    private boolean onEdit(MessageFull messageFull) {
        return inputListener != null && inputListener.onEdit(input.toString(), messageFull);
    }

    private boolean onSubmit() {
        return inputListener != null && inputListener.onSubmit(input.toString());
    }

    private boolean onReply(MessageFull messageFull) {
        return inputListener != null && inputListener.onReply(input.toString(), messageFull);
    }

    private void onAddAttachments() {
        if (attachmentsListener != null) {
            attachmentsListener.onAddAttachments(attachmentButton);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        init(context);

        messageInputStyle = MessageInputStyle.parse(context, attrs);

        this.messageInput.setMaxLines(messageInputStyle.getInputMaxLines());
        this.messageInput.setHint(messageInputStyle.getInputHint());
        if (messageInputStyle.getInputText() != null && messageInputStyle.getInputText().length() > 0) {
            this.messageInput.setText(messageInputStyle.getInputText());
            this.messageSendButton.setImageDrawable(messageInputStyle.getInputButtonIcon());
        } else {
            if (messageInputStyle.isShowMicIcon()) {
                this.messageSendButton.setImageDrawable(messageInputStyle.getInputVoiceIcon());
            } else {
                messageSendButton.setEnabled(false);
                this.messageSendButton.setImageDrawable(messageInputStyle.getInputButtonIcon());
                messageSendButton.setVisibility(View.INVISIBLE);
            }
        }
        this.messageInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageInputStyle.getInputTextSize());
        this.messageInput.setTextColor(messageInputStyle.getInputTextColor());
        this.messageInput.setHintTextColor(messageInputStyle.getInputHintColor());
        ViewCompat.setBackground(this.messageInput, messageInputStyle.getInputBackground());
        setCursor(messageInputStyle.getInputCursorDrawable());

        showAttachmentButton = messageInputStyle.showAttachmentButton();
        this.attachmentButton.setVisibility(showAttachmentButton ? VISIBLE : GONE);
        this.attachmentButton.setImageDrawable(messageInputStyle.getAttachmentButtonIcon());
        ViewCompat.setBackground(this.attachmentButton, messageInputStyle.getAttachmentButtonBackground());

        this.emojiButton.setVisibility(messageInputStyle.showEmojiButton() ? VISIBLE : GONE);
        this.emojiButton.setImageDrawable(messageInputStyle.getEmojiButtonIcon());
        ViewCompat.setBackground(this.emojiButton, messageInputStyle.getEmojiButtonBackground());

        ViewCompat.setBackground(messageSendButton, messageInputStyle.getInputButtonBackground());

        this.delayTypingStatusMillis = messageInputStyle.getDelayTypingStatus();
        this.delaySavingDraftInMillis = messageInputStyle.getDelaySavingDraft();
    }

    private void init(Context context) {
        inflate(context, R.layout.view_message_input, this);
        setOrientation(VERTICAL);

        messageInput = findViewById(R.id.messageInput);
        messageSendButton = findViewById(R.id.messageSendButton);
        emojiButton = findViewById(R.id.emojissButton);
        attachmentButton = findViewById(R.id.attachmentButton);
        disableTextView = findViewById(R.id.disable_text_view);
        scaleAnim = new ScaleAnim(messageSendButton);
        messageSendButton.setOnTouchListener(this);
        audioRecorder = new AudioRecorder();

        if (isInEditMode()) {
            return;
        }
    }

    public void enable(boolean enable, String text) {
        if (enable) {
            messageSendButton.setOnClickListener(onClickListener);
            attachmentButton.setOnClickListener(onClickListener);
            EmojIconActions emojiIcon = new EmojIconActions(getContext(), this, messageInput, emojiButton);
            emojiIcon.ShowEmojIcon();
            emojiIcon.setIconsIds(R.drawable.ic_keyboard, R.drawable.ic_emoji);
            messageInput.addTextChangedListener(this);
            messageInput.setOnFocusChangeListener(this);
            messageInput.setKeyPreImeListener(this);
        } else {
            disableTextView.setText(text);
            disableTextView.setVisibility(View.VISIBLE);
            disableTextView.setOnClickListener(v -> {
            });
        }
    }

    private void setCursor(Drawable drawable) {
        if (drawable == null) return;

        try {
            @SuppressLint("SoonBlockedPrivateApi") final Field drawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
            drawableResField.setAccessible(true);

            final Object drawableFieldOwner;
            final Class<?> drawableFieldClass;
            final Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            drawableFieldOwner = editorField.get(this.messageInput);
            drawableFieldClass = drawableFieldOwner.getClass();
            final Field drawableField = drawableFieldClass.getDeclaredField("mCursorDrawable");
            drawableField.setAccessible(true);
            drawableField.set(drawableFieldOwner, new Drawable[]{drawable, drawable});
        } catch (Exception ignored) {
        }
    }

    public void setTypingListener(TypingListener typingListener) {
        this.typingListener = typingListener;
    }

    public void enterEditMode(MessageFull message) {
        this.messageToBeReturned = message;
        String text = message.getText();
        messageInput.setText(text);
        this.messageSendButton.setImageDrawable(messageInputStyle.getInputButtonEditModeIcon());
    }

    public void appendText(String text) {
        messageInput.append(text);
        if (text != null && text.length() > 0) {
            this.messageSendButton.setImageDrawable(messageInputStyle.getInputButtonIcon());
        }
    }

    /***
     * open keyboard and set focus in editText
     */
    public void enterTypingMode() {
        messageInput.setFocusableInTouchMode(true);
        messageInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(messageInput, InputMethodManager.SHOW_IMPLICIT);
    }

    public void clear() {
        removeCallbacks(draftSaverRunnable);
        messageInput.setText("");
        messageToBeReturned = null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setClip(this);
    }

    public void setClip(View v) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(false);
            ((ViewGroup) v).setClipToPadding(false);
        }

        if (v.getParent() instanceof View) {
            setClip((View) v.getParent());
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (isListenForRecord()) {
            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    recordView.onActionDown((RecordButton) view, motionEvent);
                    break;


                case MotionEvent.ACTION_MOVE:
                    recordView.onActionMove((RecordButton) view, motionEvent);
                    break;

                case MotionEvent.ACTION_UP:
                    recordView.onActionUp((RecordButton) view);
                    break;

            }

        }
        return isListenForRecord();

    }

    public void startScale() {
        scaleAnim.start();
        emojiButton.setVisibility(View.INVISIBLE);
        attachmentButton.setVisibility(View.INVISIBLE);
        messageInput.setVisibility(View.INVISIBLE);
    }

    public void stopScale() {
        scaleAnim.stop();
        emojiButton.setVisibility(View.VISIBLE);
        attachmentButton.setVisibility(View.VISIBLE);
        messageInput.setVisibility(View.VISIBLE);
    }


    public boolean isListenForRecord() {
        return messageInputStyle.isShowMicIcon();
    }

    public void setRecordView(RecordView recordView, Activity activity) {
        this.recordView = recordView;
        this.activity = activity;
        this.recordView.setOnRecordListener(this);
        this.recordView.setRecordPermissionHandler(this);
    }

    // permission for voice message
    @Override
    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
        if (recordPermissionAvailable) {
            return true;
        }
        ActivityCompat.
                requestPermissions(activity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        0);
        return false;
    }

    @Override
    public void onStart() {
        recordFile = new File(getContext().getFilesDir(), UUID.randomUUID().toString() + ".aac");
        try {
            audioRecorder.start(recordFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCancel() {
        audioRecorder.stopRecording(true, recordFile);
    }

    @Override
    public void onFinish(long recordTime, boolean limitReached) {
        audioRecorder.stopRecording(false, recordFile);
        voiceListener.onVoiceMessageFinish(audioRecorder.getHumanTimeText(recordTime), Uri.fromFile(recordFile));
    }

    @Override
    public void onLessThanSecond() {
        audioRecorder.stopRecording(true, recordFile);
    }

    public void setVoiceListener(VoiceListener voiceListener) {
        this.voiceListener = voiceListener;
    }

    public interface InputListener {
        boolean onSubmit(CharSequence input);
        default boolean onEdit(CharSequence input, MessageFull messageToBeEdited) { return false; }
        default boolean onReply(CharSequence input, MessageFull messageToBeEdited) { return false; }
        default void onSaveDraft(CharSequence draft) { }
    }

    public interface AttachmentsListener {
        void onAddAttachments(View attachmentBtnAnchor);
    }

    public interface TypingListener {
        void onStartTyping();
        void onStopTyping();
    }

    public interface VoiceListener {
        void onVoiceMessageFinish(String time, Uri uri);
    }

    public void hideKeyboard(@NonNull View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
