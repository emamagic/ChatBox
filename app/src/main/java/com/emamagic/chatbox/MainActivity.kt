package com.emamagic.chatbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.emamagic.chat_box.ChatBox
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ChatBox.InputListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chat_box.setInputListener(this, this)

    }

    override fun onSubmit(input: String) {
        toasty(input)
    }


    private fun toasty(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}