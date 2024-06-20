package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static ServerSocket serverSocket= null;
    static Socket socket= null;
    static DataInputStream input= null;
    static DataOutputStream output= null;

    public static void main(String[] args) {
    try {
        serverSocket = new ServerSocket(9999);
        socket= serverSocket.accept();
        input= new DataInputStream(socket.getInputStream());
        output= new DataOutputStream(socket.getOutputStream());
        output.writeUTF("Hello from server");
        String fromClient= input.readUTF();
        System.out.println(fromClient);
        socket.close();
        serverSocket.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}