package com.emamagic.messageinput;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.devlomi.record_view.AudioRecorder;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;
import com.devlomi.record_view.ScaleAnim;
import com.emamagic.chatbox.ViewHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;
import im.limoo.emoji.EmojIconActions;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Component for input outcoming messages
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MessageInput extends LinearLayout implements TextWatcher, View.OnFocusChangeListener,
        MessageEditText.KeyPreImeListener, View.OnTouchListener, RecordPermissionHandler, OnRecordListener {

    protected MessageEditText messageInput;
    protected ImageButton messageSendButton;
    protected ImageButton emojiButton;
    protected ImageButton attachmentButton;
    protected ReplyBar replyBar;
    private FontTextView disableTextView;

    private boolean inEditMode;
    private boolean inReplyMode;

    private Activity activity;
    private ScaleAnim scaleAnim;
    private RecordView recordView;
    private boolean listenForRecord = true;
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
    private MessageInputStyle messageeInputStyle;
    private boolean canSubmit = true;
    private boolean isSaveDraftInQueue = false;
    private Runnable draftSaverRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("DRAFT ", "save draft : " + input);
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

    /**
     * Sets callback for 'submit' button.
     *
     * @param inputListener input callback
     */
    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    /**
     * Sets callback for 'add' button.
     *
     * @param attachmentsListener input callback
     */
    public void setAttachmentsListener(AttachmentsListener attachmentsListener) {
        this.attachmentsListener = attachmentsListener;
    }

    /**
     * Returns EditText for messages input
     *
     * @return EditText
     */
    public EditText getInputEditText() {
        return messageInput;
    }

    /**
     * Returns `submit` button
     *
     * @return ImageButton
     */
    public ImageButton getButton() {
        return messageSendButton;
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.messageSendButton) {
                if (canSubmit && !isListenForRecord()) {
                    canSubmit = false;
                    boolean isSubmitted;
                    if (inEditMode)
                        isSubmitted = onEdit(messageToBeReturned);
                    else if (inReplyMode)
                        isSubmitted = onReply(messageToBeReturned);
                    else isSubmitted = onSubmit();
                    if (isSubmitted) {
                        clear();
                    }
                    canSubmit = true;
                    removeCallbacks(typingTimerRunnable);
                    post(typingTimerRunnable);
                } else if (onRecordClickListener != null){
                    onRecordClickListener.onClick(view);
                }
            } else if (id == R.id.attachmentButton) {
                onAddAttachments();
            }

        }
    };

    /**
     * This method is called to notify you that, within s,
     * the count characters beginning at start have just replaced old text that had length before
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        input = s;
        if (s.length() <= 0) {
            if (messageeInputStyle.isShowMicIcon()) {
                this.messageSendButton.setImageDrawable(messageeInputStyle.getInputVoiceIcon());
            }
            else {
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

    /**
     * This method is called to notify you that, within s,
     * the count characters beginning at start are about to be replaced by new text with length after.
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        if (after > 0) {
            if (messageeInputStyle.isShowMicIcon()) {
                this.messageSendButton.setImageDrawable((messageeInputStyle.getInputButtonIcon()));
                listenForRecord = false;
            } else {
                messageSendButton.setEnabled(true);
                messageSendButton.setVisibility(VISIBLE);
                messageSendButton.setImageDrawable((messageeInputStyle.getInputButtonIcon()));
            }

        } else {
            listenForRecord = true;
        }
    }

    /**
     * This method is called to notify you that, somewhere within s, the text has been changed.
     */
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
        ViewHelper.hideKeyboard(messageInput);
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

        messageeInputStyle = MessageInputStyle.parse(context, attrs);

        this.messageInput.setMaxLines(messageeInputStyle.getInputMaxLines());
        this.messageInput.setHint(messageeInputStyle.getInputHint());
        if (messageeInputStyle.getInputText() != null && messageeInputStyle.getInputText().length() > 0) {
            this.messageInput.setText(messageeInputStyle.getInputText());
            this.messageSendButton.setImageDrawable(messageeInputStyle.getInputButtonIcon());
            listenForRecord = false;
        } else {
            if (messageeInputStyle.isShowMicIcon()) {
                this.messageSendButton.setImageDrawable(messageeInputStyle.getInputVoiceIcon());
                listenForRecord = true;
            } else {
                messageSendButton.setEnabled(false);
                this.messageSendButton.setImageDrawable(messageeInputStyle.getInputButtonIcon());
                messageSendButton.setVisibility(View.INVISIBLE);
            }
        }
        this.messageInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageeInputStyle.getInputTextSize());
        this.messageInput.setTextColor(messageeInputStyle.getInputTextColor());
        this.messageInput.setHintTextColor(messageeInputStyle.getInputHintColor());
        ViewCompat.setBackground(this.messageInput, messageeInputStyle.getInputBackground());
        setCursor(messageeInputStyle.getInputCursorDrawable());

        showAttachmentButton = messageeInputStyle.showAttachmentButton();
        this.attachmentButton.setVisibility(showAttachmentButton ? VISIBLE : GONE);
        this.attachmentButton.setImageDrawable(messageeInputStyle.getAttachmentButtonIcon());
        ViewCompat.setBackground(this.attachmentButton, messageeInputStyle.getAttachmentButtonBackground());

        this.emojiButton.setVisibility(messageeInputStyle.showEmojiButton() ? VISIBLE : GONE);
        this.emojiButton.setImageDrawable(messageeInputStyle.getEmojiButtonIcon());
        ViewCompat.setBackground(this.emojiButton, messageeInputStyle.getEmojiButtonBackground());

        ViewCompat.setBackground(messageSendButton, messageeInputStyle.getInputButtonBackground());

        this.delayTypingStatusMillis = messageeInputStyle.getDelayTypingStatus();
        this.delaySavingDraftInMillis = messageeInputStyle.getDelaySavingDraft();
    }

    private void init(Context context) {
        inflate(context, R.layout.view_message_input, this);
        setOrientation(VERTICAL);

        replyBar = new ReplyBar(context);
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
            final Field drawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
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

    public void setOnReplyBarCloseListener(ReplyBar.OnCloseListener onCloseListener) {
        replyBar.setOnCloseListener(onCloseListener);
    }

    public void enterEditMode(MessageFull message) {
        this.messageToBeReturned = message;
        String text = message.getText();
        replyBar.setText(text);
        replyBar.enterEditMode();
        if (!inEditMode) addView(replyBar, 0);
        inEditMode = true;
        messageInput.setText(text);
        this.messageSendButton.setImageDrawable(messageeInputStyle.getInputButtonEditModeIcon());
    }

    public void appendText(String text) {
        messageInput.append(text);
        if (text != null && text.length() > 0) {
            this.messageSendButton.setImageDrawable(messageeInputStyle.getInputButtonIcon());
            listenForRecord = false;
        }
    }

    public void enterReplyMode(MessageFull message) {
        this.messageToBeReturned = message;
        replyBar.setText(message.getTextModified().toString());
        replyBar.enterReplyMode(message.getUser().getDisplayName());
        inReplyMode = true;
        addView(replyBar, 0);
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

    public void dismissReplyBar() {
        if (replyBar.getParent() != null) {
            removeView(replyBar);
            this.messageSendButton.setImageDrawable(messageeInputStyle.getInputButtonIcon());
            listenForRecord = false;
            inEditMode = false;
            inReplyMode = false;
            messageInput.setText("");
        }
    }

    public void clear() {
        dismissReplyBar();
        removeCallbacks(draftSaverRunnable);
        messageInput.setText("");
        messageToBeReturned = null;
    }

    public boolean isEditing() {
        return inEditMode;
    }

    public boolean isInReplyMode() {
        return inReplyMode;
    }

    /**
     * set adapter for showing suggestion for mention
     */
//    public void setAutoCompleteAdapter(AutoCompleteAdapter adapter) {
//        this.messageInput.setThreshold(1);
//        this.messageInput.setAdapter(adapter);
//        this.messageInput.setTokenizer(new AutoCompleteAdapter.MentionTokenizer());
//    }

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
                    recordView.onActionDown((ImageButton) view, motionEvent, this);
                    break;


                case MotionEvent.ACTION_MOVE:
                    recordView.onActionMove((ImageButton) view, motionEvent, this);
                    break;

                case MotionEvent.ACTION_UP:
                    recordView.onActionUp((ImageButton) view, this);
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

    public void setListenForRecord(boolean listenForRecord) {
        this.listenForRecord = listenForRecord;
    }

    public boolean isListenForRecord() {
        if (!messageeInputStyle.isShowMicIcon()) return false;
        return listenForRecord;
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

    /*
     * Interface definition for a callback to be invoked when user pressed 'submit' button
     */
    public interface InputListener {


        /**
         * Fires when user presses 'send' button.
         *
         * @param input input entered by user
         * @return if input text is valid, you must return {@code true} and input will be cleared, otherwise return false.
         */
        boolean onSubmit(CharSequence input);

        default boolean onEdit(CharSequence input, MessageFull messageToBeEdited) {
            return false;
        }

        default boolean onReply(CharSequence input, MessageFull messageToBeEdited) {
            return false;
        }

        default void onSaveDraft(CharSequence draft) { }
    }

    /**
     * Interface definition for a callback to be invoked when user presses 'add' button
     */
    public interface AttachmentsListener {


        /**
         * Fires when user presses 'add' button.
         */
        void onAddAttachments(View attachmentBtnAnchor);
    }

    /**
     * Interface definition for a callback to be invoked when user typing
     */
    public interface TypingListener {


        /**
         * Fires when user presses start typing
         */
        void onStartTyping();

        /**
         * Fires when user presses stop typing
         */
        void onStopTyping();

    }

    public interface VoiceListener {

        void onVoiceMessageFinish(String time, Uri uri);
    }
}
