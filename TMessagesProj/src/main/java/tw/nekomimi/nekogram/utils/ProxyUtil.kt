package tw.nekomimi.nekogram.utils

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.v2ray.ang.V2RayConfig.VMESS_PROTOCOL
import okhttp3.HttpUrl
import org.json.JSONArray
import org.telegram.messenger.*
import java.io.ByteArrayInputStream
import java.io.File
import java.net.NetworkInterface
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
    fun importFromClipboard(ctx: Context) {

        val clip = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        var exists = false

        clip.primaryClip?.getItemAt(0)?.text?.split(" ")?.forEach {

            if (it.startsWith("tg://proxy") ||
                    it.startsWith("tg://socks") ||
                    it.startsWith("https://t.me/proxy") ||
                    it.startsWith("https://t.me/socks") ||
                    it.startsWith("vmess://")) {

                exists = true

                import(ctx,it)

            }

        }

        if (!exists) {

            Toast.makeText(ctx, LocaleController.getString("BrokenLink", R.string.BrokenLink), Toast.LENGTH_LONG).show()

        }

    }

    fun import(ctx: Context, link: String) {

        runCatching {

            if (link.startsWith(VMESS_PROTOCOL)) {

                AndroidUtilities.showVmessAlert(ctx,SharedConfig.VmessProxy(link))

            } else {

                val url = HttpUrl.parse(link)!!

                AndroidUtilities.showProxyAlert(ctx,
                        url.queryParameter("server"),
                        url.queryParameter("port"),
                        url.queryParameter("user"),
                        url.queryParameter("pass"),
                        url.queryParameter("secret"))


            }

        }.onFailure {

            Toast.makeText(ctx, LocaleController.getString("BrokenLink", R.string.BrokenLink), Toast.LENGTH_LONG).show()

        }

    }


    @JvmStatic
    @JvmOverloads
    fun shareProxy(ctx: Context, info: SharedConfig.ProxyInfo, copy: Boolean = false) {

        val url = if (info is SharedConfig.VmessProxy) {

            info.toString()

        } else {

            val httpUrl = HttpUrl.parse(if (info.secret.isEmpty()) {

                "https://t.me/socks"

            } else {

                "https://t.me/proxy"

            })!!.newBuilder()

            httpUrl.addQueryParameter("server", info.address)
            httpUrl.addQueryParameter("port", info.port.toString())

            if (info.secret.isNotBlank()) {

                httpUrl.addQueryParameter("secret", info.secret)

            } else {

                httpUrl.addQueryParameter("user", info.username)
                httpUrl.addQueryParameter("pass", info.password)

            }

            httpUrl.build().toString()

        }

        if (copy) {

            AndroidUtilities.addToClipboard(url)

            Toast.makeText(ctx, LocaleController.getString("LinkCopied", R.string.LinkCopied), Toast.LENGTH_LONG).show()

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