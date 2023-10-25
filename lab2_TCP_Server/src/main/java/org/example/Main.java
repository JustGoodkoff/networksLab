package org.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    @Parameter(names = {"-port"})
    private static int port;

    public static void main(String[] args) throws UnknownHostException {
        JCommander.newBuilder().addObject(new Main()).build().parse(args);
        Server server = new Server(port, 10);
        System.out.println(InetAddress.getLocalHost().getHostAddress() + " " + port);
        server.run();
    }
}