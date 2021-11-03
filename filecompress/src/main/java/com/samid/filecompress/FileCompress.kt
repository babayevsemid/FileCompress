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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import android.R.attr.x
import android.R.attr.x
import android.annotation.SuppressLint


class FileCompress private constructor() {
    private lateinit var filesFolder: File
    private lateinit var bitmap: Bitmap

    private var qualityList = arrayListOf<Int>()
    private var previousStream: ByteArrayOutputStream? = null
    private var fileSize = 0f
    private var maxSize = 0

    private fun createFilesFolder(context: Context) {
        filesFolder = File(context.externalCacheDir, ".FileCompress")
        filesFolder.mkdirs()
    }

    fun compress(file: File, maxSize: Int): File {
        bitmap = getBitmap(file.path)

        this.maxSize = maxSize
        this.fileSize = sizeOf(bitmap)

        Log.d("FileCompress-OldSize", fileSize.toString());

        return save()
    }

    private fun save(): File {
        val newFile = File(filesFolder, "${System.currentTimeMillis()}.jpg")

        val stream = binarySearchQuality(qualityList, 0, qualityList.size - 1)
        stream?.writeTo(FileOutputStream(newFile))

        Log.d("FileCompress-NewSize", (newFile.length() / 1024).toString());
        return newFile
    }

    private fun binarySearchQuality(
        qualityList: List<Int>,
        left: Int,
        right: Int
    ): ByteArrayOutputStream? {
        val mid = left + (right - left) / 2

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, mid, stream)
        val currSize = stream.toByteArray().size / 1024

        if (currSize < maxSize)
            previousStream = stream

        if (currSize in maxSize - 20..maxSize)
            return stream
        else if (left == right)
            return if (currSize <= maxSize) stream else previousStream

        return if (currSize > maxSize)
            binarySearchQuality(qualityList, left, mid - 1)
        else
            binarySearchQuality(qualityList, mid + 1, right)
    }

    private fun sizeOf(data: Bitmap) = data.byteCount / 10000f

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

    fun deleteCompressedFiles() {
        GlobalScope.launch {
            filesFolder.listFiles()?.forEach {
                it.delete()
            }
        }
    }

    private object HOLDER {
        val INSTANCE = FileCompress()
    }

    companion object {
        val instance: FileCompress by lazy { HOLDER.INSTANCE }

        fun init(context: Context) {
            instance.createFilesFolder(context)

            for (i in 0..100)
                instance.qualityList.add(i)
        }
    }
}