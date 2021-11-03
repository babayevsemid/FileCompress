package com.samid.filecompressing

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.samid.filecompress.FileCompress
import com.semid.filechooser.FileChooserActivity
import com.semid.filechooser.FileTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val img = findViewById<ImageView>(R.id.img)

        FileCompress.init(applicationContext)
        FileCompress.instance.deleteCompressedFiles()

        val compress = FileCompress.instance

        val fileChooser = FileChooserActivity(this)
        fileChooser.fileLiveData.observe(this) {
            lifecycleScope.launch(Dispatchers.IO) {
                val file = compress.compress(File(it.path), 500)

                withContext(Dispatchers.Main) {
                    Glide.with(applicationContext)
                        .load(file)
                        .into(img)
                }
            }
        }

        img.setOnClickListener {
            fileChooser.requestFile(FileTypeEnum.CHOOSE_PHOTO)
        }
    }
}