package tw.nekomimi.nekogram

import cn.hutool.core.util.StrUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object HttpUtil {

    val okHttpClient = OkHttpClient.Builder().build()

    @JvmStatic
    fun get(url: String): String? {

        val request = Request.Builder().url(url).build()

        val response = okHttpClient.newCall(request).execute()

        return StrUtil.utf8Str(response.body()!!.bytes())

    }

}