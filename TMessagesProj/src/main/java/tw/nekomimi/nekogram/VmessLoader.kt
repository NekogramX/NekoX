package tw.nekomimi.nekogram

import android.util.Log
import com.google.gson.Gson
import com.v2ray.ang.V2RayConfig
import com.v2ray.ang.V2RayConfig.SOCKS_PROTOCOL
import com.v2ray.ang.V2RayConfig.SS_PROTOCOL
import com.v2ray.ang.V2RayConfig.VMESS_PROTOCOL
import com.v2ray.ang.dto.AngConfig.VmessBean
import com.v2ray.ang.dto.VmessQRCode
import com.v2ray.ang.util.Utils
import com.v2ray.ang.util.V2rayConfigUtil
import libv2ray.Libv2ray
import libv2ray.V2RayPoint
import libv2ray.V2RayVPNServiceSupportsSet
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog

class VmessLoader {

    private val point: V2RayPoint = Libv2ray.newV2RayPoint(EmptyCallback())

    init {
        point.packageName = ApplicationLoader.applicationContext.packageName
    }

    fun parseVmessLink(server: String, port: Int): VmessBean {

        try {
            if (server.isBlank()) error("empty link")

            var vmess = VmessBean()

            if (server.startsWith(VMESS_PROTOCOL)) {

                val indexSplit = server.indexOf("?")
                if (indexSplit > 0) {
                    vmess = ResolveVmess4Kitsunebi(server)
                } else {

                    var result = server.replace(VMESS_PROTOCOL, "")
                    result = Utils.decode(result)
                    if (result.isBlank()) {
                        error("invalid url format")
                    }
                    val vmessQRCode = Gson().fromJson(result, VmessQRCode::class.java)
                    if (vmessQRCode.add.isBlank()
                            || vmessQRCode.port.isBlank()
                            || vmessQRCode.id.isBlank()
                            || vmessQRCode.aid.isBlank()
                            || vmessQRCode.net.isBlank()
                    ) {
                        error("invalid protocol")
                    }

                    vmess.configType = V2RayConfig.EConfigType.Vmess
                    vmess.security = "auto"
                    vmess.network = "tcp"
                    vmess.headerType = "none"

                    vmess.configVersion = Utils.parseInt(vmessQRCode.v)
                    vmess.remarks = vmessQRCode.ps
                    vmess.address = vmessQRCode.add
                    vmess.port = Utils.parseInt(vmessQRCode.port)
                    vmess.id = vmessQRCode.id
                    vmess.alterId = Utils.parseInt(vmessQRCode.aid)
                    vmess.network = vmessQRCode.net
                    vmess.headerType = vmessQRCode.type
                    vmess.requestHost = vmessQRCode.host
                    vmess.path = vmessQRCode.path
                    vmess.streamSecurity = vmessQRCode.tls
                }
                upgradeServerVersion(vmess)

                return vmess
            } else if (server.startsWith(SS_PROTOCOL)) {
                var result = server.replace(SS_PROTOCOL, "")
                val indexSplit = result.indexOf("#")
                if (indexSplit > 0) {
                    try {
                        vmess.remarks = Utils.urlDecode(result.substring(indexSplit + 1, result.length))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    result = result.substring(0, indexSplit)
                }

                //part decode
                val indexS = result.indexOf("@")
                if (indexS > 0) {
                    result = Utils.decode(result.substring(0, indexS)) + result.substring(indexS, result.length)
                } else {
                    result = Utils.decode(result)
                }

                val legacyPattern = "^(.+?):(.*)@(.+?):(\\d+?)$".toRegex()
                val match = legacyPattern.matchEntire(result) ?: error("invalid protocol")
                vmess.security = match.groupValues[1].toLowerCase()
                vmess.id = match.groupValues[2]
                vmess.address = match.groupValues[3]
                if (vmess.address.firstOrNull() == '[' && vmess.address.lastOrNull() == ']')
                    vmess.address = vmess.address.substring(1, vmess.address.length - 1)
                vmess.port = match.groupValues[4].toInt()

                return vmess

            } else if (server.startsWith(SOCKS_PROTOCOL)) {
                var result = server.replace(SOCKS_PROTOCOL, "")
                val indexSplit = result.indexOf("#")
                if (indexSplit > 0) {
                    try {
                        vmess.remarks = Utils.urlDecode(result.substring(indexSplit + 1, result.length))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    result = result.substring(0, indexSplit)
                }

                //part decode
                val indexS = result.indexOf(":")
                if (indexS < 0) {
                    result = Utils.decode(result)
                }

                val legacyPattern = "^(.+?):(\\d+?)$".toRegex()
                val match = legacyPattern.matchEntire(result) ?: error("invalid protocol")
                vmess.address = match.groupValues[1]
                if (vmess.address.firstOrNull() == '[' && vmess.address.lastOrNull() == ']')
                    vmess.address = vmess.address.substring(1, vmess.address.length - 1)
                vmess.port = match.groupValues[2].toInt()

                return vmess
            } else {
                error("invalid protocol")
            }
        } catch (e: Exception) {

            FileLog.e(e)

        }

        error("unknown link")

    }

    /**
     * upgrade
     */
    private fun upgradeServerVersion(vmess: VmessBean): Int {
        try {
            if (vmess.configVersion == 2) {
                return 0
            }

            when (vmess.network) {
                "kcp" -> {
                }
                "ws" -> {
                    var path = ""
                    var host = ""
                    val lstParameter = vmess.requestHost.split(";")
                    if (lstParameter.size > 0) {
                        path = lstParameter.get(0).trim()
                    }
                    if (lstParameter.size > 1) {
                        path = lstParameter.get(0).trim()
                        host = lstParameter.get(1).trim()
                    }
                    vmess.path = path
                    vmess.requestHost = host
                }
                "h2" -> {
                    var path = ""
                    var host = ""
                    val lstParameter = vmess.requestHost.split(";")
                    if (lstParameter.size > 0) {
                        path = lstParameter.get(0).trim()
                    }
                    if (lstParameter.size > 1) {
                        path = lstParameter.get(0).trim()
                        host = lstParameter.get(1).trim()
                    }
                    vmess.path = path
                    vmess.requestHost = host
                }
                else -> {
                }
            }
            vmess.configVersion = 2
            return 0
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }

    private fun ResolveVmess4Kitsunebi(server: String): VmessBean {

        val vmess = VmessBean()

        var result = server.replace(VMESS_PROTOCOL, "")
        val indexSplit = result.indexOf("?")
        if (indexSplit > 0) {
            result = result.substring(0, indexSplit)
        }
        result = Utils.decode(result)

        val arr1 = result.split('@')
        if (arr1.count() != 2) {
            return vmess
        }
        val arr21 = arr1[0].split(':')
        val arr22 = arr1[1].split(':')
        if (arr21.count() != 2 || arr21.count() != 2) {
            return vmess
        }

        vmess.address = arr22[0]
        vmess.port = Utils.parseInt(arr22[1])
        vmess.security = arr21[0]
        vmess.id = arr21[1]

        vmess.security = "chacha20-poly1305"
        vmess.network = "tcp"
        vmess.headerType = "none"
        vmess.remarks = "Alien"
        vmess.alterId = 0

        return vmess
    }

    fun initConfig(config: VmessBean, port: Int) {

        point.configureFileContent = V2rayConfigUtil.getV2rayConfig(config, port).content
        point.domainName = V2rayConfigUtil.currDomain
        point.enableLocalDNS = false
        point.forwardIpv6 = true

        Log.d("nekox",point.configureFileContent)
        Log.d("nekox","domainName: " + point.domainName)

    }

    fun initPublic(port: Int) {

        val public = VmessBean()
        public.address = "nekox.me"
        public.port = 443
        public.configType = V2RayConfig.EConfigType.Vmess
        public.id = "73670f86-6046-4ffd-b468-6cd73cea1f29"
        public.security = "none"
        public.network = "ws"
        public.streamSecurity = "tls"
        public.requestHost = "nekox.me"
        public.path = "/internet"

        initConfig(public, port)

    }

    fun start() {

        if (point.isRunning) return

        point.runLoop()

    }

    fun stop() {

        point.stopLoop()

    }

    class EmptyCallback : V2RayVPNServiceSupportsSet {
        override fun onEmitStatus(l: Long, s: String): Long {
            return 0
        }

        override fun prepare(): Long {
            return 0
        }

        override fun protect(l: Long): Long {
            return 0
        }

        override fun sendFd(): Long {
            return 0
        }

        override fun setup(s: String): Long {
            return 0
        }

        override fun shutdown(): Long {
            return 0
        }
    }

}