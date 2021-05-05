package com.samid.filecompressing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.samid.filecompress.FileCompress
import com.semid.filechooser.FileChooserActivity
import com.semid.filechooser.FileTypeEnum
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val img = findViewById<ImageView>(R.id.img)

        val compress = FileCompress(applicationContext)

        val fileChooser = FileChooserActivity(this)
        fileChooser.fileLiveData.observe(this) {
            lifecycleScope.launch {
                val file = compress.compress(File(it.path), 200)

                Glide.with(applicationContext)
                        .load(file)
                        .into(img)
            }
        }
        fileChooser.requestFile(FileTypeEnum.CHOOSE_PHOTO)
    }
}