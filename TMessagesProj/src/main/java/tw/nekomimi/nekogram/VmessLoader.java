package tw.nekomimi.nekogram;

import android.content.Context;

import cn.hutool.core.codec.Base64;
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

        String cfg = Base64.decodeStr("eyJpbmJvdW5kcyI6W3sidGFnIjoic29ja3MtaW4iLCJsaXN0ZW4iOiIxMjcuMC4wLjEiLCJwb3J0IjoxMTIxMCwicHJvdG9jb2wiOiJzb2NrcyIsInNldHRpbmdzIjp7ImF1dGgiOiJub2F1dGgiLCJ1ZHAiOnRydWUsInVzZXJMZXZlbCI6OH0sInNuaWZmaW5nIjp7ImRlc3RPdmVycmlkZSI6WyJodHRwIiwidGxzIl0sImVuYWJsZWQiOnRydWV9fSx7Imxpc3RlbiI6IjEyNy4wLjAuMSIsInBvcnQiOjExMjExLCJwcm90b2NvbCI6Imh0dHAiLCJzZXR0aW5ncyI6eyJ1c2VyTGV2ZWwiOjh9fV0sIm91dGJvdW5kcyI6W3sidGFnIjoicHJveHkiLCJwcm90b2NvbCI6InZtZXNzIiwic2V0dGluZ3MiOnsidm5leHQiOlt7ImFkZHJlc3MiOiJfYWRkcmVzcyIsInBvcnQiOjQ0MywidXNlcnMiOlt7ImFsdGVySWQiOjY0LCJpZCI6IjczNjcwZjg2LTYwNDYtNGZmZC1iNDY4LTZjZDczY2VhMWYyOSIsImxldmVsIjo4LCJzZWN1cml0eSI6Im5vbmUifV19XX0sInN0cmVhbVNldHRpbmdzIjp7Im5ldHdvcmsiOiJ3cyIsInNlY3VyaXR5IjoidGxzIiwidGxzc2V0dGluZ3MiOnsiYWxsb3dJbnNlY3VyZSI6dHJ1ZSwic2VydmVyTmFtZSI6Imt1cnVtaS5pbyJ9LCJ3c3NldHRpbmdzIjp7ImNvbm5lY3Rpb25SZXVzZSI6dHJ1ZSwiaGVhZGVycyI6eyJIb3N0Ijoia3VydW1pLmlvIn0sInBhdGgiOiIvaW50ZXJuZXQifX19XSwicm91dGluZyI6eyJkb21haW5TdHJhdGVneSI6IklQSWZOb25NYXRjaCIsInJ1bGVzIjpbeyJpbmJvdW5kVGFnIjpbInNvY2tzLWluIl0sIm91dGJvdW5kVGFnIjoicHJveHkiLCJ0eXBlIjoiZmllbGQifV19fQ==")

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
