package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{

    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private int port;

    public Server(int port) {
        this.port= port;
    }


    //contain code that will run in thread
    @Override
    public void run() {
        try {
            serverSocket= new ServerSocket(port);
            System.out.println("Server running at port " + port);
            while(true) {
                socket= serverSocket.accept();
                input= new DataInputStream(socket.getInputStream());
                output= new DataOutputStream(socket.getOutputStream());
                BufferedReader br= new BufferedReader(new InputStreamReader(input));
                String line;
                StringBuilder inputFromLB= new StringBuilder();
                while((line= br.readLine()) != null && !line.isEmpty()) {
                    inputFromLB.append(line);
                }
                System.out.println(inputFromLB.toString());
                String response= "Response from server running at port " + port;
                output.writeUTF(response);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
