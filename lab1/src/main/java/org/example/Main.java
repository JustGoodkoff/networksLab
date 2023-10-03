

package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class Main {
    public static Map<String, Long> ips = new HashMap();
    private static String id;
    private static String multicastAddress;
    private static int port;

    public Main() {
    }

    private static boolean parseData(String[] var0) {
        if (!var0[0].equals("IPv4") && (!var0[0].equals("IPv6") || !var0[2].equals("id"))) {
            return false;
        } else {
            multicastAddress = var0[1];
            id = var0[3];
            port = Integer.parseInt(var0[4]);
            return true;
        }
    }

    public static void main(String[] var0) throws IOException {
        int var1 = 0;
        if (!parseData(var0)) {
            System.out.println("Проверьте аргументы командной строки");
        } else {
            MulticastReceiver var2 = new MulticastReceiver(multicastAddress, id);
            var2.start();
            InetAddress var4 = InetAddress.getByName(multicastAddress);
            String var5 = "1";
            if (id.length() < 10) {
                var5 = var5 + "0" + id.length();
            } else {
                var5 = var5 + String.valueOf(id.length());
            }

            var5 = var5 + id;
            MulticastSocket var3 = new MulticastSocket(port);

            while(true) {
                DatagramPacket var6 = new DatagramPacket(var5.getBytes(), var5.length(), var4, 6969);
                var3.send(var6);
                HashSet var7 = new HashSet();
                Iterator var8 = ips.keySet().iterator();

                while(var8.hasNext()) {
                    String var9 = (String)var8.next();
                    if ((new Date()).getTime() - (Long)ips.get(var9) > 10000L) {
                        var7.add(var9);
                    }
                }

                ips.keySet().removeAll(var7);

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var10) {
                    throw new RuntimeException(var10);
                }

                ++var1;
            }
        }
    }
}
