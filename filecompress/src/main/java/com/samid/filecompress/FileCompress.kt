package com.samid.filecompress

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max


class FileCompress(private val context: Context) {
    private var fileSize = 0L
    private var maxSize = 0

    fun compress(file: File, maxSize: Int): File {
        this.fileSize = file.length() / 1024
        this.maxSize = maxSize

        val bitmap = getBitmap(file.path)

        return save(bitmap)
    }

    private fun save(bitmap: Bitmap): File {
        val folder = createHolder()

        val newFile = File(folder, "${System.currentTimeMillis()}.jpg")

        try {
            val os = FileOutputStream(newFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, calculateQuality(), os)
            os.flush()
            os.close()
        } catch (e: Exception) {
        }

        return newFile
    }

    private fun calculateQuality(): Int {
        var quality = 100

        if (fileSize < maxSize)
            quality = 100

        if (((maxSize * 230) / fileSize) > 100)
            quality = ((maxSize * 150) / fileSize).toInt()

        if (maxSize < 1000)
            quality = ((maxSize * 230) / fileSize).toInt()

        if (maxSize >= 1000)
            quality = (maxSize * 204 / fileSize).toInt()

        if (quality > 100)
            quality = 100

        return quality
    }

    private fun createHolder(): File {
        val folder = File(context.externalCacheDir, ".FileCompress")
        folder.mkdirs()

        return folder
    }

    private fun getBitmap(picturePath: String): Bitmap {
        var myBitmap = BitmapFactory.decodeFile(picturePath)

        try {
            val exif = ExifInterface(picturePath)
            val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            val matrix = Matrix()
            when (orientation) {
                6 ->
                    matrix.postRotate(90F)
                3 ->
                    matrix.postRotate(180F)
                8 ->
                    matrix.postRotate(270F)
            }

            myBitmap =
                Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.width, myBitmap.height, matrix, true)
        } catch (e: Exception) {
        }
        return myBitmap
    }

    companion object {
        fun deleteCompressedFiles(context: Context) {
            GlobalScope.launch {
                val folder = FileCompress(context)
                    .createHolder()

                folder.listFiles()?.forEach {
                    it.delete()
                }
            }
        }
    }
}