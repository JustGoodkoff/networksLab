

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

    private static boolean parseData(String[] args) {
        if (!args[0].equals("IPv4") && (!args[0].equals("IPv6") || !args[2].equals("id"))) {
            return false;
        } else {
            multicastAddress = args[1];
            id = args[3];
            port = Integer.parseInt(args[4]);
            return true;
        }
    }

    public static void main(String[] args) throws IOException {
        if (!parseData(args)) {
            System.out.println("Проверьте аргументы командной строки");
            return;
        }
        MulticastReceiver receiver = new MulticastReceiver(multicastAddress, id);
        receiver.start();
        InetAddress group = InetAddress.getByName(multicastAddress);
        String message = "1";
        if (id.length() < 10) {
            message += "0" + id.length();
        } else {
            message += String.valueOf(id.length());
        }
        message += id;
        MulticastSocket socket = new MulticastSocket(port);

        while (true) {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), group, 6969);
            socket.send(packet);
            HashSet set = new HashSet();
            Iterator iterator = ips.keySet().iterator();

            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if ((new Date()).getTime() - ips.get(key) > 10000) {
                    set.add(key);
                }
            }

            ips.keySet().removeAll(set);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
