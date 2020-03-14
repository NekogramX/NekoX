package tw.nekomimi.nekogram.utils

import android.Manifest
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.v2ray.ang.V2RayConfig.VMESS_PROTOCOL
import okhttp3.HttpUrl
import org.json.JSONArray
import org.telegram.messenger.*
import org.telegram.messenger.browser.Browser
import org.telegram.ui.ActionBar.BottomSheet
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

        clip.primaryClip?.getItemAt(0)?.text?.split('\n')?.map { it.split(" ") }?.forEach {
            it.forEach {

                if (it.startsWith("tg://proxy") ||
                        it.startsWith("tg://socks") ||
                        it.startsWith("https://t.me/proxy") ||
                        it.startsWith("https://t.me/socks") ||
                        it.startsWith("vmess://")) {

                    exists = true

                    import(ctx, it)

                }

            }

        }

        if (!exists) {

            Toast.makeText(ctx, LocaleController.getString("BrokenLink", R.string.BrokenLink), Toast.LENGTH_LONG).show()

        }

    }

    fun import(ctx: Context, link: String) {

        runCatching {

            if (link.startsWith(VMESS_PROTOCOL)) {

                AndroidUtilities.showVmessAlert(ctx, SharedConfig.VmessProxy(link))

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
    fun shareProxy(ctx: Activity, info: SharedConfig.ProxyInfo, type: Int) {

        val url = if (info is SharedConfig.VmessProxy) {

            info.bean.toString()

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

        if (type == 1) {

            AndroidUtilities.addToClipboard(url)

            Toast.makeText(ctx, LocaleController.getString("LinkCopied", R.string.LinkCopied), Toast.LENGTH_LONG).show()

        } else if (type == 0) {

            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.type = "text/plain"

            shareIntent.putExtra(Intent.EXTRA_TEXT, url)

            val chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", R.string.ShareLink))

            chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            ctx.startActivity(chooserIntent)

        } else {

            showQrDialog(ctx, url)

        }

    }

    @JvmStatic
    fun showQrDialog(ctx: Activity, text: String) {

        val code = createQRCode(text)

        android.app.AlertDialog.Builder(ctx).setView(LinearLayout(ctx).apply {

            addView(LinearLayout(ctx).apply {

                gravity = Gravity.CENTER

                val width = AndroidUtilities.dp(330f)

                addView(ImageView(ctx).apply {

                    setImageBitmap(code)

                    scaleType = ImageView.ScaleType.FIT_XY

                    setOnLongClickListener {

                        BottomSheet.Builder(ctx).setItems(arrayOf(

                                LocaleController.getString("SaveToGallery", R.string.SaveToGallery),
                                LocaleController.getString("Cancel", R.string.Cancel)

                        )) { _, i ->

                            if (i == 0) {

                                if (Build.VERSION.SDK_INT >= 23 && ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                    ctx.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 4)

                                    return@setItems

                                }

                                val saveTo = File(Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_DOWNLOADS}/share_${text.hashCode()}.jpg")

                                saveTo.parentFile?.mkdirs()

                                runCatching {

                                    saveTo.createNewFile()

                                    saveTo.outputStream().use {

                                        code?.compress(Bitmap.CompressFormat.JPEG, 100, it);

                                    }

                                    AndroidUtilities.addMediaToGallery(saveTo.path)

                                }

                            }

                        }.show()

                        return@setOnLongClickListener true

                    }

                }, LinearLayout.LayoutParams(width, width))

            }, LinearLayout.LayoutParams(-1, -1).apply {

                gravity = Gravity.CENTER

            })

        }).show()

    }

    fun createQRCode(text: String, size: Int = 800): Bitmap? {
        try {
            val hints = HashMap<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            //hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000.toInt()
                    } else {
                        pixels[y * size + x] = 0xffffffff.toInt()
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }

    val qrReader = QRCodeReader()

    @JvmStatic
    fun tryReadQR(ctx: Activity, bitmap: Bitmap) {

        val intArray = IntArray(bitmap.getWidth() * bitmap.getHeight())
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight())
        val source = RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray)

        val result = qrReader.decode(BinaryBitmap(GlobalHistogramBinarizer(source)))

        if (result == null || result.text.isBlank()) {

            AlertUtil.showToast(LocaleController.getString("NoQrFound", R.string.NoQrFound))

        } else {

            showLinkAlert(ctx, result.text)

        }

    }

    @JvmStatic
    fun showLinkAlert(ctx: Activity, text: String) {

        val builder = BottomSheet.Builder(ctx)

        var isUrl = false

        runCatching {
            HttpUrl.parse(text)
            if (Browser.isInternalUrl(text, booleanArrayOf(false))) {
                Browser.openUrl(ctx, text)
                return
            }
            isUrl = true
        }

        builder.setTitle(text)

        builder.setItems(arrayOf(
                if (isUrl) LocaleController.getString("Open", R.string.OpenUrlTitle) else null,
                LocaleController.getString("Copy", R.string.Copy),
                LocaleController.getString("Cancel", R.string.Cancel)
        )) { _, i ->
            if (i == 0) {
                Browser.openUrl(ctx, text)
            } else if (i == 1) {
                AndroidUtilities.addToClipboard(text)
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopied", R.string.LinkCopied), Toast.LENGTH_LONG).show()
            }
        }

        builder.show()

    }

}
