

package org.example;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    private String id;
    private String multicastAddress;
    protected byte[] buf = new byte[256];

    MulticastReceiver(String var1, String var2) {
        this.id = var2;
        this.multicastAddress = var1;
    }

    public void run() {
        InetAddress var1 = null;

        try {
            this.socket = new MulticastSocket(6969);
            var1 = InetAddress.getByName(this.multicastAddress);
            this.socket.joinGroup(var1);
        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }

        while(true) {
            DatagramPacket var2 = new DatagramPacket(this.buf, this.buf.length);

            try {
                this.socket.receive(var2);
                System.out.println(this.socket.getNetworkInterface().getName());
                String var3;
                if (var2.getData()[0] == 50) {
                    var3 = new String(var2.getData(), 1, var2.getLength() - 1);
                    if (var3.equals(this.id)) {
                        Main.ips.remove(var3);
                        break;
                    }

                    Main.ips.remove(var3);
                } else {
                    var3 = new String(var2.getData(), 3, var2.getLength() - 3);
                    PrintStream var10000 = System.out;
                    String var10001 = String.valueOf(var2.getAddress());
                    var10000.println(var10001 + " " + var2.getPort() + " " + var3);
                    if (Main.ips.containsKey(var3)) {
                        Main.ips.replace(var3, (new Date()).getTime());
                    } else {
                        Main.ips.put(var3, (new Date()).getTime());
                    }

                    System.out.println(Main.ips);
                }
            } catch (IOException var6) {
                throw new RuntimeException(var6);
            }
        }

        try {
            this.socket.leaveGroup(var1);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }

        this.socket.close();
    }
}
