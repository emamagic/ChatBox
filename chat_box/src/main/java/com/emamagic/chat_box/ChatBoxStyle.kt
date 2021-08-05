package com.emamagic.chat_box

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.emamagic.chat_box.Const.DEFAULT_VALUE

const val DEFAULT_MAX_LINES = 5
const val CHAT_BOX_ICON_DEFAULT_VISIBILITY = true
const val DEFAULT_DELAY_TYPING_STATUS = 1000

class ChatBoxStyle(context: Context, attrs: AttributeSet) : Style(context, attrs) {

    /* send button icon */
    private var sendButtonIcon = DEFAULT_VALUE
    private var sendButtonIconColor = DEFAULT_VALUE
    private var sendButtonIconPressedColor = DEFAULT_VALUE
    private var sendButtonIconDisabledColor = DEFAULT_VALUE

    /* send button background */
    private var sendButtonBackground = DEFAULT_VALUE
    private var sendButtonBgColor = DEFAULT_VALUE
    private var sendButtonBgPressedColor = DEFAULT_VALUE
    private var sendButtonBgDisabledColor = DEFAULT_VALUE

    /* send button size */
    private var sendButtonWidth = DEFAULT_VALUE
    private var sendButtonHeight = DEFAULT_VALUE
    private var sendButtonMargin = DEFAULT_VALUE


    /* attachment button icon */
    private var showAttachmentButton = CHAT_BOX_ICON_DEFAULT_VISIBILITY
    private var attachmentButtonIcon = DEFAULT_VALUE
    private var attachmentButtonIconColor = DEFAULT_VALUE
    private var attachmentButtonIconPressedColor = DEFAULT_VALUE
    private var attachmentButtonIconDisabledColor = DEFAULT_VALUE

    /* attachment button background */
    private var attachmentButtonBackground = DEFAULT_VALUE
    private var attachmentButtonBgColor = DEFAULT_VALUE
    private var attachmentButtonBgPressedColor = DEFAULT_VALUE
    private var attachmentButtonBgDisabledColor = DEFAULT_VALUE

    /* attachment button size */
    private var attachmentButtonWidth = DEFAULT_VALUE
    private var attachmentButtonHeight = DEFAULT_VALUE
    private var attachmentButtonMargin = DEFAULT_VALUE


    /* emoji button icon */
    private var showEmojiButton = CHAT_BOX_ICON_DEFAULT_VISIBILITY
    private var emojiButtonIcon = DEFAULT_VALUE
    private var emojiButtonIconColor = DEFAULT_VALUE
    private var emojiButtonIconPressedColor = DEFAULT_VALUE
    private var emojiButtonIconDisabledColor = DEFAULT_VALUE

    /* emoji button background */
    private var emojiButtonBackground = DEFAULT_VALUE
    private var emojiButtonBgColor = DEFAULT_VALUE
    private var emojiButtonBgPressedColor = DEFAULT_VALUE
    private var emojiButtonBgDisabledColor = DEFAULT_VALUE

    /* emoji button size */
    private var emojiButtonWidth = DEFAULT_VALUE
    private var emojiButtonHeight = DEFAULT_VALUE
    private var emojiButtonMargin = DEFAULT_VALUE

    /* input text */
    private var inputText: String = ""
    private var inputMaxLines = DEFAULT_VALUE
    private var inputHint: String = getString(R.string.chat_box_hint)
    private var inputTextSize = DEFAULT_VALUE
    private var inputTextColor = DEFAULT_VALUE
    private var inputHintColor = DEFAULT_VALUE
    private var inputBackground: Drawable? = null
    private var inputCursorDrawable: Drawable? = null

    /* record button */
    private var showRecordButton = CHAT_BOX_ICON_DEFAULT_VISIBILITY
    private var recordButtonIcon = DEFAULT_VALUE

    /* typing status */
    private var delayTypingStatus = DEFAULT_DELAY_TYPING_STATUS


