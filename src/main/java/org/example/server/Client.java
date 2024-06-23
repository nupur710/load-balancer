package org.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {

    private Socket socket;
    private DataInputStream res;
    private DataOutputStream req;
    private static int counter= 1;
    private final int requestId;

    public Client(int port) throws IOException {
        //Connect to load balancer port
        socket= new Socket("localhost", port);
        synchronized (Client.class) {
            requestId= counter++;
        }
    }

    @Override
    public void run() {
        try {
            req = new DataOutputStream(socket.getOutputStream());
            req.writeUTF("Request " + requestId);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (req != null) {
                    req.close();
                    if (socket != null) {
                        socket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
