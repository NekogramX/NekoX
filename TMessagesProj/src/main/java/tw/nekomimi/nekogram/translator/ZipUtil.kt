package tw.nekomimi.nekogram.translator

import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

object ZipUtil {

    @JvmStatic
    fun unzip(input: InputStream, output: File) {

        ZipInputStream(input).use { zip ->

            while (true) {

                val entry = zip.nextEntry ?: break

                val entryFile = File(output,entry.name)

                if (entry.isDirectory) {

                    entryFile.mkdirs()

                } else {

                    entryFile.outputStream().use {

                        zip.copyTo(it)

                    }

                }

            }

        }

    }

}