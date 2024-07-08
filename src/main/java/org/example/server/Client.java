package org.example.server;

import java.io.ByteArrayOutputStream;
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
            res= new DataInputStream(socket.getInputStream());
            req.writeUTF("Request " + requestId);
            ByteArrayOutputStream bufferResp= new ByteArrayOutputStream();
            byte[] buffer= new byte[8192];
            int bytesRead;
            while((bytesRead= res.read(buffer)) != -1) {
                bufferResp.write(buffer, 0, bytesRead);
            }
            String responseMessage = bufferResp.toString();
            System.out.println("Response: " + responseMessage);

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
