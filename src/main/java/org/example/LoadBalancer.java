package org.example;

import org.example.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {
    static private int serversStartPort= 8080;
    static private int noOfServersToRun= 2;
    static private int portToRun= serversStartPort;
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket loadBalancer= new ServerSocket(1221); //client will send request to port 1221
        while(true) {
            Socket lbSocket = loadBalancer.accept();
            DataInputStream lbClient= new DataInputStream(lbSocket.getInputStream());
            DataOutputStream lbClientOutput= new DataOutputStream(lbSocket.getOutputStream());
            if(portToRun > (serversStartPort+noOfServersToRun)-1) { portToRun= serversStartPort; }
            Socket connectToServer = new Socket("localhost", portToRun);
            portToRun++;
            //Send request to server
            DataOutputStream lbServer= new DataOutputStream(connectToServer.getOutputStream());
            byte[] buffer= new byte[8192];
            int bytesRead;
            while(lbClient.available() > 0 && (bytesRead= lbClient.read(buffer)) != -1) //reads buffer.length bytes of data from input stream & stores it into buffer array
            {
                lbServer.write(buffer, 0, bytesRead);
            }
            lbServer.flush();
            //Read Response from server
            DataInputStream lbServerResp = new DataInputStream(connectToServer.getInputStream());
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();

            while ((bytesRead = lbServerResp.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, bytesRead);
            }

            String responseFromServer = responseBuffer.toString();
            System.out.println(responseFromServer);

            lbClientOutput.write(responseBuffer.toByteArray());
            lbClientOutput.flush();
            lbClient.close();
            lbClientOutput.close();
            lbServerResp.close();
            connectToServer.close();
            lbSocket.close();

        }
    }
}
