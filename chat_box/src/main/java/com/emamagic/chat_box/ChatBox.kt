package com.emamagic.chat_box

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
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

import androidx.constraintlayout.widget.ConstraintLayout


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
    private var delayTypingStatusMillis = 0
    private var input: String = ""
    private var isTyping: Boolean = false
    private lateinit var activity: Activity

    private var attachmentsListener: AttachmentsListener? = null
    private var inputListener: InputListener? = null
    private var typingListener: TypingListener? = null

    private var typingTimerRunnable = Runnable {
        if (isTyping) {
            isTyping = false
            typingListener?.onStopTyping()
        }
    }

    init {
        initViews(context)
        if (attrs != null) {
            initStyles(context, attrs)
        }
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

        /* send */
        ViewCompat.setBackground(
            sendButton,
            chatBoxStyle.getSendButtonBackground()
        )
        if (chatBoxStyle.isShowingRecordButton()) {
            sendButton.setImageDrawable(chatBoxStyle.getRecordButtonIcon())
        } else {
            sendButton.visibility = View.GONE
        }
        sendButton.layoutParams.width = chatBoxStyle.getSendButtonWidth()
        sendButton.layoutParams.height = chatBoxStyle.getSendButtonHeight()
        setMargin(sendButton, chatBoxStyle.getSendButtonMargin())


        /* attachment */
        attachmentButton.visibility =
            if (chatBoxStyle.isShowingAttachmentButton()) VISIBLE else GONE
        attachmentButton.setImageDrawable(chatBoxStyle.getAttachmentButtonIcon())
        ViewCompat.setBackground(
            attachmentButton,
            chatBoxStyle.getAttachmentButtonBackground()
        )
        attachmentButton.layoutParams.width = chatBoxStyle.getAttachmentButtonWidth()
        attachmentButton.layoutParams.height = chatBoxStyle.getAttachmentButtonHeight()
        setMargin(attachmentButton, chatBoxStyle.getAttachmentButtonMargin())


        /* emoji */
        emojiButton.visibility = if (chatBoxStyle.isShowingEmojiButton()) VISIBLE else GONE
        emojiButton.setImageDrawable(chatBoxStyle.getEmojiButtonIcon())
        ViewCompat.setBackground(
            emojiButton,
            chatBoxStyle.getEmojiButtonBackground()
        )
        emojiButton.layoutParams.width = chatBoxStyle.getEmojiButtonWidth()
        emojiButton.layoutParams.height = chatBoxStyle.getEmojiButtonHeight()
        setMargin(emojiButton, chatBoxStyle.getEmojiButtonMargin())


        /* input text */
        messageInput.maxLines = chatBoxStyle.getInputMaxLines()
        messageInput.hint = chatBoxStyle.getInputHint()
        messageInput.setText(chatBoxStyle.getInputText())
        setCursor(chatBoxStyle.getInputCursorDrawable())
        ViewCompat.setBackground(messageInput, chatBoxStyle.getInputBackground())
        messageInput.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            chatBoxStyle.getInputTextSize().toFloat()
        )
        messageInput.setTextColor(chatBoxStyle.getInputTextColor())
        messageInput.setHintTextColor(chatBoxStyle.getInputHintColor())
       setMargin(messageInput, chatBoxStyle.getEmojiButtonMargin())


        /* record button */
        recordButton.setImageDrawable(chatBoxStyle.getRecordButtonIcon())

        /* typing status */
        delayTypingStatusMillis = chatBoxStyle.getDelayTypingStatus()


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
            startTyping()
            sendButton.visibility = View.VISIBLE
            attachmentButton.visibility = View.GONE
            sendButton.setImageDrawable(chatBoxStyle.getSendButtonIcon())
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun setCursor(drawable: Drawable?) {
        if (drawable == null) return
        try {
            @SuppressLint("SoonBlockedPrivateApi") val drawableResField =
                TextView::class.java.getDeclaredField("mCursorDrawableRes")
            drawableResField.isAccessible = true
            val drawableFieldOwner: Any
            val drawableFieldClass: Class<*>
            val editorField = TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            drawableFieldOwner = editorField[messageInput]
            drawableFieldClass = drawableFieldOwner.javaClass
            val drawableField = drawableFieldClass.getDeclaredField("mCursorDrawable")
            drawableField.isAccessible = true
            drawableField[drawableFieldOwner] =
                arrayOf(drawable, drawable)
        } catch (ignored: Exception) {
        }
    }

    private fun setMargin(view: View, margin: Int) {
        (view.layoutParams as ConstraintLayout.LayoutParams).setMargins(margin, margin, margin, margin)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.sendButton -> {
                inputListener?.onSubmit(input)
            }
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


    override fun onFocusChange(view: View, hasFocus: Boolean) {

    }

    private fun startTyping() {
        if (!isTyping) {
            isTyping = true
            typingListener?.onStartTyping()
        }
        removeCallbacks(typingTimerRunnable)
        postDelayed(typingTimerRunnable, delayTypingStatusMillis.toLong())
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

    fun setTypingListener(typingListener: TypingListener) {
        this.typingListener = typingListener;
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

    interface TypingListener {
        fun onStartTyping()
        fun onStopTyping()
    }

}