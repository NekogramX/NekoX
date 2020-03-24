package tw.nekomimi.nekogram

import android.util.Base64
import com.v2ray.ang.V2RayConfig.SS_PROTOCOL
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import java.io.File
import java.io.FileDescriptor
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class ShadowsocksLoader {

    lateinit var bean: Bean
    var port by Delegates.notNull<Int>()
    var shadowsocksProcess: GuardedProcessPool? = null

    fun initConfig(bean: Bean,port: Int) {

        this.bean = bean
        this.port = port

    }

    fun start() {

        stop()

        shadowsocksProcess = GuardedProcessPool {

            FileLog.e(it)

        }.apply {

            start(listOf("${ApplicationLoader.applicationContext.applicationInfo.nativeLibraryDir}/libss-local.so",
                    "-s", bean.host,
                    "-p", bean.remotePort.toString(),
                    "-k", bean.password,
                    "-m", bean.method,
                    "-l", port.toString()))

        }

    }

    fun stop() {

        if (shadowsocksProcess != null) {

            runCatching {

                runBlocking { shadowsocksProcess!!.close(this) }

            }

        }

    }

    data class Bean(
            var host: String = "",
            var remotePort: Int = 443,
            var password: String = "",
            var method: String = "aes-256-cfb"
    ) {

        init {

            if (method !in methods) error("method $method not supported")

        }

        companion object {

            fun parse(url: String): Bean {

                if (url.contains("@")) {

                    // ss-android style

                    val link = url.replace("ss://","https://").toHttpUrlOrNull() ?: error("invalid ss-android link $url")

                    val methodAndPswd = Base64.decode(link.username, Base64.DEFAULT).toString()

                    return Bean(
                            link.host,
                            link.port,
                            methodAndPswd.substringAfter(":"),
                            methodAndPswd.substringBefore(":")
                    )

                } else {

                    // v2rayNG style

                    var v2Url = url

                    if (v2Url.contains("#")) v2Url = v2Url.substringBefore("#")

                    val link = (SS_PROTOCOL + Base64.decode(v2Url.substringAfter(SS_PROTOCOL), Base64.DEFAULT)).replace("ss://","https://").toHttpUrlOrNull() ?: error("invalid v2rayNG link $url")

                    return Bean(
                            link.host,
                            link.port,
                            link.password,
                            link.username
                    )

                }

            }

        }

        override fun toString(): String {

           return "ss://" + Base64.encodeToString(("$method:$password").toByteArray(), Base64.DEFAULT) + "@$host:$remotePort"

        }

    }

    companion object {

        val methods = arrayOf(

                "rc4-md5",
                "aes-128-cfb",
                "aes-192-cfb",
                "aes-256-cfb",
                "aes-128-ctr",
                "aes-192-ctr",
                "aes-256-ctr",
                "bf-cfb",
                "camellia-128-cfb",
                "camellia-192-cfb",
                "camellia-256-cfb",
                "salsa20",
                "chacha20",
                "chacha20-ietf",
                "aes-128-gcm",
                "aes-192-gcm",
                "aes-256-gcm",
                "chacha20-ietf-poly1305",
                "xchacha20-ietf-poly1305"
        )

    }

}