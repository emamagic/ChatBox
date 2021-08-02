package com.emamagic.chat_box

interface OnRecordListener {
    fun onStart()
    fun onCancel()
    fun onFinish(recordTime: Long, limitReached: Boolean)
    fun onLessThanSecond()
}