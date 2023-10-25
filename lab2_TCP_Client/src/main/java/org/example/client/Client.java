package org.example.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class Client implements Runnable {

    private final int BUFFER_SIZE = 1024;

    private InetAddress serverAddress;
    private int port;

    private Path filePath;

    public Client(String serverAddress, int port, String filePath) throws IOException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.port = port;
        this.filePath = getFilePath(filePath);
    }


    private Path getFilePath(String pathStr) throws IOException {
        System.out.println(pathStr);
        Path tmpPath = Paths.get(pathStr);
        if (!Files.exists(tmpPath)){
            throw new IOException(pathStr + " does not exist");
        }
        return tmpPath;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(this.serverAddress, this.port);
            InputStream fileReader = Files.newInputStream(filePath);
            DataInputStream socketInput = new DataInputStream(socket.getInputStream());
            DataOutputStream socketOutput = new DataOutputStream(socket.getOutputStream());


            socketOutput.writeInt(filePath.getFileName().toString().length());
            socketOutput.writeUTF(filePath.getFileName().toString());
            socketOutput.writeLong(Files.size(filePath));
            socketOutput.flush();

            byte[] buf = new byte[BUFFER_SIZE];
            MessageDigest hashSum = MessageDigest.getInstance("SHA-256");
            int messageSize;
            while ((messageSize = fileReader.read(buf, 0, BUFFER_SIZE)) > 0) {
                socketOutput.write(buf, 0, messageSize);
                socketOutput.flush();
                hashSum.update(buf);
            }

            socketOutput.writeUTF(hashSum.toString());
            socketOutput.flush();

            if (!socketInput.readBoolean()) {
                System.out.println("Incorrect data received by server");
            } else {
                System.out.println("Data received by server correctly");
            }
            socketOutput.flush();
            socketOutput.close();
            socketInput.close();
            socket.close();

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
