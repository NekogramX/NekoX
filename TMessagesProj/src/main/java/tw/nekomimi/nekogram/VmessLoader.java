package tw.nekomimi.nekogram;

import libv2ray.Libv2ray;
import libv2ray.V2RayPoint;
import libv2ray.V2RayVPNServiceSupportsSet;

public class VmessLoader {

    private V2RayPoint point;

    public void initConfig(String config) {

        point = Libv2ray.newV2RayPoint(new EmptyCallback());
        point.setConfigureFileContent(config);

        point.setEnableLocalDNS(false);

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
