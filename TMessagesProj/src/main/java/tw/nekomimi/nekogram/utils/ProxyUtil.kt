package tw.nekomimi.nekogram.utils

import android.content.Context
import android.content.Intent
import android.net.ProxyInfo
import android.text.TextUtils
import android.widget.Toast
import okhttp3.HttpUrl
import org.json.JSONArray
import org.telegram.messenger.*
import org.telegram.ui.ProxySettingsActivity
import java.io.ByteArrayInputStream
import java.io.File
import java.net.NetworkInterface
import java.net.URLEncoder
import java.util.*


object ProxyUtil {

    @JvmStatic
    val cacheFile = File(ApplicationLoader.applicationContext.filesDir.parent, "nekox/proxy_list.json")

    @JvmStatic
    fun isVPNEnabled(): Boolean {

        val networkList = mutableListOf<String>()

        runCatching {

            Collections.list(NetworkInterface.getNetworkInterfaces()).forEach {

                if (it.isUp) networkList.add(it.name)

            }

        }

        return networkList.contains("tun0")

    }

    @JvmStatic
    fun reloadProxyList(): Boolean {

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

    @JvmStatic
    @JvmOverloads
    fun shareProxy(ctx: Context,info: SharedConfig.ProxyInfo,copy: Boolean = false) {

        val url = if (info is SharedConfig.VmessProxy) {

            info.toString()

        } else {

            val httpUrl = HttpUrl.parse(if(info.secret.isEmpty()) {

                "https://t.me/socks"

            } else {

                "https://t.me/proxy"

            })!!.newBuilder()

            httpUrl.addQueryParameter("server",info.address)
            httpUrl.addQueryParameter("port",info.port.toString())

            if (info.secret.isNotBlank()) {

                httpUrl.addQueryParameter("secret", info.secret)

            } else {

                httpUrl.addQueryParameter("user",info.username)
                httpUrl.addQueryParameter("pass",info.password)

            }

            httpUrl.build().toString()

        }

        if (copy) {

            AndroidUtilities.addToClipboard(url)

            Toast.makeText(ctx,LocaleController.getString("LinkCopied",R.string.LinkCopied),Toast.LENGTH_LONG).show()

        } else {

            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.type = "text/plain"

            shareIntent.putExtra(Intent.EXTRA_TEXT, url)

            val chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", R.string.ShareLink))

            chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            ctx.startActivity(chooserIntent)

        }

    }

}