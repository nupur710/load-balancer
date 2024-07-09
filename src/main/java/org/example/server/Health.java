package org.example.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Health {
    int noOfServers;
    int startPort;
    Health(int noOfServers, int startPort) {
        this.noOfServers= noOfServers;
        this.startPort= startPort;
    }

    List<Integer> healthOfServer() throws IOException {
        int port= startPort;
        List<Integer> healthyServers = null;
        for(int i= 0; i< noOfServers; i++) {
            Socket sc= new Socket("localhost", port);
            //Send request
            sendRequest(port);
            String status= response(sc);
            if(status.equals("200OK")) {
                healthyServers.add(port);
            }
            port++;
        } return healthyServers;
    }

    void sendRequest(int port) {
        String cmd= "curl http://localhost:"+port;
        ProcessBuilder processBuilder= new ProcessBuilder(cmd.split(" "));
        processBuilder.directory(new File("\\Users\\lenovo"));
    }

    String response(Socket sc) throws IOException {
        DataInputStream lbServerResp = new DataInputStream(sc.getInputStream());
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer= new byte[8192];
        while ((bytesRead = lbServerResp.read(buffer)) != -1) {
            responseBuffer.write(buffer, 0, bytesRead);
        }
        String responseFromServer = responseBuffer.toString();
        return responseFromServer.split(" ")[1];
    }
}