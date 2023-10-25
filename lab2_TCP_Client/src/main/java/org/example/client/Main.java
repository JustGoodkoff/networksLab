package org.example.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {
    @Parameter(names = {"-path"})
    private static String path;

    @Parameter(names = {"-IP"})
    private static String serverIP;

    @Parameter(names = {"-port"})
    private static int serverPort;

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args);

        Client client = new Client(serverIP,  serverPort, path);
//        Client client = new Client("192.168.1.62", 6969, "C:\\Users\\79631\\.android\\avd\\Pixel_3a_API_34_extension_level_7_x86_64.avd\\sdcard.img");
        client.run();
    }
}