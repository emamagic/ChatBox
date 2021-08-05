package com.emamagic.chatbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.emamagic.chat_box.ChatBox
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ChatBox.InputListener, ChatBox.TypingListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chat_box.setInputListener(this, this)
        chat_box.setTypingListener(this)

    }

    override fun onSubmit(input: String) {
        toasty(input)
    }


    private fun toasty(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStartTyping() {
        Log.e("MainActivity", "onStartTyping: ")
    }

    override fun onStopTyping() {
        Log.e("MainActivity", "onStopTyping: ")
    }

}