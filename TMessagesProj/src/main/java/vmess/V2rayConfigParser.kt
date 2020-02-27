package vmess

import cn.hutool.core.codec.Base64
import cn.hutool.json.JSONObject
import java.util.*

object V2rayConfigParser {

    fun parseVmessLink(linkWithScheme: String) {

        if (!linkWithScheme.startsWith("vmess://")) error("not a vmess link")

        var data = JSONObject(Base64.decode(linkWithScheme.substring(8)))

        

    }

}