package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {

    public static void main(String[] args) throws IOException {
        ServerSocket loadBalancer= new ServerSocket(1221);
        while(true) {
            Socket lbSocket = loadBalancer.accept();
            DataInputStream inputStream = new DataInputStream(lbSocket.getInputStream());
            String ip = inputStream.readUTF();
            System.out.println("To loadbalancer: " + ip);
        }
    }
}
