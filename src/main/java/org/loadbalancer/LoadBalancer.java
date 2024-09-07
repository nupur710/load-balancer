package org.loadbalancer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    static private int serversStartPort = 8080;
    static private int noOfServersToRun = 3;
    static private int portToRun = serversStartPort;
    static private byte[] buffer= new byte[8192];

    public static void main(String[] args) {
        List<Server> serversRunning= new ArrayList<>();
        for(int i= 0; i < noOfServersToRun; i++) {
            serversRunning.add(new Server(portToRun+i));
        }
        int i= 0;
        portToRun= serversRunning.get(0).getPort();
        HealthCheck healthCheck= new HealthCheck(serversRunning, 30);
        healthCheck.startScheduler();
        List<Server> healthyServers= healthCheck.getHealthyServers();
        for ( Server s : healthyServers) {
            System.out.println("Healthy servers running at ports: " + s.getPort());
        }
        try (ServerSocket loadBalancer = new ServerSocket(1221);) {
            while (true) {
                try (Socket lbSocket = loadBalancer.accept();
                     DataInputStream lbClient = new DataInputStream(lbSocket.getInputStream());
                     DataOutputStream lbClientOutput = new DataOutputStream(lbSocket.getOutputStream());) {
                    //Refresh list of healthy servers for every client request
                    healthyServers= healthCheck.getHealthyServers();
                    if(healthyServers.isEmpty()) {
                        System.err.println("No healthy server available");
                        lbClientOutput.writeUTF("503 Service Unavailable: No healthy servers");
                        continue;
                    }
                    if (portToRun > ((serversStartPort + noOfServersToRun) - 1)) {
                        portToRun = healthyServers.get(0).getPort();
                        i = 0;
                    }
                    try (Socket connectToServer = new Socket("localhost", portToRun);
                         DataOutputStream lbServer= new DataOutputStream(connectToServer.getOutputStream());
                         DataInputStream lbServerResp = new DataInputStream(connectToServer.getInputStream());) {
                        i++;
                        if (i <= (healthyServers.size() - 1)) {
                            portToRun = healthyServers.get(i).getPort();
                        } else {
                            portToRun = healthyServers.get(i - 1).getPort() + 1;
                        }
                        int bytesRead = sendRequest(lbClient, lbServer);
                        readResponse(lbClientOutput, lbServerResp, bytesRead);
                    } catch(IOException e) {
                        System.err.println("Error connecting to server: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error in load balancer: " + e.getMessage());
        }
    }
    private static int sendRequest(DataInputStream dis, DataOutputStream dos) throws IOException {
        int bytesRead = 0;
        while(dis.available() > 0 && (bytesRead= dis.read(buffer)) != -1) {
            dos.write(buffer, 0, bytesRead);
        }
        dos.flush();
        return bytesRead;
    }

    private static void readResponse(DataOutputStream dos, DataInputStream dis, int bytesRead) throws IOException {
        ByteArrayOutputStream responseBuffer= new ByteArrayOutputStream();
        while((bytesRead= dis.read(buffer)) != -1) {
            responseBuffer.write(buffer, 0, bytesRead);
        }
        String responseFromServer= responseBuffer.toString();
        System.out.println("Resp received from server:\n" + responseFromServer);
        dos.write(responseBuffer.toByteArray());
        dos.flush();
    }

}