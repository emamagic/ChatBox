package com.emamagic.chat_box

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat

class ChatBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs), TextWatcher,
    View.OnTouchListener, MessageEditText.KeyPreImeListener {

    private lateinit var messageInput: MessageEditText
    private lateinit var messageSendButton: ImageButton
    private lateinit var emojiButton: ImageButton
    private lateinit var attachmentButton: ImageButton

    private lateinit var messageInputStyle: MessageInputStyle
    private var showAttachmentButton = false
    private var delayTypingStatusMillis = 0
    private var delaySavingDraftInMillis = 0

    init {
        if (attrs == null) { init(context) }
        else { init(context, attrs) }
    }

    @SuppressLint("WrongConstant", "ClickableViewAccessibility")
    private fun init(context: Context) {
        inflate(context, R.layout.view_message_input, this)
        orientation = LinearLayout.VERTICAL
        messageInput = findViewById(R.id.messageInput)
        messageSendButton = findViewById(R.id.messageSendButton)
        emojiButton = findViewById(R.id.emojissButton)
        attachmentButton = findViewById(R.id.attachmentButton)
        messageSendButton.setOnTouchListener(this)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        init(context)
        messageInputStyle = MessageInputStyle.parse(context, attrs)
        messageInput.maxLines = messageInputStyle.getInputMaxLines()
        messageInput.hint = messageInputStyle.getInputHint()
        if (messageInputStyle.getInputText().isNotEmpty()) {
            messageInput.setText(messageInputStyle.getInputText())
            messageSendButton.setImageDrawable(messageInputStyle.getInputButtonIcon())
        } else {
            if (messageInputStyle.isShowMicIcon()) {
                messageSendButton.setImageDrawable(messageInputStyle.getInputVoiceIcon())
            } else {
                messageSendButton.isEnabled = false
                messageSendButton.setImageDrawable(messageInputStyle.getInputButtonIcon())
                messageSendButton.visibility = INVISIBLE
            }
        }
        messageInput.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            messageInputStyle.getInputTextSize().toFloat()
        )
        messageInput.setTextColor(messageInputStyle.getInputTextColor())
        messageInput.setHintTextColor(messageInputStyle.getInputHintColor())
        ViewCompat.setBackground(messageInput, messageInputStyle.getInputBackground())
        Utility.setCursor(messageInputStyle.getInputCursorDrawable(), messageInput)
        showAttachmentButton = messageInputStyle.showAttachmentButton()
        attachmentButton.visibility = if (showAttachmentButton) VISIBLE else GONE
        attachmentButton.setImageDrawable(messageInputStyle.getAttachmentButtonIcon())
        ViewCompat.setBackground(
            attachmentButton,
            messageInputStyle.getAttachmentButtonBackground()
        )
        emojiButton.visibility = if (messageInputStyle.showEmojiButton()) VISIBLE else GONE
        emojiButton.setImageDrawable(messageInputStyle.getEmojiButtonIcon())
        ViewCompat.setBackground(emojiButton, messageInputStyle.getEmojiButtonBackground())
        ViewCompat.setBackground(messageSendButton, messageInputStyle.getInputButtonBackground())
        this.delayTypingStatusMillis = messageInputStyle.getDelayTypingStatus()
        this.delaySavingDraftInMillis = messageInputStyle.getDelaySavingDraft()
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {}
    override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
        if (s.isEmpty()) {
            if (messageInputStyle.isShowMicIcon()) {
                messageSendButton.setImageDrawable(messageInputStyle.getInputVoiceIcon())
            } else {
                messageSendButton.isEnabled = false
                messageSendButton.visibility = INVISIBLE
            }
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> recordView.onActionDown(view as RecordButton, motionEvent)
            MotionEvent.ACTION_MOVE -> recordView.onActionMove(view as RecordButton, motionEvent)
            MotionEvent.ACTION_UP -> recordView.onActionUp(view as RecordButton)
        }
        return true
    }


    override fun onHideKeyboard(): Boolean {
        Utility.hideKeyboard(messageInput)
        messageInput.clearFocus()
        return true
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

    interface InputListener {
        fun onSubmit(input: CharSequence?): Boolean
    }

    interface AttachmentsListener {
        fun onAddAttachments(attachmentBtnAnchor: View?)
    }

    interface VoiceListener {
        fun onVoiceMessageFinish(time: String?, uri: Uri?)
    }


}