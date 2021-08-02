package com.emamagic.chat_box

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat

private const val DEFAULT_MAX_LINES = 5
private const val DEFAULT_DELAY_TYPING_STATUS = 1000
private const val DEFAULT_DELAY_SAVING_DRAFT = 1000
private const val DEFAULT_INT_VALUE = 0


class MessageInputStyle(context: Context, attrs: AttributeSet) : Style(context, attrs) {
    
    private var showAttachmentButton = false
    private var showEmojiButton = false
    private var showMicIcon = false

    private var attachmentButtonBackground = DEFAULT_INT_VALUE
    private var attachmentButtonDefaultBgColor = DEFAULT_INT_VALUE
    private var attachmentButtonDefaultBgPressedColor = DEFAULT_INT_VALUE
    private var attachmentButtonDefaultBgDisabledColor = DEFAULT_INT_VALUE

    private var attachmentButtonIcon = DEFAULT_INT_VALUE
    private var attachmentButtonDefaultIconColor = DEFAULT_INT_VALUE
    private var attachmentButtonDefaultIconPressedColor = DEFAULT_INT_VALUE
    private var attachmentButtonDefaultIconDisabledColor = DEFAULT_INT_VALUE

    private var attachmentButtonWidth = DEFAULT_INT_VALUE
    private var attachmentButtonHeight = DEFAULT_INT_VALUE
    private var attachmentButtonMargin = DEFAULT_INT_VALUE

    private var emojiButtonBackground = DEFAULT_INT_VALUE
    private var emojiButtonDefaultBgColor = DEFAULT_INT_VALUE
    private var emojiButtonDefaultBgPressedColor = DEFAULT_INT_VALUE
    private var emojiButtonDefaultBgDisabledColor = DEFAULT_INT_VALUE

    private var emojiButtonIcon = DEFAULT_INT_VALUE
    private var emojiButtonDefaultIconColor = DEFAULT_INT_VALUE
    private var emojiButtonDefaultIconPressedColor = DEFAULT_INT_VALUE
    private var emojiButtonDefaultIconDisabledColor = DEFAULT_INT_VALUE

    private var emojiButtonWidth = DEFAULT_INT_VALUE
    private var emojiButtonHeight = DEFAULT_INT_VALUE
    private var emojiButtonMargin = DEFAULT_INT_VALUE

    private var inputButtonBackground = DEFAULT_INT_VALUE
    private var inputButtonDefaultBgColor = DEFAULT_INT_VALUE
    private var inputButtonDefaultBgPressedColor = DEFAULT_INT_VALUE
    private var inputButtonDefaultBgDisabledColor = DEFAULT_INT_VALUE

    private var inputButtonIcon = DEFAULT_INT_VALUE
    private var inputButtonDefaultIconColor = DEFAULT_INT_VALUE
    private var inputButtonDefaultIconPressedColor = DEFAULT_INT_VALUE
    private var inputButtonDefaultIconDisabledColor = DEFAULT_INT_VALUE

    private var inputButtonWidth = DEFAULT_INT_VALUE
    private var inputButtonHeight = DEFAULT_INT_VALUE
    private var inputButtonMargin = DEFAULT_INT_VALUE

    private var inputMaxLines = DEFAULT_INT_VALUE
    private var inputHint: String = ""
    private var inputText: String = ""

    private var inputTextSize = DEFAULT_INT_VALUE
    private var inputTextColor = DEFAULT_INT_VALUE
    private var inputHintColor = DEFAULT_INT_VALUE

    private var inputBackground: Drawable? = null
    private var inputCursorDrawable: Drawable? = null

    private var inputDefaultPaddingLeft = DEFAULT_INT_VALUE
    private var inputDefaultPaddingRight = DEFAULT_INT_VALUE
    private var inputDefaultPaddingTop = DEFAULT_INT_VALUE
    private var inputDefaultPaddingBottom = DEFAULT_INT_VALUE

    private var delayTypingStatus = DEFAULT_INT_VALUE
    private var delaySavingDraft = DEFAULT_INT_VALUE

