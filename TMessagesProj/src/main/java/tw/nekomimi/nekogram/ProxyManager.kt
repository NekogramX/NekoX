package tw.nekomimi.nekogram

import android.content.Context
import com.v2ray.ang.dto.AngConfig
import org.telegram.messenger.ApplicationLoader
import java.net.ServerSocket
import kotlin.random.Random

object ProxyManager {

    val pref by lazy { ApplicationLoader.applicationContext.getSharedPreferences("port_cfg", Context.MODE_PRIVATE) }

    @JvmStatic
    fun getPorxyForBean(bean: AngConfig.VmessBean): Int {

        val hash = (bean.address + bean.port + bean.path).hashCode().toString()

        var port = pref.getInt(hash, -1)

        if (!isPorxyAvilable(port)) {

            port = mkNewPort()

            pref.edit().putInt(hash, port).apply()

        }

        return port

    }

    fun mkNewPort(): Int {

        val random = Random(System.currentTimeMillis())

        var port: Int

        do {

            port = random.nextInt(2048, 32768)

        } while (!isPorxyAvilable(port))

        return port

    }

    fun isPorxyAvilable(port: Int): Boolean {

        if (port !in 2048..32767) return false

        runCatching {

            ServerSocket(port).close()

        }.onFailure {

            return false

        }

        return true

    }

}
