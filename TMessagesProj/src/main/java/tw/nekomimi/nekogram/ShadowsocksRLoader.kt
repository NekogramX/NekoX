package tw.nekomimi.nekogram

import android.util.Base64
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import java.io.File
import java.util.*
import kotlin.properties.Delegates

class ShadowsocksRLoader {

    lateinit var bean: Bean
    var port by Delegates.notNull<Int>()
    var shadowsocksProcess: GuardedProcessPool? = null

    fun initConfig(bean: Bean, port: Int) {

        this.bean = bean
        this.port = port

    }

    fun start() {

        stop()

        val cacheCfg = File(ApplicationLoader.applicationContext.cacheDir,"ssr_cfg_${bean.hash}.json")

        cacheCfg.writeText(bean.toJson().toString())

        shadowsocksProcess = GuardedProcessPool {

            FileLog.e(it)

        }.apply {

            start(listOf("${ApplicationLoader.applicationContext.applicationInfo.nativeLibraryDir}/libssr-local.so",
                    "-b","127.0.0.1",
                    "--host", bean.host,
                    "-t", "600",
                    "-c", cacheCfg.path,
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
            var protocol: String = "origin",
            var protocol_param: String = "",
            var obfs: String = "plain",
            var obfs_param: String = "",
            var method: String = "aes-256-cfb"
    ) {

        val hash = (host + remotePort + password + protocol + obfs + method).hashCode()

        init {

            if (method !in methods) error("method $method not supported")
            if (protocol !in protocols) error("protocol $protocol not supported")
            if (obfs !in obfses) error("obfs $obfs not supported")

        }

        fun toJson(): JSONObject = JSONObject().apply {
            put("server", host)
            put("server_port", remotePort)
            put("password", password)
            put("method", method)
            put("protocol", protocol)
            put("protocol_param", protocol_param)
            put("obfs", obfs)
            put("obfs_param", obfs_param)
            put("remarks", "nekox-cache")
            put("route", "all")
            put("remote_dns", "8.8.8.8:53")
            put("ipv6", true)
            put("metered", false)
            put("proxy_apps", JSONObject().apply {
                put("enabled", false)
            })
            put("udpdns", false)
        }

        companion object {

            private val pattern_ssr = "(?i)ssr://([A-Za-z0-9_=-]+)".toRegex()
            private val decodedPattern_ssr = "(?i)^((.+):(\\d+?):(.*):(.+):(.*):(.+)/(.*))".toRegex()
            private val decodedPattern_ssr_obfsparam = "(?i)(.*)[?&]obfsparam=([A-Za-z0-9_=-]*)(.*)".toRegex()
            private val decodedPattern_ssr_protocolparam = "(?i)(.*)[?&]protoparam=([A-Za-z0-9_=-]*)(.*)".toRegex()

            private fun base64Decode(data: String) = String(Base64.decode(data.replace("=", ""), Base64.URL_SAFE), Charsets.UTF_8)

            fun findAllSSRUrls(data: CharSequence) = pattern_ssr.findAll(data).map {
                val uri = base64Decode(it.groupValues[1])
                try {
                    val match = decodedPattern_ssr.matchEntire(uri)
                    if (match != null) {
                        val profile = Bean()
                        profile.host = match.groupValues[2].toLowerCase(Locale.ENGLISH)
                        profile.remotePort = match.groupValues[3].toInt()
                        profile.protocol = match.groupValues[4].toLowerCase(Locale.ENGLISH)
                        profile.method = match.groupValues[5].toLowerCase(Locale.ENGLISH)
                        profile.obfs = match.groupValues[6].toLowerCase(Locale.ENGLISH)
                        profile.password = base64Decode(match.groupValues[7])

                        val match1 = decodedPattern_ssr_obfsparam.matchEntire(match.groupValues[8])
                        if (match1 != null) profile.obfs_param = base64Decode(match1.groupValues[2])

                        val match2 = decodedPattern_ssr_protocolparam.matchEntire(match.groupValues[8])
                        if (match2 != null) profile.protocol_param = base64Decode(match2.groupValues[2])

                        profile
                    } else {
                        null
                    }
                } catch (e: IllegalArgumentException) {
                    error("Invalid SSR URI: ${it.value}")
                    null
                }
            }.filterNotNull().toMutableSet()

            fun parse(url: String): Bean {

                return findAllSSRUrls(url).first()

            }

        }

        override fun toString(): String {

            val flags = Base64.NO_PADDING or Base64.URL_SAFE or Base64.NO_WRAP
            return "ssr://" + Base64.encodeToString("%s:%d:%s:%s:%s:%s/?obfsparam=%s&protoparam=%s".format(Locale.ENGLISH, host, remotePort, protocol, method, obfs,
                    Base64.encodeToString("%s".format(Locale.ENGLISH, password).toByteArray(), flags),
                    Base64.encodeToString("%s".format(Locale.ENGLISH, obfs_param).toByteArray(), flags),
                    Base64.encodeToString("%s".format(Locale.ENGLISH, protocol_param).toByteArray(), flags)).toByteArray(), flags)
        }

    }

    companion object {

        val methods = arrayOf(

                "none",
                "table",
                "rc4",
                "rc4-md5",
                "rc4-md5-6",
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
                "chacha20-ietf"

        )

        val protocols = arrayOf(
                "origin",
                "verify_simple",
                "verify_sha1",
                "auth_sha1",
                "auth_sha1_v2",
                "auth_sha1_v4",
                "auth_aes128_sha1",
                "auth_aes128_md5",
                "auth_chain_a",
                "auth_chain_b"
        )

        val obfses = arrayOf(
                "plain",
                "http_simple",
                "http_post",
                "tls_simple",
                "tls1.2_ticket_auth"
        )

    }

}