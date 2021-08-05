package com.emamagic.chat_box

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.emamagic.chat_box.Const.CHAT_BOX_ICON_DEFAULT_VISIBILITY
import com.emamagic.chat_box.Const.DEFAULT_CANCEL_BOUNDS
import com.emamagic.chat_box.Const.DEFAULT_VALUE
import com.emamagic.chat_box.Const.DEFAULT_MAX_LINES

class ChatBoxStyle(context: Context, attrs: AttributeSet) : Style(context, attrs) {

    private var sendButtonIcon = DEFAULT_VALUE

    private var showAttachmentButton = CHAT_BOX_ICON_DEFAULT_VISIBILITY
    private var attachmentButtonIcon = DEFAULT_VALUE

    private var showEmojiButton = CHAT_BOX_ICON_DEFAULT_VISIBILITY
    private var emojiButtonIcon = DEFAULT_VALUE

    private var inputText: String = ""
    private var inputMaxLines = DEFAULT_VALUE

    private var inputHint: String = getString(R.string.chat_box_hint)

    private var inputTextSize = DEFAULT_VALUE
    private var inputTextColor = DEFAULT_VALUE
    private var inputHintColor = DEFAULT_VALUE

    private var showRecordButton = CHAT_BOX_ICON_DEFAULT_VISIBILITY
    private var recordButtonIcon = DEFAULT_VALUE


    companion object {
        @JvmStatic
        fun parse(
            context: Context,
            attrs: AttributeSet
        ): ChatBoxStyle {
            val style = ChatBoxStyle(context, attrs)
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChatBox)

            style.sendButtonIcon =
                typedArray.getResourceId(R.styleable.ChatBox_sendButtonIcon, DEFAULT_VALUE)

            style.showAttachmentButton =
                typedArray.getBoolean(R.styleable.ChatBox_showAttachmentButton, CHAT_BOX_ICON_DEFAULT_VISIBILITY)

            style.attachmentButtonIcon =
                typedArray.getResourceId(R.styleable.ChatBox_attachmentButtonIcon, DEFAULT_VALUE)

            style.showEmojiButton =
                typedArray.getBoolean(R.styleable.ChatBox_showEmojiButton, CHAT_BOX_ICON_DEFAULT_VISIBILITY)

            style.emojiButtonIcon =
                typedArray.getResourceId(R.styleable.ChatBox_emojiButtonIcon, DEFAULT_VALUE)

            style.inputText = typedArray.getString(R.styleable.ChatBox_inputText) ?: ""


            style.inputMaxLines = typedArray.getInt(
                R.styleable.ChatBox_inputMaxLines,
                DEFAULT_MAX_LINES
            )

            style.inputHint = typedArray.getString(R.styleable.ChatBox_inputHint) ?: style.getString(R.string.chat_box_hint)

            style.inputTextSize = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_inputTextSize,
                style.getDimension(R.dimen.input_text_size)
            )
            style.inputTextColor = typedArray.getColor(
                R.styleable.ChatBox_inputTextColor,
                style.getColor(R.color.dark_grey_two)
            )
            style.inputHintColor = typedArray.getColor(
                R.styleable.ChatBox_inputHintColor,
                style.getColor(R.color.warm_grey_three)
            )

            style.showRecordButton =
                typedArray.getBoolean(R.styleable.ChatBox_showRecordButton, CHAT_BOX_ICON_DEFAULT_VISIBILITY)

            style.recordButtonIcon =
                typedArray.getResourceId(R.styleable.ChatBox_recordButtonIcon, DEFAULT_VALUE)

            typedArray.recycle()

            return style
        }
    }


    fun getInputText(): String {
        return inputText
    }

    fun getInputMaxLines(): Int {
        return inputMaxLines
    }

    fun getInputHint(): String {
        return inputHint
    }

    fun getInputTextSize(): Int {
        return inputTextSize
    }

    fun getInputTextColor(): Int {
        return inputTextColor
    }

    fun getInputHintColor(): Int {
        return inputHintColor
    }

    fun isShowingAttachmentButton(): Boolean {
        return showAttachmentButton
    }


    fun isShowingEmojiButton(): Boolean {
        return showEmojiButton
    }

    fun isShowingRecordButton(): Boolean {
        return showRecordButton
    }

    fun getDefaultIconBackground(): Drawable {
        return getSelector(
            getColor(R.color.white), getColor(R.color.white),
            getColor(R.color.white), R.drawable.mask
        )
    }

    fun getAttachmentButtonIcon(): Drawable? {
        return if (attachmentButtonIcon == DEFAULT_VALUE) {
            getSelector(
                getColor(R.color.warm_grey), getColor(R.color.colorPrimaryDark),
                getColor(R.color.warm_grey), R.drawable.ic_attachment
            )
        } else {
            getDrawable(attachmentButtonIcon)
        }
    }


    fun getEmojiButtonIcon(): Drawable? {
        return if (emojiButtonIcon == DEFAULT_VALUE) {
            getSelector(
                getColor(R.color.warm_grey), getColor(R.color.colorPrimaryDark),
                getColor(R.color.warm_grey), R.drawable.ic_emoji
            )
        } else {
            getDrawable(emojiButtonIcon)
        }
    }

    fun getSendButtonIcon(): Drawable? {
        return if (sendButtonIcon == DEFAULT_VALUE) {
            getSelector(
                getColor(R.color.warm_grey), getColor(R.color.colorPrimaryDark),
                getColor(R.color.warm_grey), R.drawable.ic_send
            )
        } else {
            getDrawable(sendButtonIcon)
        }
    }

    fun getRecordButtonIcon(): Drawable? {
        return if (recordButtonIcon == DEFAULT_VALUE) {
            getSelector(
                getColor(R.color.warm_grey), getColor(R.color.colorPrimaryDark),
                getColor(R.color.warm_grey), R.drawable.recv_ic_mic
            )
        } else {
            getDrawable(recordButtonIcon)
        }
    }

}