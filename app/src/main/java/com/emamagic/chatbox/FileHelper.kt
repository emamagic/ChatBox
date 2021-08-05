package com.emamagic.chatbox

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Pair
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.*


object FileHelper {
    private const val AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider"
    private const val CACHE_PATH = "cache"
    private const val LIMOO_DIRECTORY_PATH = "ChatBox"
    private val LIMOO_STORAGE_DIRECTORY =
        File(Environment.getExternalStorageDirectory(), LIMOO_DIRECTORY_PATH)


    /**
     * @return using FileProvider returns a uri with content:// scheme
     */
    @JvmStatic
    fun getUriWithContentScheme(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, AUTHORITY, file)
    }


    /**
     * open a temp file in cache and resolve the uri from content resolver and update attachment uri
     * with new file in cache
     *
     * @param uri uri of the incoming file
     * @return saved image file.
     */
    @JvmStatic
    fun getFileFromUri(uri: Uri, name: String, mimeType: String, context: Context): Pair<File, Uri>? {
        var `is`: InputStream? = null
        if (uri.authority != null) {
            try {
                `is` = context.contentResolver.openInputStream(uri)
                val savedFile = getNewFileInDirectory(name, context.cacheDir)
                return if (isImageType(mimeType) && !isGif(mimeType)) {
                    Pair(saveImageReturnFile(`is`, savedFile), getUriWithContentScheme(context, savedFile))
                } else {
                    Pair(
                        saveDocumentReturnFile(`is`, savedFile),
                        getUriWithContentScheme(context, savedFile)
                    )
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    fun isImageType(mimeType: String?): Boolean {
        return null != mimeType && mimeType.startsWith("image/")
    }
    fun isGif(mimeType: String): Boolean {
        return !TextUtils.isEmpty(mimeType) && mimeType.trim { it <= ' ' } == "image/gif"
    }

    private fun saveDocumentReturnFile(`is`: InputStream?, savedFile: File): File {
        var fos: FileOutputStream? = null
        try {
            if (savedFile.exists()) {
                savedFile.delete()
            }
            fos = FileOutputStream(savedFile)
            val buffer = ByteArray(1024) //Set buffer type
            var len1 = 0 //init length
            while (`is`!!.read(buffer).also { len1 = it } != -1) {
                fos.write(buffer, 0, len1) //Write new file
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                    `is`!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return savedFile
    }

    private fun saveImageReturnFile(`is`: InputStream?, savedImage: File): File {
        val bitmap = BitmapFactory.decodeStream(`is`)
        var fos: FileOutputStream? = null
        if (savedImage.exists()) {
            savedImage.delete()
        }
        try {
            val bytes = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, bytes)
            fos = FileOutputStream(savedImage.path)
            fos.write(bytes.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
            }
            if (fos != null) {
                try {
                    fos.close()
                    `is`!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return savedImage
    }

    @JvmStatic
    fun getNewFileInDirectory(name: String, directory: File): File {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw RuntimeException("can't make directories")
            }
        }
        var file = File(directory, name)
        if (!file.exists()) return file
        val firstIndexOfDot = name.indexOf('.')
        var firstHalf = name
        var secondHalf = ""
        if (firstIndexOfDot != -1) {
            firstHalf = name.substring(0, firstIndexOfDot)
            secondHalf = name.substring(firstHalf.length)
        }
        var fileNumber = 0
        while (file.exists()) {
            fileNumber++
            file = File(directory, "$firstHalf ($fileNumber)$secondHalf")
        }
        return file
    }

    @JvmStatic
    fun getNewFile(name: String): File {
        return getNewFileInDirectory(name, LIMOO_STORAGE_DIRECTORY)
    }


}