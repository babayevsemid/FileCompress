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
    implementation 'com.github.babayevsemid:FileCompress:1.1.2' 
}
```
### Use

```  
val file=....
val newFile = FileCompress.instance.compress(file, 500) // 480..500 KB
//.compress(file, 500,10 //maxSizeExtra optional ex. 504 kb 480..510 KB)

``` 
 
### Delete the created files when the application is created
```
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        FileCompress.init(this.applicationContext)
        FileCompress.instance.deleteCompressedFiles()
    }
}
``` 
