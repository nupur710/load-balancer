
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
    private static String CRLF= "\r\n";

    public Server(int port) {
        this.port= port;
    }


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
                String response= parseRequest(inputFromLB.toString());
                output.writeBytes(response);
                output.flush();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(serverSocket != null) {
                    serverSocket.close();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String parseRequest(String request) {
        String[] cd= request.split(" ");
        String http= cd[2].substring(0, 8);
        String responseBody= "Response 200 OK successfully received from server running at port " + port;
        String response = http+" 200 OK" + CRLF +
                "Content-Type: text/plain" + CRLF +
                "Content-Length: " + responseBody.length() + CRLF +
                CRLF +
                responseBody;
        return response;
    }
}