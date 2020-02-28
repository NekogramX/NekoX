package tw.nekomimi.nekogram;

import android.util.Log;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;

import org.apache.commons.net.util.SubnetUtils;
import org.telegram.messenger.ApplicationLoader;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class InternalProxy {

    static String TAG = "nekox";

    static LinkedList<String> ipv4AddressList;

    public static String newAddress() throws Exception {

        if (ipv4AddressList == null) {

            ipv4AddressList = new LinkedList<>();

            String v4Addresses;

            File cacheFile = new File(ApplicationLoader.applicationContext.getCacheDir(), "cloudflare-ips-v4.txt");

            if (!cacheFile.isFile() && System.currentTimeMillis() - cacheFile.lastModified() > 7 * 24 * 60 * 60 * 1000L) {

                v4Addresses = HttpUtil.get("https://www.cloudflare.com/ips-v4");

                FileUtil.writeUtf8String(v4Addresses,cacheFile);

            } else {

                v4Addresses = FileUtil.readUtf8String(cacheFile);

            }

            for (String address : v4Addresses.split("\n")) {

                CollectionUtil.addAll(ipv4AddressList, new SubnetUtils(address).getInfo().getAllAddresses());

            }

        }

        ThreadLocalRandom random = RandomUtil.getRandom();

        while (true) {

            String address = ipv4AddressList.get(random.nextInt(ipv4AddressList.size()));

            long start = System.currentTimeMillis();

            Socket server = new Socket();

            InetSocketAddress target = new InetSocketAddress(address, 80);

            try {

                server.connect(target, 200);

                server.close();

                long end = System.currentTimeMillis();

                HttpURLConnection httpConnection = (HttpURLConnection) new URL("http://" + address).openConnection();

                httpConnection.setConnectTimeout(1000);
                httpConnection.setReadTimeout(1000);

                httpConnection.getResponseCode();

                Log.d(TAG, "IP " + address + " " + (end - start) + "ms");

                return address;

            } catch (Exception e) {

                Log.d(TAG, "IP " + address + " : " + e.getMessage());

            }

        }

    }


}
