package com.emamagic.chatbox

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.emamagic.chat_box.ChatBox
import com.emamagic.chat_box.ChatBoxPicker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), ChatBox.InputListener, ChatBox.TypingListener, ChatBox.AttachmentsListener {

    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chat_box.init(this)
        chat_box.setInputListener(this)
        chat_box.setTypingListener(this)
        chat_box.setAttachmentListener(this)
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
                ChatBox.PICK_AUDIO_REQUEST_ID -> {
                    toasty("audio")
                }
                ChatBox.PICK_CONTACT_REQUEST_ID -> {
                    toasty("contact")
                }
                ChatBox.PICK_DOCUMENT_REQUEST_ID -> {
                    toasty("document")
                }
                ChatBox.PICK_GALLERY_REQUEST_ID -> {
                    toasty("gallery")
                }
                ChatBox.TAKE_PHOTO_REQUEST_ID -> {
                    toasty("camera")
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setMultiShowButton() {


        val bottomSheetDialogFragment = ChatBoxPicker.Builder(
            this@MainActivity,
            imageProvider = { imageView, uri ->
                Glide.with(this@MainActivity)
                    .load(uri)
                    .into(imageView)
            },
            onMultiImageSelectedListener = { list: ArrayList<Uri> ->
                showUriList(list)
            },
            peekHeight = 1600,
            showTitle = false,
            completeButtonText = "Done",
            emptySelectionText = "No Selection"
        )
            .create(this@MainActivity)
        bottomSheetDialogFragment.show(supportFragmentManager)

    }
    private fun showUriList(uriList: ArrayList<Uri>) {
        // Remove all views before
        // adding the new ones.
        selected_photos_container.removeAllViews()

        iv_image.visibility = View.GONE
        selected_photos_container.visibility = View.VISIBLE

        val wdpx =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics)
                .toInt()
        val htpx =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics)
                .toInt()


        for (uri in uriList) {

            val imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, selected_photos_container, false)
            val thumbnail = imageHolder.findViewById(R.id.media_image) as ImageView

            Glide.with(this)
                .load(uri.toString())
                .into(thumbnail)

            selected_photos_container.addView(imageHolder)

            thumbnail.layoutParams = FrameLayout.LayoutParams(wdpx, htpx)
        }
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

    override fun onAddAttachments() {
        setMultiShowButton()
    }

}