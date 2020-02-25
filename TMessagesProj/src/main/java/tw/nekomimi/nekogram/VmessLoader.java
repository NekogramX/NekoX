package tw.nekomimi.nekogram;

import android.content.Context;
import libv2ray.Libv2ray;
import libv2ray.V2RayPoint;
import libv2ray.V2RayVPNServiceSupportsSet;

public class VmessLoader {

    private V2RayPoint point;

    public VmessLoader(Context ctx) {

        point = Libv2ray.newV2RayPoint(new EmptyCallback());
        point.setPackageName(ctx.getPackageName());

    }

    public void initConfig(String config) {

        point.setConfigureFileContent(config);

        point.setEnableLocalDNS(false);
        point.setForwardIpv6(true);

    }

    public void initPublic() throws Exception {

        String addr = InternalProxy.newAddress();

        String cfg = "{\n" +
                "  \"inbounds\": [\n" +
                "    {\n" +
                "      \"tag\": \"socks-in\",\n" +
                "      \"listen\": \"127.0.0.1\",\n" +
                "      \"port\": 11210,\n" +
                "      \"protocol\": \"socks\",\n" +
                "      \"settings\": {\n" +
                "        \"auth\": \"noauth\",\n" +
                "        \"udp\": true,\n" +
                "        \"userLevel\": 8\n" +
                "      },\n" +
                "      \"sniffing\": {\n" +
                "        \"destOverride\": [\n" +
                "          \"http\",\n" +
                "          \"tls\"\n" +
                "        ],\n" +
                "        \"enabled\": true\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"listen\": \"127.0.0.1\",\n" +
                "      \"port\": 11211,\n" +
                "      \"protocol\": \"http\",\n" +
                "      \"settings\": {\n" +
                "        \"userLevel\": 8\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"outbounds\": [\n" +
                "    {\n" +
                "      \"tag\": \"proxy\",\n" +
                "      \"protocol\": \"vmess\",\n" +
                "      \"settings\": {\n" +
                "        \"vnext\": [\n" +
                "          {\n" +
                "            \"address\": \"_address\",\n" +
                "            \"port\": 443,\n" +
                "            \"users\": [\n" +
                "              {\n" +
                "                \"alterId\": 64,\n" +
                "                \"id\": \"73670f86-6046-4ffd-b468-6cd73cea1f29\",\n" +
                "                \"level\": 8,\n" +
                "                \"security\": \"none\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"streamSettings\": {\n" +
                "        \"network\": \"ws\",\n" +
                "        \"security\": \"tls\",\n" +
                "        \"tlssettings\": {\n" +
                "          \"allowInsecure\": true,\n" +
                "          \"serverName\": \"kurumi.io\"\n" +
                "        },\n" +
                "        \"wssettings\": {\n" +
                "          \"connectionReuse\": true,\n" +
                "          \"headers\": {\n" +
                "            \"Host\": \"kurumi.io\"\n" +
                "          },\n" +
                "          \"path\": \"/internet\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"routing\": {\n" +
                "    \"domainStrategy\": \"IPIfNonMatch\",\n" +
                "    \"rules\": [\n" +
                "      {\n" +
                "        \"inboundTag\": [\n" +
                "          \"socks-in\"\n" +
                "        ],\n" +
                "        \"outboundTag\": \"proxy\",\n" +
                "        \"type\": \"field\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        cfg = cfg.replace("_address", InternalProxy.newAddress());

        initConfig(cfg);

        point.setDomainName(addr + ":443");

    }

    public void start() throws Exception {

        point.runLoop();

    }

    public void stop() throws Exception {

        point.stopLoop();

    }

    public static class EmptyCallback implements V2RayVPNServiceSupportsSet {

        @Override
        public long onEmitStatus(long l, String s) {
            return 0;
        }

        @Override
        public long prepare() {
            return 0;
        }

        @Override
        public long protect(long l) {
            return 0;
        }

        @Override
        public long sendFd() {
            return 0;
        }

        @Override
        public long setup(String s) {
            return 0;
        }

        @Override
        public long shutdown() {
            return 0;
        }

    }


}
