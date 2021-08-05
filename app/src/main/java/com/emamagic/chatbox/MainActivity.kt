package com.emamagic.chatbox

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.emamagic.chat_box.ChatBox
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), ChatBox.InputListener, ChatBox.TypingListener {

    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chat_box.init(this)
        chat_box.setInputListener(this)
        chat_box.setTypingListener(this)
        chat_box.setUriForCamera(FileHelper.getUriWithContentScheme(this, createImageFile()))

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                ChatBox.PICK_AUDIO_REQUEST_ID -> { toasty("audio") }
                ChatBox.PICK_CONTACT_REQUEST_ID -> { toasty("contact") }
                ChatBox.PICK_DOCUMENT_REQUEST_ID -> { toasty("document") }
                ChatBox.PICK_GALLERY_REQUEST_ID -> { toasty("gallery") }
                ChatBox.TAKE_PHOTO_REQUEST_ID -> { toasty("camera") }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

}