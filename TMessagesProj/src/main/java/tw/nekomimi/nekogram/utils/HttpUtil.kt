package tw.nekomimi.nekogram.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object HttpUtil {

    val okhttpClient = OkHttpClient().newBuilder().readTimeout(500,TimeUnit.MILLISECONDS).build()

    @JvmStatic
    fun get(url: String): String {

        val request = Request.Builder().url(url).build()

        okhttpClient.newCall(request).execute().apply {

            val body = body()

            return body?.string() ?: error("HTTP ERROR ${code()}")

        }

    }

    @JvmStatic
    fun getByteArray(url: String): ByteArray {

        val request = Request.Builder().url(url).build()

        okhttpClient.newCall(request).execute().apply {

            val body = body()

            return body?.bytes() ?: error("HTTP ERROR ${code()}")

        }

    }

}