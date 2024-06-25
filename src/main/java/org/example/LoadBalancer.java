package org.example;

import org.example.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {
    static private int startPort= 8080;
    static private int noOfServersToRun= 10;
    static private int portToRun= startPort;
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket loadBalancer= new ServerSocket(1221);
        while(true) {
            Socket lbSocket = loadBalancer.accept();
            DataInputStream inputStream = new DataInputStream(lbSocket.getInputStream());
            String ip = inputStream.readUTF();
            //if(lbSocket.isConnected()) System.out.println("connected!");
            System.out.println("To loadbalancer: " + ip);
            if(portToRun > (startPort+noOfServersToRun)-1) { portToRun= startPort; }
            Socket connectToServer = new Socket("localhost", portToRun);
            portToRun++;
            DataOutputStream dataOutputStream = new DataOutputStream(connectToServer.getOutputStream());
            dataOutputStream.writeUTF(ip);
//            dataOutputStream.close();
//            connectToServer.close();
//            inputStream.close();
//            lbSocket.close();
        }
    }
}
