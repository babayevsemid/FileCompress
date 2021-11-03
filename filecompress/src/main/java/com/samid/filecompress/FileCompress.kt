package com.samid.filecompress

import android.R.attr
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
import android.os.Build

import android.annotation.TargetApi
import java.io.ByteArrayOutputStream
import android.R.attr.bitmap


class FileCompress(private val context: Context) {
    private var fileSize = 0f
    private var maxSize = 0

    fun compress(file: File, maxSize: Int): File {
        val bitmap = getBitmap(file.path)

        this.maxSize = maxSize
        this.fileSize = sizeOf(bitmap)

        Log.d("FileCompress-FileSize", fileSize.toString());

        return save(bitmap)
    }

    private fun sizeOf(data: Bitmap) = data.byteCount / 10000f

    private fun save(bitmap: Bitmap): File {
        val folder = createHolder()

        val newFile = File(folder, "${System.currentTimeMillis()}.jpg")


        var currSize: Int
        var currQuality = 100

        do {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, currQuality, stream)
            currSize = stream.toByteArray().size / 1024

            currQuality -= 2

            if (currSize < maxSize)
                stream.writeTo(FileOutputStream(newFile))
        } while (currSize >= maxSize)

        Log.e("FileCompress-MewFile", (newFile.length() / 1024).toString());
        return newFile
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