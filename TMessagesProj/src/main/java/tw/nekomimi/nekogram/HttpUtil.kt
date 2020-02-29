package tw.nekomimi.nekogram

import okhttp3.OkHttpClient
import okhttp3.Request

object HttpUtil {

    val okhttpClient = OkHttpClient().newBuilder().build()

    @JvmStatic
    operator fun get(url: String): String {

        val request = Request.Builder().url(url).build()

        okhttpClient.newCall(request).execute().apply {

            val body = body()

            return body?.string() ?: error("HTTP ERROR ${code()}")

        }

    }
}