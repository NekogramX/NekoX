package tw.nekomimi.nekogram;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.google.android.exoplayer2.util.Log;
import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class InternalProxy {

    static String TAG = "nekox";

    static LinkedList<String> ipv4AddressList;

    public static String newAddress() throws Exception {

        if (ipv4AddressList == null) {

            ipv4AddressList = new LinkedList<>();

            String v4Addresses = HttpUtil.get("https://www.cloudflare.com/ips-v4");

            for (String address : v4Addresses.split("\n")) {

                CollectionUtil.addAll(ipv4AddressList, new SubnetUtils(address).getInfo().getAllAddresses());

            }

        }

        ThreadLocalRandom random = RandomUtil.getRandom();

        while (true) {

            String address = ipv4AddressList.get(random.nextInt(ipv4AddressList.size()));

            long start = System.currentTimeMillis();

            Socket server = new Socket();

            InetSocketAddress target = new InetSocketAddress(address, 443);

            try {

                server.connect(target, 500);

                server.close();

                long end = System.currentTimeMillis();

                Log.d(TAG, "IP " + address + " " + (end - start) + "ms");

                return address;

            } catch (IOException e) {

                Log.d(TAG, "IP " + address + " 不可用");

            }

        }

    }


}
