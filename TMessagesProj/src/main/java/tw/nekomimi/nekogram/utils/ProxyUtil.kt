package tw.nekomimi.nekogram.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import org.json.JSONArray
import org.telegram.messenger.ApplicationLoader
import java.io.ByteArrayInputStream
import java.io.File
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList


object ProxyUtil {

    @JvmStatic
    val cacheFile = File(ApplicationLoader.applicationContext.filesDir.parent, "nekox/proxy_list.json")

    @JvmStatic
    fun isVPNEnabled(): Boolean {

        val cm = ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkList: MutableList<String> = ArrayList()

        runCatching {
            Collections.list(NetworkInterface.getNetworkInterfaces()).forEach {

                if (it.isUp()) networkList.add(it.name)

            }
        }

        return networkList.contains("tun0")

    }

    @JvmStatic
    fun reloadProxyList(): Boolean {

        val ctx = ApplicationLoader.applicationContext

        cacheFile.parentFile?.mkdirs()

        runCatching {

            // 从 GITEE 主站 读取

            val list = JSONArray(HttpUtil.get("https://gitee.com/nekoshizuku/AwesomeRepo/raw/master/proxy_list.json")).toString()

            if (!cacheFile.isFile || list != cacheFile.readText()) {

                cacheFile.writeText(list)

                return true

            }

        }.recover {

            // 从 GITHUB PAGES 读取

            val list = JSONArray(HttpUtil.get("https://nekogramx.github.io/ProxyList/proxy_list.json")).toString()

            if (!cacheFile.isFile || list != cacheFile.readText()) {

                cacheFile.writeText(list)

                return true

            }

        }.recover {

            // 从 GITLAB 读取

            val list = JSONArray(HttpUtil.get("https://gitlab.com/KazamaWataru/nekox-proxy-list/-/raw/master/proxy_list.json")).toString()

            if (!cacheFile.isFile || list != cacheFile.readText()) {

                cacheFile.writeText(list)

                return true

            }

        }.recover {

            // 从 GITHUB 主站 读取

            val master = HttpUtil.getByteArray("https://github.com/NekogramX/ProxyList/archive/master.zip")

            val list = JSONArray(String(ZipUtil.read(ByteArrayInputStream(master), "ProxyList-master/proxy_list.json"))).toString()

            if (!cacheFile.isFile || list != cacheFile.readText()) {

                cacheFile.writeText(list)

                return true

            }

        }

        return false

    }

}