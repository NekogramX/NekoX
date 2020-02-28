package tw.nekomimi.nekogram

import java.io.File

object FileUtil {

    @JvmStatic
    fun readUtf8String(file: File) = file.readText()

    @JvmStatic
    fun writeUtf8String(text: String, save: File) = save.writeText(text)


}