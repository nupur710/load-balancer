
package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    private int port;
    private static final String CRLF= "\r\n";

    public Server(int port) {
        this.port= port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        try(ServerSocket serverSocket= new ServerSocket(port)) {
            System.out.println("Server running at port " + port);
            while(true) {
                try(Socket socket= serverSocket.accept();
                    DataOutputStream output= new DataOutputStream(socket.getOutputStream());
                    BufferedReader br= new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    StringBuilder inputFromLB = new StringBuilder();
                    while ((line = br.readLine()) != null && !line.isEmpty()) {
                        inputFromLB.append(line);
                    }
                    String response = parseRequest(inputFromLB.toString());
                    output.write(response.getBytes("UTF-8")); //not specifying charsetName throws connection reset by peer exception
                    output.flush();
                    //wait before closing connection
                    Thread.sleep(100);
                } catch (IOException e) {
                    System.err.println("Error handling request at port " + port + ": " +e.getMessage());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server at port " + port + ": " + e.getMessage());
        }
    }

    public String parseRequest(String request) {
        String[] cd= request.split(" ");
        String http= cd[2].substring(0, 8);
        String responseBody= "Response 200OK successfully received from server running at port " + port;
        String response = http+" 200 OK" + CRLF +
                "Content-Type: text/plain" + CRLF +
                "Content-Length: " + responseBody.length() + CRLF +
                CRLF +
                responseBody;
        System.out.println(response);
        return response;
    }
}
