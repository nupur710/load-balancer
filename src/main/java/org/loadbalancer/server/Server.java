
package org.loadbalancer.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Server implements Runnable{
    private int port;
    private AtomicLong lastAccessedTime= new AtomicLong();
    private static final String CRLF= "\r\n";
    private AtomicInteger activeConnections= new AtomicInteger(0);
    /**store a list of server instances. These are started when MultipleServers.java
     * is run. We will use this to retrieve server running at a port.
     */
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
                    activeConnections.getAndIncrement();
                    setLastAccessedTime(System.currentTimeMillis());
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
        } finally {
            activeConnections.getAndDecrement();
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

    public int getActiveConnections() {
        return activeConnections.get();
    }

    /**
     * Compare server objects for equality based on port no. value. If current
     */
    @Override
    public boolean equals(Object obj) {
        if(this== obj) return true;
        if(obj== null || getClass() != obj.getClass()) return false;
        Server server= (Server) obj;
        return port== server.port;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.port);
    }

    public long getLastAccessedTime() {
        return this.lastAccessedTime.get();
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime.set(lastAccessedTime);
    }
}