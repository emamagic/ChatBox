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
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr), TextWatcher,
    MessageEditText.KeyPreImeListener {

    private lateinit var messageInput: MessageEditText
    private lateinit var messageSendButton: ImageButton
    private lateinit var emojiButton: ImageButton
    private lateinit var attachmentButton: ImageButton
    private lateinit var recordButton: RecordButton

    private lateinit var chatBoxStyle: ChatBoxStyle
    private var showAttachmentButton = false
    private var delayTypingStatusMillis = 0
    private var delaySavingDraftInMillis = 0

    init {
        initViews(context)
        if (attrs != null) { initStyles(context, attrs) }
    }

    @SuppressLint("WrongConstant", "ClickableViewAccessibility")
    private fun initViews(context: Context) {
        inflate(context, R.layout.view_message_input, this)
        orientation = LinearLayout.VERTICAL
        messageInput = findViewById(R.id.messageInput)
        messageSendButton = findViewById(R.id.messageSendButton)
        emojiButton = findViewById(R.id.emojiButton)
        attachmentButton = findViewById(R.id.attachmentButton)
    }

    private fun initStyles(context: Context, attrs: AttributeSet) {
        chatBoxStyle = ChatBoxStyle.parse(context, attrs)
        messageInput.maxLines = chatBoxStyle.getInputMaxLines()
        messageInput.hint = chatBoxStyle.getInputHint()
        if (chatBoxStyle.getInputText().isNotEmpty()) {
            messageInput.setText(chatBoxStyle.getInputText())
            messageSendButton.setImageDrawable(chatBoxStyle.getInputButtonIcon())
        } else {
            if (chatBoxStyle.isShowMicIcon()) {
                messageSendButton.setImageDrawable(chatBoxStyle.getInputVoiceIcon())
            } else {
                messageSendButton.isEnabled = false
                messageSendButton.setImageDrawable(chatBoxStyle.getInputButtonIcon())
                messageSendButton.visibility = INVISIBLE
            }
        }
        messageInput.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            chatBoxStyle.getInputTextSize().toFloat()
        )
        messageInput.setTextColor(chatBoxStyle.getInputTextColor())
        messageInput.setHintTextColor(chatBoxStyle.getInputHintColor())
        ViewCompat.setBackground(messageInput, chatBoxStyle.getInputBackground())
        Utility.setCursor(chatBoxStyle.getInputCursorDrawable(), messageInput)
        showAttachmentButton = chatBoxStyle.showAttachmentButton()
        attachmentButton.visibility = if (showAttachmentButton) VISIBLE else GONE
        attachmentButton.setImageDrawable(chatBoxStyle.getAttachmentButtonIcon())
        ViewCompat.setBackground(
            attachmentButton,
            chatBoxStyle.getAttachmentButtonBackground()
        )
        emojiButton.visibility = if (chatBoxStyle.showEmojiButton()) VISIBLE else GONE
        emojiButton.setImageDrawable(chatBoxStyle.getEmojiButtonIcon())
        ViewCompat.setBackground(emojiButton, chatBoxStyle.getEmojiButtonBackground())
        ViewCompat.setBackground(messageSendButton, chatBoxStyle.getInputButtonBackground())
        this.delayTypingStatusMillis = chatBoxStyle.getDelayTypingStatus()
        this.delaySavingDraftInMillis = chatBoxStyle.getDelaySavingDraft()
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {}
    override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
        if (s.isEmpty()) {
            if (chatBoxStyle.isShowMicIcon()) {
                messageSendButton.setImageDrawable(chatBoxStyle.getInputVoiceIcon())
            } else {
                messageSendButton.isEnabled = false
                messageSendButton.visibility = INVISIBLE
            }
        }
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