package com.emamagic.chat_box

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import com.emamagic.emoji.EmojIconActions

class ChatBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr), TextWatcher,
    MessageEditText.KeyPreImeListener, View.OnFocusChangeListener, View.OnClickListener {

    private lateinit var messageInput: MessageEditText
    private lateinit var sendButton: ImageButton
    private lateinit var emojiButton: ImageButton
    private lateinit var attachmentButton: ImageButton
    private lateinit var recordButton: RecordButton
    private lateinit var chatBoxStyle: ChatBoxStyle
    private var attachmentTypeSelector: AttachmentTypeSelector? = null
    private var input: String = ""
    private lateinit var activity: Activity

    private var attachmentsListener: AttachmentsListener? = null
    private var inputListener: InputListener? = null

    init {
        initViews(context)
        if (attrs != null) { initStyles(context, attrs) }
    }

    @SuppressLint("WrongConstant", "ClickableViewAccessibility")
    private fun initViews(context: Context) {
        inflate(context, R.layout.chat_box_layout, this)
        orientation = LinearLayout.VERTICAL
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        recordButton = findViewById(R.id.recordButton)
        emojiButton = findViewById(R.id.emojiButton)
        attachmentButton = findViewById(R.id.attachmentButton)
        setUpViews()
    }

    private fun initStyles(context: Context, attrs: AttributeSet) {
        chatBoxStyle = ChatBoxStyle.parse(context, attrs)
        messageInput.maxLines = chatBoxStyle.getInputMaxLines()
        messageInput.hint = chatBoxStyle.getInputHint()
        sendButton.setImageDrawable(chatBoxStyle.getSendButtonIcon())
        if (chatBoxStyle.isShowingRecordButton()) {
            sendButton.setImageDrawable(chatBoxStyle.getRecordButtonIcon())
        } else {
            sendButton.visibility = View.GONE
        }
        messageInput.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            chatBoxStyle.getInputTextSize().toFloat()
        )
        messageInput.setTextColor(chatBoxStyle.getInputTextColor())
        messageInput.setHintTextColor(chatBoxStyle.getInputHintColor())
        attachmentButton.visibility = if (chatBoxStyle.isShowingAttachmentButton()) VISIBLE else GONE
        attachmentButton.setImageDrawable(chatBoxStyle.getAttachmentButtonIcon())
        emojiButton.visibility = if (chatBoxStyle.isShowingEmojiButton()) VISIBLE else GONE
        emojiButton.setImageDrawable(chatBoxStyle.getEmojiButtonIcon())
        recordButton.setImageDrawable(chatBoxStyle.getRecordButtonIcon())
        ViewCompat.setBackground(
            attachmentButton,
            chatBoxStyle.getDefaultIconBackground()
        )
        ViewCompat.setBackground(
            sendButton,
            chatBoxStyle.getDefaultIconBackground()
        )
        ViewCompat.setBackground(
            emojiButton,
            chatBoxStyle.getDefaultIconBackground()
        )
    }

    private fun setUpViews() {
        sendButton.setOnClickListener(this)
        attachmentButton.setOnClickListener(this)
        val emojiIcon = EmojIconActions(context, this, messageInput, emojiButton)
        emojiIcon.ShowEmojIcon()
        emojiIcon.setIconsIds(R.drawable.ic_keyboard, R.drawable.ic_emoji)
        messageInput.addTextChangedListener(this)
        messageInput.onFocusChangeListener = this
        messageInput.setKeyPreImeListener(this)
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {}
    override fun onTextChanged(input: CharSequence, p1: Int, p2: Int, p3: Int) {
        this.input = input.toString()
        if (input.isEmpty()) {
            attachmentButton.visibility = View.VISIBLE
            if (chatBoxStyle.isShowingRecordButton()) {
                sendButton.setImageDrawable(chatBoxStyle.getRecordButtonIcon())
            } else {
                sendButton.visibility = View.GONE
            }
        } else {
            sendButton.visibility = View.VISIBLE
            attachmentButton.visibility = View.GONE
            sendButton.setImageDrawable(chatBoxStyle.getSendButtonIcon())
        }
    }


    override fun onClick(view: View) {
        when(view.id) {
            R.id.sendButton -> { inputListener?.onSubmit(input) }
            R.id.attachmentButton -> {
                if (attachmentsListener == null) {
                    if (attachmentTypeSelector == null) {
                        attachmentTypeSelector = AttachmentTypeSelector(context, activity)
                    }
                    attachmentTypeSelector?.show(attachmentButton)
                } else {
                    attachmentsListener?.onAddAttachments()
                }
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

    fun setInputListener(inputListener: InputListener, activity: Activity) {
        this.inputListener = inputListener
        this.activity = activity
    }

    fun setAttachmentListener(attachmentsListener: AttachmentsListener) {
        this.attachmentsListener = attachmentsListener
    }


    interface InputListener {
        fun onSubmit(input: String)
    }

    interface AttachmentsListener {
        fun onAddAttachments()
    }

    interface RecorderListener {
        fun onRecordFinish(time: String, uri: Uri)
    }

    override fun onFocusChange(view: View, hasFocus: Boolean) {

    }
    interface TypingListener {
        fun onStartTyping()
        fun onStopTyping()
    }

}