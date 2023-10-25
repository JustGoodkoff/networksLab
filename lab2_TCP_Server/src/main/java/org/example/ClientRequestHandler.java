package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class ClientRequestHandler implements Runnable {
    private final long TIME_INTERVAL = 3000; // 3sec
    private final int BUFFER_SIZE = 1024;

    private final Socket socket;

    public ClientRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream clientReader = new DataInputStream(socket.getInputStream());
             DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
             socket) {
            int fileNameSize = clientReader.readInt();
            String fileName = clientReader.readUTF();
            if (fileNameSize != fileName.length()) {
                System.out.println("Wrong filename");
                return;
            }
            long fileSize = clientReader.readLong();
            Path path = createFile(fileName);
            try (OutputStream fileWriter = Files.newOutputStream(path)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                long allReadBytes = 0;
                long prevAllReadBytes = 0;
                long startTime = System.currentTimeMillis();
                long lastCheckedTime = startTime;
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                while (allReadBytes < fileSize) {
                    int readBytes;
                    if ((readBytes = clientReader.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                        fileWriter.write(buffer, 0, readBytes);
                        md.update(buffer, 0, readBytes);
                    }
                    allReadBytes += readBytes;
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastCheckedTime > TIME_INTERVAL) {
                        double currentSpeed = (allReadBytes - prevAllReadBytes) / 1024.0 / 1024.0 / ((currentTime - lastCheckedTime) / 1000.0);
                        double avgSpeed = allReadBytes / 1024.0 / 1024.0 / ((currentTime - startTime) / 1000.0);
                        System.out.println("Speed for client " + socket.getInetAddress() + " " + socket.getPort() + " : instant speed = " + "Mb/s " + currentSpeed + " average speed = " + avgSpeed + " Mb/s");
                        lastCheckedTime = currentTime;
                        prevAllReadBytes = allReadBytes;
                    }
                }
                fileWriter.close();
                System.out.println(allReadBytes);
                if (System.currentTimeMillis() - startTime <= TIME_INTERVAL) {
                    long avgSpeed = allReadBytes * 1000 / (System.currentTimeMillis() - lastCheckedTime);
                    System.out.println("Speed for client " + socket.getInetAddress() + " " + socket.getPort() + " : average speed = " + avgSpeed + " Mb/s");
                }

                String hashSum = clientReader.readUTF();
                writer.writeBoolean(!hashSum.equals(hashSumToString(md)));
                writer.flush();
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("Connection with client " +  socket.getInetAddress() + " " + socket.getPort() + " is closed");
    }

    private Path createFile(String filename) throws IOException {
        Path dirPath = Paths.get( "uploads");
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }
        Path path = Paths.get(System.getProperty("user.dir") + System.getProperty("file.separator") + dirPath + System.getProperty("file.separator") + filename);
        System.out.println(System.getProperty("user.dir"));
        if (Files.exists(path)) {
            path = Paths.get(System.getProperty("user.dir") + System.getProperty("file.separator") + dirPath + System.getProperty("file.separator") + generateRandomFileName(filename));
        }
        Files.createFile(path);
        return path;
    }

    private String hashSumToString(MessageDigest md) {
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


    private String generateRandomFileName(String fileName) {
        Random random = new Random();
        return Math.abs(random.nextInt()) + "_" + fileName;
    }


}