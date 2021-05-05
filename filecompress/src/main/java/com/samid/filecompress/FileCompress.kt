package com.samid.filecompress

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream


class FileCompress(private val context: Context) {
    suspend fun compress(file: File, maxSize: Int): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val resizedBitmap = getResizedBitmap(bitmap, maxSize)

        return save(resizedBitmap)
    }

    private fun save(bitmap: Bitmap): File {
        val folder = File(context.externalCacheDir, ".FileCompress")
        folder.mkdirs()

        val newFile = File(folder, "${System.currentTimeMillis()}.jpg")

        try {
            val os = FileOutputStream(newFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os)
            os.flush()
            os.close()
            Log.e("uriImgSize", (newFile.length() / 1024).toString() + "")
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }

        return newFile
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}