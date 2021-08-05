package com.emamagic.chat_box

import android.animation.Animator
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Pair
import android.view.*
import android.view.animation.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow

const val PICK_GALLERY_REQUEST_ID = 1
const val PICK_DOCUMENT_REQUEST_ID = 2
const val PICK_AUDIO_REQUEST_ID = 3
const val PICK_CONTACT_REQUEST_ID = 4
class AttachmentTypeSelector(context: Context, private val activity: Activity) :
    PopupWindow(context) {
    private val imageButton: ImageView
    private val audioButton: ImageView
    private val documentButton: ImageView
    private val contactButton: ImageView
    private val cameraButton: ImageView
    private val closeButton: ImageView
    private var currentAnchor: View? = null
    fun show(anchor: View) {
        currentAnchor = anchor
        showAtLocation(anchor, Gravity.BOTTOM, 0, 0)
        contentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                contentView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                if (AnimationHelper.isAppAnimationEnabled()) {
                    animateWindowInCircular(anchor, contentView)
                } else {
                    animateWindowInTranslate(contentView)
                }
            }
        })
        if (AnimationHelper.isAppAnimationEnabled()) {
            animateButtonIn(imageButton, ANIMATION_DURATION / 2)
            animateButtonIn(cameraButton, ANIMATION_DURATION / 2)
            animateButtonIn(audioButton, ANIMATION_DURATION / 3)
            animateButtonIn(documentButton, ANIMATION_DURATION / 4)
            animateButtonIn(contactButton, 0)
            animateButtonIn(closeButton, 0)
        }
    }

    override fun dismiss() {
        if (AnimationHelper.isAppAnimationEnabled()) {
            animateWindowOutCircular(currentAnchor, contentView)
        } else {
            animateWindowOutTranslate(contentView)
        }
    }

    private fun animateButtonIn(button: View, delay: Int) {
        val animation = AnimationSet(true)
        val scale: Animation = ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f
        )
        animation.addAnimation(scale)
        animation.interpolator = OvershootInterpolator(1f)
        animation.duration = ANIMATION_DURATION.toLong()
        animation.startOffset = delay.toLong()
        button.startAnimation(animation)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateWindowInCircular(anchor: View?, contentView: View) {
        val coordinates = getClickOrigin(anchor, contentView)
        val animator = ViewAnimationUtils.createCircularReveal(
            contentView,
            coordinates.first,
            coordinates.second, 0f,
            contentView.width.coerceAtLeast(contentView.height).toFloat()
        )
        animator.duration = ANIMATION_DURATION.toLong()
        animator.start()
    }

    private fun animateWindowInTranslate(contentView: View) {
        val animation: Animation = TranslateAnimation(
            0f, 0f,
            contentView.height.toFloat(), 0f
        )
        animation.duration = ANIMATION_DURATION.toLong()
        getContentView().startAnimation(animation)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateWindowOutCircular(anchor: View?, contentView: View) {
        val coordinates = getClickOrigin(anchor, contentView)
        val animator = ViewAnimationUtils.createCircularReveal(
            getContentView(),
            coordinates.first,
            coordinates.second,
            getContentView().width.coerceAtLeast(getContentView().height).toFloat(), 0f
        )
        animator.duration = ANIMATION_DURATION.toLong()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                super@AttachmentTypeSelector.dismiss()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator.start()
    }

    private fun animateWindowOutTranslate(contentView: View) {
        val animation: Animation = TranslateAnimation(
            0f, 0f, 0f,
            (contentView.top + contentView.height).toFloat()
        )
        animation.duration = ANIMATION_DURATION.toLong()
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                super@AttachmentTypeSelector.dismiss()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        getContentView().startAnimation(animation)
    }

    private fun getClickOrigin(anchor: View?, contentView: View): Pair<Int, Int> {
        if (anchor == null) return Pair(0, 0)
        val anchorCoordinates = IntArray(2)
        anchor.getLocationOnScreen(anchorCoordinates)
        anchorCoordinates[0] += anchor.width / 2
        anchorCoordinates[1] += anchor.height / 2
        val contentCoordinates = IntArray(2)
        contentView.getLocationOnScreen(contentCoordinates)
        val x = anchorCoordinates[0] - contentCoordinates[0]
        val y = anchorCoordinates[1] - contentCoordinates[1]
        return Pair(x, y)
    }


    private inner class PropagatingClickListener(val type: Int) :
        View.OnClickListener {
        override fun onClick(v: View) {
            animateWindowOutTranslate(contentView)
            onAttachmentTypeSelected(type)
        }
    }


    private fun onAttachmentTypeSelected(type: Int) {
        when (type) {
            ADD_GALLERY -> AttachmentManager.selectGallery(
                activity,
                PICK_GALLERY_REQUEST_ID
            )
            ADD_DOCUMENT -> AttachmentManager.selectDocument(
                activity,
                PICK_DOCUMENT_REQUEST_ID
            )
            ADD_SOUND -> AttachmentManager.selectAudio(
                activity,
                PICK_AUDIO_REQUEST_ID
            )
            ADD_CONTACT_INFO -> AttachmentManager.selectContactInfo(
                activity,
                PICK_CONTACT_REQUEST_ID
            )
            TAKE_PHOTO -> {
//                val tempFileName =
//                    System.currentTimeMillis().toString() + "." + MimeTypeMap.getSingleton()
//                        .getExtensionFromMimeType(IMAGE_JPEG)
//                val captureUri = FileHelper.getUriWithContentScheme(
//                    activity,
//                    FileHelper.getNewFileInDirectory(tempFileName, activity.cacheDir)
//                )
//                if (captureUri != null) {
//                    AttachmentManager.capturePhoto(
//                        activity,
//                        TAKE_PHOTO_REQUEST_ID,
//                        captureUri
//                    )
//                }
            }
        }
    }

    private inner class CloseClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            dismiss()
        }
    }

    companion object {
        const val ADD_GALLERY = 1
        const val ADD_DOCUMENT = 2
        const val ADD_SOUND = 3
        const val ADD_CONTACT_INFO = 4
        const val TAKE_PHOTO = 5
        private const val ANIMATION_DURATION = 300
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.attachment_type_selector, null, true) as LinearLayout
        imageButton = layout.findViewById(R.id.gallery_button)
        audioButton = layout.findViewById(R.id.audio_button)
        documentButton = layout.findViewById(R.id.document_button)
        contactButton = layout.findViewById(R.id.contact_button)
        cameraButton = layout.findViewById(R.id.camera_button)
        closeButton = layout.findViewById(R.id.close_button)
        imageButton.setOnClickListener(PropagatingClickListener(ADD_GALLERY))
        audioButton.setOnClickListener(PropagatingClickListener(ADD_SOUND))
        documentButton.setOnClickListener(PropagatingClickListener(ADD_DOCUMENT))
        contactButton.setOnClickListener(PropagatingClickListener(ADD_CONTACT_INFO))
        cameraButton.setOnClickListener(PropagatingClickListener(TAKE_PHOTO))
        closeButton.setOnClickListener(CloseClickListener())
        contentView = layout
        width = LinearLayout.LayoutParams.MATCH_PARENT
        height = LinearLayout.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(BitmapDrawable())
        animationStyle = 0
        inputMethodMode = INPUT_METHOD_NOT_NEEDED
        isFocusable = true
        isTouchable = true
    }
}