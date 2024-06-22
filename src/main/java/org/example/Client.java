package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    static Socket socket= null;
    static Socket socket2= null;
    static Socket socket3= null;
    static DataInputStream input= null;
    static DataInputStream input2= null;
    static DataInputStream input3= null;
    static DataOutputStream output= null;
    static DataOutputStream output2= null;
    static DataOutputStream output3= null;

    public static void main(String[] args) throws IOException {
        socket= new Socket("localhost", 8080);
        socket2= new Socket("localhost", 8081);
        socket3= new Socket("localhost", 8082);
        output= new DataOutputStream(socket.getOutputStream());
        output2= new DataOutputStream(socket2.getOutputStream());
        output3= new DataOutputStream(socket3.getOutputStream());
        input= new DataInputStream(socket.getInputStream());
        input2= new DataInputStream(socket2.getInputStream());
        input3= new DataInputStream(socket3.getInputStream());
        output.writeUTF("Request from client to server 1");
        output2.writeUTF("Request from client to server 2");
        output3.writeUTF("Request from client to server 3");
    }


}
