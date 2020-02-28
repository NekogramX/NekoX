package tw.nekomimi.nekogram;

import cn.hutool.core.util.StrUtil;

public class HttpUtil {

    public static String get(String url) {

        return new String(cn.hutool.http.HttpUtil.createGet(url).execute().bodyBytes());

    }

}
