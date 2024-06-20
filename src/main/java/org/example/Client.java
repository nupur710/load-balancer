package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    static Socket socket= null;
    static DataInputStream input= null;
    static DataOutputStream output= null;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 8888);
            output= new DataOutputStream(socket.getOutputStream());
            input= new DataInputStream(socket.getInputStream());
            output.writeUTF("hello from client");
            String response= input.readUTF();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
