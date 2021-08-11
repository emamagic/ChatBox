package com.emamagic.chat_box

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.emamagic.chat_box.view.SquareFrameLayout
import com.emamagic.chat_box.view.SquareImageView
import java.io.File

@SuppressLint("Range")
class ImageGalleryAdapter(private var context: Context, private var builder: ChatBoxPicker.Builder?) :
    RecyclerView.Adapter<ImageGalleryAdapter.GalleryViewHolder>() {

    private var pickerTiles: ArrayList<PickerTile> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var selectedUriList: ArrayList<Uri>

    init {
        selectedUriList = ArrayList()

        if (builder?.showCamera == true) {
            pickerTiles.add(PickerTile(tileType = PickerTile.CAMERA))
        }

        if (builder?.showGallery == true) {
            pickerTiles.add(PickerTile(tileType = PickerTile.GALLERY))
        }

        var imageCursor: Cursor? = null
        try {
            val columns =
                arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION)
            val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"


            imageCursor = context.applicationContext.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                orderBy
            )

            if (imageCursor != null) {
                var count = 0
                while (imageCursor.moveToNext() && count < (builder?.previewMaxCount ?: 25)) {
                    val imageLocation =
                        imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val imageFile = File(imageLocation)
                    pickerTiles.add(PickerTile(Uri.fromFile(imageFile)))
                    count++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (imageCursor?.isClosed != true)
                imageCursor?.close()
        }
    }

    fun setSelectedUriList(selectedUriList: ArrayList<Uri>, uri: Uri) {
        this.selectedUriList = selectedUriList

        var position = -1

        var pickerTile: PickerTile
        for (i in pickerTiles.indices) {
            pickerTile = pickerTiles[i]
            if (pickerTile.isImageTile && pickerTile.imageUri == uri) {
                position = i
                break
            }
        }


        if (position > 0) {
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GalleryViewHolder(View.inflate(context, R.layout.kwik_picker_grid_item, null))

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val pickerTile = getItem(position)
        var isSelected = false
        when {
            pickerTile.isCameraTile -> {
                holder.iv_thumbnail.setBackgroundResource(builder?.cameraTileBackgroundResId ?: 0)
                holder.iv_thumbnail.setImageDrawable(builder?.cameraTileDrawable)
            }
            pickerTile.isGalleryTile -> {
                holder.iv_thumbnail.setBackgroundResource(builder?.galleryTileBackgroundResId ?: 0)
                holder.iv_thumbnail.setImageDrawable(builder?.galleryTileDrawable)
            }
            else -> {
                val uri = pickerTile.imageUri
                builder?.imageProvider?.invoke(holder.iv_thumbnail, uri)
                isSelected = selectedUriList.contains(uri)
            }
        }


        val foregroundDrawable: Drawable? = builder?.selectedForegroundDrawable ?:
                ContextCompat.getDrawable(context, R.drawable.gallery_photo_selected)

        (holder.root as FrameLayout).foreground = if (isSelected) foregroundDrawable else null


        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(
                    holder.itemView,
                    position
                )
            }
        }
    }

    fun getItem(position: Int) = pickerTiles[position]
    override fun getItemCount() = pickerTiles.size
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    class PickerTile internal constructor(
        val imageUri: Uri? = null,
        val tileType: Int = IMAGE
    ) {

        val isImageTile: Boolean
            get() = tileType == IMAGE

        val isCameraTile: Boolean
            get() = tileType == CAMERA

        val isGalleryTile: Boolean
            get() = tileType == GALLERY

        override fun toString(): String {
            return when {
                isImageTile -> "ImageTile: " + imageUri
                isCameraTile -> "CameraTile"
                isGalleryTile -> "PickerTile"
                else -> "Invalid item"
            }
        }

        @IntDef(CAMERA, GALLERY)
        @Retention(AnnotationRetention.SOURCE)
        annotation class SpecialTileType

        companion object {
            const val IMAGE = 1
            const val CAMERA = 2
            const val GALLERY = 3
        }
    }

    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var root = view.findViewById(R.id.root) as SquareFrameLayout
        var iv_thumbnail = view.findViewById(R.id.iv_thumbnail) as SquareImageView
    }
}
