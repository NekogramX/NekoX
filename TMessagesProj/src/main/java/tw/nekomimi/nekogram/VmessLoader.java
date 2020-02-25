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

        String cfg = "{\"inbounds\":[{\"listen\":\"127.0.0.1\",\"port\":11210,\"protocol\":\"socks\",\"settings\":{\"auth\":\"noauth\",\"udp\":true,\"userLevel\":8},\"sniffing\":{\"destOverride\":[\"http\",\"tls\"],\"enabled\":true}},{\"listen\":\"127.0.0.1\",\"port\":11211,\"protocol\":\"http\",\"settings\":{\"userLevel\":8}}],\"outbounds\":[{\"protocol\":\"vmess\",\"settings\":{\"vnext\":[{\"address\":\"_address\",\"port\":443,\"users\":[{\"alterId\":64,\"id\":\"73670f86-6046-4ffd-b468-6cd73cea1f29\",\"level\":8,\"security\":\"none\"}]}]},\"streamSettings\":{\"network\":\"ws\",\"security\":\"tls\",\"tlssettings\":{\"allowInsecure\":true,\"serverName\":\"kurumi.io\"},\"wssettings\":{\"connectionReuse\":true,\"headers\":{\"Host\":\"kurumi.io\"},\"path\":\"/internet\"}}}]}";

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
