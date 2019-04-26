package org.academiadecodigo.bootcamp.SimpleWebServer;


import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private Socket connectionSocket;
    private final String DEFAULT_FILE = "/index.html";
    private DataOutputStream out;
    private BufferedReader in;

    public RequestHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    public void run() {

        try {
            this.in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            this.out = new DataOutputStream(connectionSocket.getOutputStream());

            String resourcePath = receiveRequest(in);

            File resource = new File("www" + resourcePath);

            if (getStatusCode(resource).equals("404 Not Found")) {
                resource = new File("www/404.html");
            }

            String header = generateHeader(resource);

            out.writeBytes(header);

            getBytesFromFile(resource);

            closeConnections();

        } catch (Exception e) {
            System.out.println("Exception.");
        }

    }

    public String receiveRequest(BufferedReader in) throws IOException {

        String request = in.readLine().split(" ")[1];

        System.out.println(request);

        if (request.endsWith("/")) {
            return DEFAULT_FILE;
        }
        return request;
    }

    public String generateHeader(File resource) throws IOException {

        String contentStatusCode = getStatusCode(resource);
        String contentType = getContentType(resource);
        long contentLength = resource.length();

        String header = "HTTP/1.0 " + contentStatusCode + "\r\n Content-Type: " + contentType + " charset=UTF-8\r\n Content-Length: " + contentLength + "\r\n\r\n";

        return header;
    }

    private String getContentType(File resource) throws IOException {

        String contentType = resource.getCanonicalPath().split("\\.")[1];

        switch (contentType) {
            case ".html":
                contentType = "text/html";
                break;
            case ".jpg":
                contentType = "image/jpg";
                break;
            case ".jpeg":
                contentType = "image/jpeg";
                break;
            case ".png":
                contentType = "image/png";
                break;
            default:
                contentType = "text/html";
        }
        return contentType;
    }

    private String getStatusCode(File resource) {
        if (!resource.exists()) {
            return "404 Not Found";
        }
        return "200 Document Follows";
    }


    public byte[] getBytesFromFile(File resource) throws IOException {
        FileInputStream fileIn = new FileInputStream(resource);
        byte[] fileData = new byte[1024];
        int line = 0;
        while ((line = fileIn.read(fileData)) != -1) {
            out.write(fileData, 0, line);

        }

        return fileData;
    }

    public void closeConnections() throws Exception {

        this.connectionSocket.close();
        this.out.close();
        this.in.close();

    }


}
