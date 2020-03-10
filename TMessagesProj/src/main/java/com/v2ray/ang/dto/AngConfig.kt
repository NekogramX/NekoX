package com.v2ray.ang.dto

import com.google.gson.Gson
import com.v2ray.ang.V2RayConfig
import com.v2ray.ang.V2RayConfig.SS_PROTOCOL
import com.v2ray.ang.V2RayConfig.VMESS_PROTOCOL
import com.v2ray.ang.util.Utils

data class AngConfig(
        var index: Int,
        var vmess: ArrayList<VmessBean>,
        var subItem: ArrayList<SubItemBean>
) {
    data class VmessBean(var guid: String = "123456",
                         var address: String = "v2ray.cool",
                         var port: Int = 10086,
                         var id: String = "a3482e88-686a-4a58-8126-99c9df64b7bf",
                         var alterId: Int = 64,
                         var security: String = "aes-128-cfb",
                         var network: String = "tcp",
                         var remarks: String = "def",
                         var headerType: String = "",
                         var requestHost: String = "",
                         var path: String = "",
                         var streamSecurity: String = "",
                         var configType: Int = 1,
                         var configVersion: Int = 1,
                         var testResult: String = "") {

        override fun toString(): String {

            if (configType == V2RayConfig.EConfigType.Vmess) {

                val vmessQRCode = VmessQRCode()

                vmessQRCode.v = configVersion.toString()
                vmessQRCode.ps = remarks
                vmessQRCode.add = address
                vmessQRCode.port = port.toString()
                vmessQRCode.id = id
                vmessQRCode.aid = alterId.toString()
                vmessQRCode.net = network
                vmessQRCode.type = headerType
                vmessQRCode.host = requestHost
                vmessQRCode.path = path
                vmessQRCode.tls = streamSecurity

                return VMESS_PROTOCOL + Utils.encode(Gson().toJson(vmessQRCode))

            } else if (configType == V2RayConfig.EConfigType.Shadowsocks) {

                val remark = "#" + Utils.urlEncode(remarks)

                val url = String.format("%s:%s@%s:%s",security, id, address, port)

                return SS_PROTOCOL + Utils.encode(url) + remark

            } else {

                error("invalid vmess bean type")

            }

        }

    }

    data class SubItemBean(var id: String = "",
                           var remarks: String = "",
                           var url: String = "")
}
