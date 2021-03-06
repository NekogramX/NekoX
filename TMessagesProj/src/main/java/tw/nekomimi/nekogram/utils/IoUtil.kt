package tw.nekomimi.nekogram.utils

import java.io.File
import java.io.InputStream
import java.io.OutputStream

object IoUtil {

    @JvmStatic
    fun copy(inS: InputStream,outS: OutputStream) = inS.copyTo(outS)

    @JvmStatic
    fun copy(inS: InputStream,outF: File) {

        FileUtil.initFile(outF)

        outF.outputStream().use {

            inS.copyTo(it)

        }

    }

}