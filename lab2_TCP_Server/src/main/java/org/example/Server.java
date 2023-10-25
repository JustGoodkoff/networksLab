package org.example;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable{
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private static final int DEFAULT_BACKLOG = 3;
    private final int port;
    private final int backlog;


    public Server(int port, int backlog){
        this.port = port;
        this.backlog = backlog;
    }


    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port, this.backlog);
            System.out.println("Server start on port" + this.port);
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Client connected - " + socket.getInetAddress() + ":" + socket.getPort());
                Thread clientHandler = new Thread(new ClientRequestHandler(socket));
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Cannot open server socket");
        }
    }
}