    companion object {
        @JvmStatic
        fun parse(
            context: Context,
            attrs: AttributeSet
        ): MessageInputStyle {
            val style = MessageInputStyle(context, attrs)
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MessageInput)
            style.showAttachmentButton =
                typedArray.getBoolean(R.styleable.MessageInput_showAttachmentButton, false)
            style.attachmentButtonBackground =
                typedArray.getResourceId(R.styleable.MessageInput_attachmentButtonBackground, DEFAULT_INT_VALUE)
            style.attachmentButtonDefaultBgColor = typedArray.getColor(
                R.styleable.MessageInput_attachmentButtonDefaultBgColor,
                style.getColor(R.color.transparent)
            )
            style.attachmentButtonDefaultBgPressedColor = typedArray.getColor(
                R.styleable.MessageInput_attachmentButtonDefaultBgPressedColor,
                style.getColor(R.color.transparent)
            )
            style.attachmentButtonDefaultBgDisabledColor = typedArray.getColor(
                R.styleable.MessageInput_attachmentButtonDefaultBgDisabledColor,
                style.getColor(R.color.transparent)
            )
            style.attachmentButtonIcon =
                typedArray.getResourceId(R.styleable.MessageInput_attachmentButtonIcon, DEFAULT_INT_VALUE)
            style.attachmentButtonDefaultIconColor = typedArray.getColor(
                R.styleable.MessageInput_attachmentButtonDefaultIconColor,
                style.getColor(R.color.warm_grey)
            )
            style.attachmentButtonDefaultIconPressedColor = typedArray.getColor(
                R.styleable.MessageInput_attachmentButtonDefaultIconPressedColor,
                style.getColor(R.color.colorPrimaryDark)
            )
            style.attachmentButtonDefaultIconDisabledColor = typedArray.getColor(
                R.styleable.MessageInput_attachmentButtonDefaultIconDisabledColor,
                style.getColor(R.color.warm_grey)
            )
            style.attachmentButtonWidth = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_attachmentButtonWidth,
                style.getDimension(R.dimen.input_button_width)
            )
            style.attachmentButtonHeight = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_attachmentButtonHeight,
                style.getDimension(R.dimen.input_button_height)
            )
            style.attachmentButtonMargin = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_attachmentButtonMargin,
                style.getDimension(R.dimen.input_button_margin)
            )
            style.showEmojiButton =
                typedArray.getBoolean(R.styleable.MessageInput_showEmojiButton, false)
            style.showMicIcon = typedArray.getBoolean(R.styleable.MessageInput_showMicIcon, true)
            style.emojiButtonBackground =
                typedArray.getResourceId(R.styleable.MessageInput_emojiButtonBackground, DEFAULT_INT_VALUE)
            style.emojiButtonDefaultBgColor = typedArray.getColor(
                R.styleable.MessageInput_emojiButtonDefaultBgColor,
                style.getColor(R.color.transparent)
            )
            style.emojiButtonDefaultBgPressedColor = typedArray.getColor(
                R.styleable.MessageInput_emojiButtonDefaultBgPressedColor,
                style.getColor(R.color.transparent)
            )
            style.emojiButtonDefaultBgDisabledColor = typedArray.getColor(
                R.styleable.MessageInput_emojiButtonDefaultBgDisabledColor,
                style.getColor(R.color.transparent)
            )
            style.emojiButtonIcon =
                typedArray.getResourceId(R.styleable.MessageInput_emojiButtonIcon, DEFAULT_INT_VALUE)
            style.emojiButtonDefaultIconColor = typedArray.getColor(
                R.styleable.MessageInput_emojiButtonDefaultIconColor,
                style.getColor(R.color.warm_grey)
            )
            style.emojiButtonDefaultIconPressedColor = typedArray.getColor(
                R.styleable.MessageInput_emojiButtonDefaultIconPressedColor,
                style.getColor(R.color.colorPrimaryDark)
            )
            style.emojiButtonDefaultIconDisabledColor = typedArray.getColor(
                R.styleable.MessageInput_emojiButtonDefaultIconDisabledColor,
                style.getColor(R.color.warm_grey)
            )
            style.emojiButtonWidth = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_emojiButtonWidth,
                style.getDimension(R.dimen.input_button_width)
            )
            style.emojiButtonHeight = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_emojiButtonHeight,
                style.getDimension(R.dimen.input_button_height)
            )
            style.emojiButtonMargin = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_emojiButtonMargin,
                style.getDimension(R.dimen.input_button_margin)
            )
            style.inputButtonBackground =
                typedArray.getResourceId(R.styleable.MessageInput_inputButtonBackground, DEFAULT_INT_VALUE)
            style.inputButtonDefaultBgColor = typedArray.getColor(
                R.styleable.MessageInput_inputButtonDefaultBgColor,
                style.getColor(R.color.transparent)
            )
            style.inputButtonDefaultBgPressedColor = typedArray.getColor(
                R.styleable.MessageInput_inputButtonDefaultBgPressedColor,
                style.getColor(R.color.transparent)
            )
            style.inputButtonDefaultBgDisabledColor = typedArray.getColor(
                R.styleable.MessageInput_inputButtonDefaultBgDisabledColor,
                style.getColor(R.color.transparent)
            )
            style.inputButtonIcon =
                typedArray.getResourceId(R.styleable.MessageInput_inputButtonIcon, DEFAULT_INT_VALUE)
            style.inputButtonDefaultIconColor = typedArray.getColor(
                R.styleable.MessageInput_inputButtonDefaultIconColor,
                style.getColor(R.color.colorPrimaryDark)
            )
            style.inputButtonDefaultIconPressedColor = typedArray.getColor(
                R.styleable.MessageInput_inputButtonDefaultIconPressedColor,
                style.getColor(R.color.colorAccent)
            )
            style.inputButtonDefaultIconDisabledColor = typedArray.getColor(
                R.styleable.MessageInput_inputButtonDefaultIconDisabledColor,
                style.getColor(R.color.warm_grey)
            )
            style.inputButtonWidth = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_inputButtonWidth,
                style.getDimension(R.dimen.input_button_width)
            )
            style.inputButtonHeight = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_inputButtonHeight,
                style.getDimension(R.dimen.input_button_height)
            )
            style.inputButtonMargin = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_inputButtonMargin,
                style.getDimension(R.dimen.input_button_margin)
            )
            style.inputMaxLines = typedArray.getInt(
                R.styleable.MessageInput_inputMaxLines,
                DEFAULT_MAX_LINES
            )
            style.inputHint = typedArray.getString(R.styleable.MessageInput_inputHint) ?: ""
            style.inputText = typedArray.getString(R.styleable.MessageInput_inputText) ?: ""
            style.inputTextSize = typedArray.getDimensionPixelSize(
                R.styleable.MessageInput_inputTextSize,
                style.getDimension(R.dimen.input_text_size)
            )
            style.inputTextColor = typedArray.getColor(
                R.styleable.MessageInput_inputTextColor,
                style.getColor(R.color.dark_grey_two)
            )
            style.inputHintColor = typedArray.getColor(
                R.styleable.MessageInput_inputHintColor,
                style.getColor(R.color.warm_grey_three)
            )
            style.inputBackground = typedArray.getDrawable(R.styleable.MessageInput_inputBackground)
            style.inputCursorDrawable =
                typedArray.getDrawable(R.styleable.MessageInput_inputCursorDrawable)
            style.delayTypingStatus = typedArray.getInt(
                R.styleable.MessageInput_delayTypingStatus,
                DEFAULT_DELAY_TYPING_STATUS
            )
            style.delaySavingDraft = typedArray.getInt(
                R.styleable.MessageInput_delaySavingDraft,
                DEFAULT_DELAY_SAVING_DRAFT
            )
            typedArray.recycle()
            style.inputDefaultPaddingLeft = style.getDimension(R.dimen.input_padding_left)
            style.inputDefaultPaddingRight = style.getDimension(R.dimen.input_padding_right)
            style.inputDefaultPaddingTop = style.getDimension(R.dimen.input_padding_top)
            style.inputDefaultPaddingBottom = style.getDimension(R.dimen.input_padding_bottom)
            return style
        }
    }


     fun getInputButtonMargin(): Int {
        return inputButtonMargin
    }

     fun getInputButtonWidth(): Int {
        return inputButtonWidth
    }

     fun getInputButtonHeight(): Int {
        return inputButtonHeight
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

     fun getInputDefaultPaddingLeft(): Int {
        return inputDefaultPaddingLeft
    }

     fun getInputDefaultPaddingRight(): Int {
        return inputDefaultPaddingRight
    }

     fun getInputDefaultPaddingTop(): Int {
        return inputDefaultPaddingTop
    }

     fun getInputDefaultPaddingBottom(): Int {
        return inputDefaultPaddingBottom
    }

     fun getDelayTypingStatus(): Int {
        return delayTypingStatus
    }

     fun getDelaySavingDraft(): Int {
        return delaySavingDraft
    }
    private fun getSelector(
        @ColorInt normalColor: Int, @ColorInt pressedColor: Int,
        @ColorInt disabledColor: Int, @DrawableRes shape: Int
    ): Drawable? {
        val drawable = DrawableCompat.wrap(
            getVectorDrawable(shape)!!
        ).mutate()
        DrawableCompat.setTintList(
            drawable,
            ColorStateList(
                arrayOf(
                    intArrayOf(
                        android.R.attr.state_enabled,
                        -android.R.attr.state_pressed
                    ),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
                    intArrayOf(-android.R.attr.state_enabled)
                ), intArrayOf(normalColor, pressedColor, disabledColor)
            )
        )
        return drawable
    }

     fun showAttachmentButton(): Boolean {
        return showAttachmentButton
    }

     fun getAttachmentButtonBackground(): Drawable? {
        return if (attachmentButtonBackground == -1) {
            getSelector(
                attachmentButtonDefaultBgColor, attachmentButtonDefaultBgPressedColor,
                attachmentButtonDefaultBgDisabledColor, R.drawable.mask
            )
        } else {
            getDrawable(attachmentButtonBackground)
        }
    }

     fun getAttachmentButtonIcon(): Drawable? {
        return if (attachmentButtonIcon == -1) {
            getSelector(
                attachmentButtonDefaultIconColor, attachmentButtonDefaultIconPressedColor,
                attachmentButtonDefaultIconDisabledColor, R.drawable.ic_attachment
            )
        } else {
            getDrawable(attachmentButtonIcon)
        }
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

     fun showEmojiButton(): Boolean {
        return showEmojiButton
    }

     fun isShowMicIcon(): Boolean {
        return showMicIcon
    }

     fun getEmojiButtonBackground(): Drawable? {
        return if (emojiButtonBackground == -1) {
            getSelector(
                emojiButtonDefaultBgColor, emojiButtonDefaultBgPressedColor,
                emojiButtonDefaultBgDisabledColor, R.drawable.mask
            )
        } else {
            getDrawable(emojiButtonBackground)
        }
    }

     fun getEmojiButtonIcon(): Drawable? {
        return if (emojiButtonIcon == -1) {
            getSelector(
                emojiButtonDefaultIconColor, emojiButtonDefaultIconPressedColor,
                emojiButtonDefaultIconDisabledColor, R.drawable.ic_emoji
            )
        } else {
            getDrawable(emojiButtonIcon)
        }
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

     fun getInputButtonBackground(): Drawable? {
        return if (inputButtonBackground == -1) {
            getSelector(
                inputButtonDefaultBgColor, inputButtonDefaultBgPressedColor,
                inputButtonDefaultBgDisabledColor, R.drawable.mask
            )
        } else {
            getDrawable(inputButtonBackground)
        }
    }

     fun getInputButtonIcon(): Drawable? {
        return if (inputButtonIcon == -1) {
            getSelector(
                inputButtonDefaultIconColor, inputButtonDefaultIconPressedColor,
                inputButtonDefaultIconDisabledColor, R.drawable.ic_send
            )
        } else {
            getDrawable(inputButtonIcon)
        }
    }


     fun getInputButtonEditModeIcon(): Drawable? {
        return if (inputButtonIcon == -1) {
            getSelector(
                inputButtonDefaultIconColor, inputButtonDefaultIconPressedColor,
                inputButtonDefaultIconDisabledColor, R.drawable.ic_tick
            )
        } else {
            getDrawable(inputButtonIcon)
        }
    }

     fun getInputVoiceIcon(): Drawable? {
        return if (inputButtonIcon == -1) {
            getSelector(
                inputButtonDefaultIconColor, inputButtonDefaultIconPressedColor,
                inputButtonDefaultIconDisabledColor, R.drawable.recv_ic_mic
            )
        } else {
            getDrawable(inputButtonIcon)
        }
    }
}