# FileCompress  

### Installation

Add this to your ```build.gradle``` file

```
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    implementation 'com.github.babayevsemid:FileCompress:1.0.0' 
}
```
### Use

```
val compress = FileCompress(applicationContext)

val file=....
val newFile = compress.compress(file, 500) //500 KB

``` 
 
### Delete the created files when the application is created
```
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        FileCompress.deleteCompressedFiles(this)
    }
}
``` 
