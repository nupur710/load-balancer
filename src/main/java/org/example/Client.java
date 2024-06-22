package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
       Socket[] sockets= new Socket[3];
       DataInputStream[] input= new DataInputStream[3];
       DataOutputStream[] output= new DataOutputStream[3];
       int[] ports= {8080, 8081, 8082};

       for (int i = 0; i< ports.length; i++) {
           sockets[i]= new Socket("localhost", ports[i]);
           input[i]= new DataInputStream(sockets[i].getInputStream());
           output[i]= new DataOutputStream(sockets[i].getOutputStream());
           output[i].writeUTF("Client sends request to server at port " + ports[i]);
           String response= input[i].readUTF();
           System.out.println("Client recieves response from server at port " + ports[i] + " : " + response);
       }

       for(int i= 0; i< ports.length; i++) {
           input[i].close();
           output[i].close();
           sockets[i].close();
       }
    }
}
