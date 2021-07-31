package com.emamagic.message_input

import android.text.Spannable
import android.text.SpannableString
import java.util.*

data class MessageFull(
    val id: String,
    val createAt: Long = 0,
    val userId: String,
    val conversationId: String,
    val threadRootId: String? = null,
    val text: String? = null,
    val type: String? = null,
    val pendingMessageId: String? = null,
    val textModified: SpannableString? = null,
    val forwardedUser: String? = null,
        val state: String,
    val directMessageUserName: String? = null,
    val directMessageTextModified: SpannableString? = null,
    // this below field used for divar group
    val timelineLabel: Spannable? = null,
    val workspaceId: String? = null,
    val workspaceDisplayName: String? = null,
    val workspaceIconHash: String? = null,
    val duration: String? = null,
    val labelColor: String? = null,
    val timelineLabel2: String? = null
)
