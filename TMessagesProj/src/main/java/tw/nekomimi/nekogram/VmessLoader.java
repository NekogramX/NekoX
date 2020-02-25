package tw.nekomimi.nekogram;

import android.content.Context;

import cn.hutool.core.codec.Base64;
import libv2ray.Libv2ray;
import libv2ray.V2RayPoint;
import libv2ray.V2RayVPNServiceSupportsSet;

public class VmessLoader {

    public static VmessLoader PUBLIC;

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

    static String cfg = Base64.decodeStr("eyJpbmJvdW5kcyI6W3sidGFnIjoic29ja3MtaW4iLCJsaXN0ZW4iOiIxMjcuMC4wLjEiLCJwb3J0IjoxMTIxMCwicHJvdG9jb2wiOiJzb2NrcyIsInNldHRpbmdzIjp7ImF1dGgiOiJub2F1dGgiLCJ1ZHAiOnRydWUsInVzZXJMZXZlbCI6OH0sInNuaWZmaW5nIjp7ImRlc3RPdmVycmlkZSI6WyJodHRwIiwidGxzIl0sImVuYWJsZWQiOnRydWV9fV0sIm91dGJvdW5kcyI6W3sidGFnIjoicHJveHkiLCJwcm90b2NvbCI6InZtZXNzIiwic2V0dGluZ3MiOnsidm5leHQiOlt7ImFkZHJlc3MiOiJfYWRkcmVzcyIsInBvcnQiOjQ0MywidXNlcnMiOlt7ImFsdGVySWQiOjY0LCJpZCI6IjczNjcwZjg2LTYwNDYtNGZmZC1iNDY4LTZjZDczY2VhMWYyOSIsImxldmVsIjo4LCJzZWN1cml0eSI6Im5vbmUifV19XX0sInN0cmVhbVNldHRpbmdzIjp7Im5ldHdvcmsiOiJ3cyIsInNlY3VyaXR5IjoidGxzIiwidGxzc2V0dGluZ3MiOnsiYWxsb3dJbnNlY3VyZSI6dHJ1ZSwic2VydmVyTmFtZSI6Im5la294Lm1lIn0sIndzc2V0dGluZ3MiOnsiY29ubmVjdGlvblJldXNlIjp0cnVlLCJoZWFkZXJzIjp7Ikhvc3QiOiJuZWtveC5tZSJ9LCJwYXRoIjoiL2ludGVybmV0In19fV0sInJvdXRpbmciOnsiZG9tYWluU3RyYXRlZ3kiOiJJUElmTm9uTWF0Y2giLCJydWxlcyI6W3siaW5ib3VuZFRhZyI6WyJzb2Nrcy1pbiJdLCJvdXRib3VuZFRhZyI6InByb3h5IiwidHlwZSI6ImZpZWxkIn1dfX0=");

    public void initPublic() throws Exception {

        if (point.getIsRunning()) stop();

        initConfig(cfg.replace("_address", "104.24.108.141"));

        point.setDomainName("104.24.108.141:443");

        start();

        String newCfg = cfg.replace("_address", InternalProxy.newAddress());

        stop();

        initConfig(newCfg);

        start();

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
