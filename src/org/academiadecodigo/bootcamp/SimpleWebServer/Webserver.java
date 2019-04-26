package org.academiadecodigo.bootcamp.SimpleWebServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Webserver {

    private ServerSocket serverConnect;
    private final int PORT = 8080;

    public Webserver() {
        try {
            this.serverConnect = new ServerSocket(this.PORT);
            System.out.println("Waiting for connections...");
        } catch (IOException exception) {
            System.out.println("Error trying to bind to port: " + exception.getMessage());
        }
    }

    public static void main(String[] args) {

        Webserver webserver = new Webserver();
        webserver.init();

    }

    public void init() {

        Socket connectionSocket;

        while (true) {
            try {

                connectionSocket = serverConnect.accept();
                ExecutorService cachedPool = Executors.newCachedThreadPool();
                cachedPool.submit(new RequestHandler(connectionSocket));

            } catch (Exception e) {
                System.out.println("Exception init().");
            }

        }
    }

}