    companion object {
        @JvmStatic
        fun parse(
            context: Context,
            attrs: AttributeSet
        ): ChatBoxStyle {
            val style = ChatBoxStyle(context, attrs)
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChatBox)

            /* send */
            style.sendButtonIcon =
                typedArray.getResourceId(R.styleable.ChatBox_sendButtonIcon, DEFAULT_VALUE)
            style.sendButtonBackground =
                typedArray.getResourceId(R.styleable.ChatBox_sendButtonBackground, DEFAULT_VALUE)
            style.sendButtonBgColor = typedArray.getColor(
                R.styleable.ChatBox_sendButtonBgColor,
                style.getColor(R.color.transparent)
            )
            style.sendButtonBgPressedColor = typedArray.getColor(
                R.styleable.ChatBox_sendButtonBgPressedColor,
                style.getColor(R.color.transparent)
            )
            style.sendButtonBgDisabledColor = typedArray.getColor(
                R.styleable.ChatBox_sendButtonBgDisabledColor,
                style.getColor(R.color.transparent)
            )
            style.sendButtonIconColor = typedArray.getColor(
                R.styleable.ChatBox_sendButtonIconColor,
                style.getColor(R.color.colorPrimaryDark)
            )
            style.sendButtonIconPressedColor = typedArray.getColor(
                R.styleable.ChatBox_sendButtonIconPressedColor,
                style.getColor(R.color.colorAccent)
            )
            style.sendButtonIconDisabledColor = typedArray.getColor(
                R.styleable.ChatBox_sendButtonIconDisabledColor,
                style.getColor(R.color.warm_grey)
            )
            style.sendButtonWidth = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_sendButtonWidth,
                style.getDimension(R.dimen.input_button_width)
            )
            style.sendButtonHeight = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_sendButtonHeight,
                style.getDimension(R.dimen.input_button_height)
            )
            style.sendButtonMargin = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_sendButtonMargin,
                style.getDimension(R.dimen.input_button_margin)
            )


            /* attachment */
            style.showAttachmentButton =
                typedArray.getBoolean(
                    R.styleable.ChatBox_showAttachmentButton,
                    CHAT_BOX_ICON_DEFAULT_VISIBILITY
                )
            style.attachmentButtonIcon =
                typedArray.getResourceId(R.styleable.ChatBox_attachmentButtonIcon, DEFAULT_VALUE)
            style.attachmentButtonBgColor = typedArray.getColor(
                R.styleable.ChatBox_attachmentButtonBgColor,
                style.getColor(R.color.transparent)
            )
            style.attachmentButtonBgPressedColor = typedArray.getColor(
                R.styleable.ChatBox_attachmentButtonBgPressedColor,
                style.getColor(R.color.transparent)
            )
            style.attachmentButtonBgDisabledColor = typedArray.getColor(
                R.styleable.ChatBox_attachmentButtonBgDisabledColor,
                style.getColor(R.color.transparent)
            )
            style.attachmentButtonIconColor = typedArray.getColor(
                R.styleable.ChatBox_attachmentButtonIconColor,
                style.getColor(R.color.warm_grey)
            )
            style.attachmentButtonIconPressedColor = typedArray.getColor(
                R.styleable.ChatBox_attachmentButtonIconPressedColor,
                style.getColor(R.color.colorPrimaryDark)
            )
            style.attachmentButtonIconDisabledColor = typedArray.getColor(
                R.styleable.ChatBox_attachmentButtonIconDisabledColor,
                style.getColor(R.color.warm_grey)
            )
            style.attachmentButtonWidth = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_attachmentButtonWidth,
                style.getDimension(R.dimen.input_button_width)
            )
            style.attachmentButtonHeight = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_attachmentButtonHeight,
                style.getDimension(R.dimen.input_button_height)
            )
            style.attachmentButtonMargin = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_attachmentButtonMargin,
                style.getDimension(R.dimen.input_button_margin)
            )
            style.attachmentButtonBackground = typedArray.getResourceId(
                R.styleable.ChatBox_attachmentButtonBackground,
                DEFAULT_VALUE
            )


            /* emoji */
            style.showEmojiButton =
                typedArray.getBoolean(
                    R.styleable.ChatBox_showEmojiButton,
                    CHAT_BOX_ICON_DEFAULT_VISIBILITY
                )
            style.emojiButtonIcon =
                typedArray.getResourceId(R.styleable.ChatBox_emojiButtonIcon, DEFAULT_VALUE)
            style.emojiButtonBackground =
                typedArray.getResourceId(R.styleable.ChatBox_emojiButtonBackground, DEFAULT_VALUE)
            style.emojiButtonBgColor = typedArray.getColor(
                R.styleable.ChatBox_emojiButtonBgColor,
                style.getColor(R.color.transparent)
            )
            style.emojiButtonBgPressedColor = typedArray.getColor(
                R.styleable.ChatBox_emojiButtonBgPressedColor,
                style.getColor(R.color.transparent)
            )
            style.emojiButtonBgDisabledColor = typedArray.getColor(
                R.styleable.ChatBox_emojiButtonBgDisabledColor,
                style.getColor(R.color.transparent)
            )
            style.emojiButtonIconColor = typedArray.getColor(
                R.styleable.ChatBox_emojiButtonIconColor,
                style.getColor(R.color.warm_grey)
            )
            style.emojiButtonIconPressedColor = typedArray.getColor(
                R.styleable.ChatBox_emojiButtonIconPressedColor,
                style.getColor(R.color.colorPrimaryDark)
            )
            style.emojiButtonIconDisabledColor = typedArray.getColor(
                R.styleable.ChatBox_emojiButtonIconDisabledColor,
                style.getColor(R.color.warm_grey)
            )
            style.emojiButtonWidth = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_emojiButtonWidth,
                style.getDimension(R.dimen.input_button_width)
            )
            style.emojiButtonHeight = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_emojiButtonHeight,
                style.getDimension(R.dimen.input_button_height)
            )
            style.emojiButtonMargin = typedArray.getDimensionPixelSize(
                R.styleable.ChatBox_emojiButtonMargin,
                style.getDimension(R.dimen.input_button_margin)
            )


            /* input text */
            style.inputText = typedArray.getString(R.styleable.ChatBox_inputText) ?: ""
            style.inputMaxLines = typedArray.getInt(
                R.styleable.ChatBox_inputMaxLines,
                DEFAULT_MAX_LINES
            )
            style.inputHint = typedArray.getString(R.styleable.ChatBox_inputHint)
                ?: style.getString(R.string.chat_box_hint)
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
            style.inputBackground = typedArray.getDrawable(R.styleable.ChatBox_inputBackground)
            style.inputCursorDrawable =
                typedArray.getDrawable(R.styleable.ChatBox_inputCursorDrawable)


            /* record button */
            style.showRecordButton =
                typedArray.getBoolean(
                    R.styleable.ChatBox_showRecordButton,
                    CHAT_BOX_ICON_DEFAULT_VISIBILITY
                )
            style.recordButtonIcon =
                typedArray.getResourceId(R.styleable.ChatBox_recordButtonIcon, DEFAULT_VALUE)


            /* typing status */
            style.delayTypingStatus = typedArray.getInt(
                R.styleable.ChatBox_delayTypingStatus,
                DEFAULT_DELAY_TYPING_STATUS
            )

            typedArray.recycle()

            return style
        }
    }


    fun getSendButtonMargin(): Int {
        return sendButtonMargin
    }

    fun getSendButtonWidth(): Int {
        return sendButtonWidth
    }

    fun getSendButtonHeight(): Int {
        return sendButtonHeight
    }

    fun getInputMaxLines(): Int {
        return inputMaxLines
    }

    fun getInputHint(): String {
        return inputHint
    }

    fun getInputText(): String {
        return inputText
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

    fun getInputBackground(): Drawable? {
        return inputBackground
    }

    fun getInputCursorDrawable(): Drawable? {
        return inputCursorDrawable
    }

    fun getDelayTypingStatus(): Int {
        return delayTypingStatus
    }

    fun getEmojiButtonWidth(): Int {
        return emojiButtonWidth
    }

    fun getEmojiButtonHeight(): Int {
        return emojiButtonHeight
    }

    fun getEmojiButtonMargin(): Int {
        return emojiButtonMargin
    }

    fun getAttachmentButtonWidth(): Int {
        return attachmentButtonWidth
    }

    fun getAttachmentButtonHeight(): Int {
        return attachmentButtonHeight
    }

    fun getAttachmentButtonMargin(): Int {
        return attachmentButtonMargin
    }

    fun isShowingEmojiButton(): Boolean {
        return showEmojiButton
    }

    fun isShowingRecordButton(): Boolean {
        return showRecordButton
    }


    fun isShowingAttachmentButton(): Boolean {
        return showAttachmentButton
    }

    fun getAttachmentButtonBackground(): Drawable? {
        return if (attachmentButtonBackground == DEFAULT_VALUE) {
            getSelector(
                attachmentButtonBgColor, attachmentButtonBgPressedColor,
                attachmentButtonBgDisabledColor, R.drawable.mask
            )
        } else {
            getDrawable(attachmentButtonBackground)
        }
    }

    fun getAttachmentButtonIcon(): Drawable? {
        return if (attachmentButtonIcon == DEFAULT_VALUE) {
            getSelector(
                attachmentButtonIconColor, attachmentButtonIconPressedColor,
                attachmentButtonIconDisabledColor, R.drawable.ic_attachment
            )
        } else {
            getDrawable(attachmentButtonIcon)
        }
    }


    fun getEmojiButtonBackground(): Drawable? {
        return if (emojiButtonBackground == -1) {
            getSelector(
                emojiButtonBgColor, emojiButtonBgPressedColor,
                emojiButtonBgDisabledColor, R.drawable.mask
            )
        } else {
            getDrawable(emojiButtonBackground)
        }
    }

    fun getEmojiButtonIcon(): Drawable? {
        return if (emojiButtonIcon == DEFAULT_VALUE) {
            getSelector(
                emojiButtonIconColor, emojiButtonIconPressedColor,
                emojiButtonIconDisabledColor, R.drawable.ic_emoji
            )
        } else {
            getDrawable(emojiButtonIcon)
        }
    }

    fun getSendButtonBackground(): Drawable? {
        return if (sendButtonBackground == DEFAULT_VALUE) {
            getSelector(
                sendButtonBgColor, sendButtonBgPressedColor,
                sendButtonBgDisabledColor, R.drawable.mask
            )
        } else {
            getDrawable(sendButtonBackground)
        }
    }

    fun getSendButtonIcon(): Drawable? {
        return if (sendButtonIcon == DEFAULT_VALUE) {
            getSelector(
                sendButtonIconColor, sendButtonIconPressedColor,
                sendButtonIconDisabledColor, R.drawable.ic_send
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