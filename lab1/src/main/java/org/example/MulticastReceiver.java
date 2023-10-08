

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

    MulticastReceiver(String id, String address) {
        this.id = id;
        this.multicastAddress = address;
    }

    public void run() {
        InetAddress address = null;

        try {
            this.socket = new MulticastSocket(6969);
            address = InetAddress.getByName(this.multicastAddress);
            this.socket.joinGroup(address);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(true) {
            DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length);

            try {
                this.socket.receive(packet);
                System.out.println(this.socket.getNetworkInterface().getName());
                String data;
                if (packet.getData()[0] == 0) {
                    data = new String(packet.getData(), 1, packet.getLength() - 1);
                    if (data.equals(this.id)) {
                        Main.ips.remove(data);
                        break;
                    }

                    Main.ips.remove(data);
                } else {
                    data = new String(packet.getData(), 3, packet.getLength() - 3);
                    if (Main.ips.containsKey(data)) {
                        Main.ips.replace(data, (new Date()).getTime());
                    } else {
                        Main.ips.put(data, (new Date()).getTime());
                    }

                    System.out.println(Main.ips);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            this.socket.leaveGroup(address);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.socket.close();
    }
}
