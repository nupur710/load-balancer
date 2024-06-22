package org.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public Server(int port) throws IOException {
        serverSocket= new ServerSocket(port);
        socket= serverSocket.accept();
        System.out.println("Server running in port " + port);
    }

    public String readInput() throws IOException {
        input= new DataInputStream(socket.getInputStream());
        return input.readUTF();
    }

    public void writeOutput(String text) throws IOException {
        output= new DataOutputStream(socket.getOutputStream());
        output.writeUTF(text);
    }
}
