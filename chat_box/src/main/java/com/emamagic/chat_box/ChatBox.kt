package com.emamagic.chat_box

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import com.emamagic.emoji.EmojIconActions
import java.lang.Exception

class ChatBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr), TextWatcher,
    MessageEditText.KeyPreImeListener, View.OnFocusChangeListener {

    private lateinit var messageInput: MessageEditText
    private lateinit var messageSendButton: ImageButton
    private lateinit var emojiButton: ImageButton
    private lateinit var attachmentButton: ImageButton
    private lateinit var recordButton: RecordButton

    private lateinit var chatBoxStyle: ChatBoxStyle
    private var showAttachmentButton = false

    init {
        initViews(context)
        if (attrs != null) { initStyles(context, attrs) }
    }

    @SuppressLint("WrongConstant", "ClickableViewAccessibility")
    private fun initViews(context: Context) {
        inflate(context, R.layout.chat_box_layout, this)
        orientation = LinearLayout.VERTICAL
        messageInput = findViewById(R.id.messageInput)
        messageSendButton = findViewById(R.id.sendButton)
        recordButton = findViewById(R.id.recordButton)
        emojiButton = findViewById(R.id.emojiButton)
        attachmentButton = findViewById(R.id.attachmentButton)
        setUpViews()
    }

    private fun initStyles(context: Context, attrs: AttributeSet) {
        chatBoxStyle = ChatBoxStyle.parse(context, attrs)
        messageInput.maxLines = chatBoxStyle.getInputMaxLines()
        messageInput.hint = chatBoxStyle.getInputHint()
        messageSendButton.setImageDrawable(chatBoxStyle.getSendButtonIcon())

        messageInput.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            chatBoxStyle.getInputTextSize().toFloat()
        )
        messageInput.setTextColor(chatBoxStyle.getInputTextColor())
        messageInput.setHintTextColor(chatBoxStyle.getInputHintColor())
        showAttachmentButton = chatBoxStyle.isShowingAttachmentButton()
        attachmentButton.visibility = if (showAttachmentButton) VISIBLE else GONE
        attachmentButton.setImageDrawable(chatBoxStyle.getAttachmentButtonIcon())
        emojiButton.visibility = if (chatBoxStyle.isShowingEmojiButton()) VISIBLE else GONE
        emojiButton.setImageDrawable(chatBoxStyle.getEmojiButtonIcon())
        recordButton.setImageDrawable(chatBoxStyle.getRecordButtonIcon())
        ViewCompat.setBackground(
            attachmentButton,
            chatBoxStyle.getDefaultIconBackground()
        )
        ViewCompat.setBackground(
            messageSendButton,
            chatBoxStyle.getDefaultIconBackground()
        )
        ViewCompat.setBackground(
            emojiButton,
            chatBoxStyle.getDefaultIconBackground()
        )
    }

    private fun setUpViews() {
        messageSendButton.setOnClickListener(onClickListener)
        attachmentButton.setOnClickListener(onClickListener)
        val emojiIcon = EmojIconActions(context, this, messageInput, emojiButton)
        emojiIcon.ShowEmojIcon()
        emojiIcon.setIconsIds(R.drawable.ic_keyboard, R.drawable.ic_emoji)
        messageInput.addTextChangedListener(this)
        messageInput.onFocusChangeListener = this
        messageInput.setKeyPreImeListener(this)
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {}
    override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
        if (s.isEmpty()) {
            if (chatBoxStyle.isShowingRecordButton()) {
                messageSendButton.setImageDrawable(chatBoxStyle.getRecordButtonIcon())
            } else {
                messageSendButton.isEnabled = false
                messageSendButton.visibility = INVISIBLE
            }
        }
    }

    private val onClickListener = View.OnClickListener {

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

    override fun onFocusChange(p0: View?, p1: Boolean) {

    }


}