package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {
    static ServerSocket lb= null;
    static Socket lb_server= null;
    static Socket lb_client= null;
    static DataInputStream dataInputStream= null;
    static DataInputStream dataInputStream2= null;
    static DataOutputStream dataOutputStream= null;
    static DataOutputStream dataOutputStream2= null;
    public static void main(String[] args) {
        try {
            lb = new ServerSocket(8888);
            lb_server = lb.accept();
            dataInputStream= new DataInputStream(lb_server.getInputStream());
            lb_client= new Socket("localhost", 9999);
            dataInputStream2= new DataInputStream((lb_client.getInputStream()));
            String input2= dataInputStream2.readUTF(); //recieved from sever; send to client
            String input= dataInputStream.readUTF(); //recieved from client; send to server
           // System.out.println("Client says to load balancer: " + input);
            dataOutputStream2= new DataOutputStream(lb_server.getOutputStream());
            dataOutputStream2.writeUTF(input2);
            //System.out.println("Server says to load balancer: " + input2);
            dataOutputStream= new DataOutputStream(lb_client.getOutputStream());
            dataOutputStream.writeUTF(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
