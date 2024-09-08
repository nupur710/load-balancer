package org.loadbalancer;

import org.loadbalancer.algorithms.LoadBalancerStrategy;
import org.loadbalancer.algorithms.RoundRobin;
import org.loadbalancer.algorithms.WeightedRoundRobin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    static private int serversStartPort = 8080;
    static private int noOfServersToRun = 3;
    static private int currentIndex = 0;
    static private byte[] buffer = new byte[8192];
    static private LoadBalancerStrategy strategy= new WeightedRoundRobin();

    public static void main(String[] args) {
        List<Server> serversRunning = new ArrayList<>();
        for (int i = 0; i < noOfServersToRun; i++) {
            serversRunning.add(new Server(serversStartPort + i));
        }
        HealthCheck healthCheck = new HealthCheck(serversRunning, 30);
        healthCheck.startScheduler();

        try (ServerSocket loadBalancer = new ServerSocket(1221)) {
            while (true) {
                try (Socket lbSocket = loadBalancer.accept();
                     DataInputStream lbClient = new DataInputStream(lbSocket.getInputStream());
                     DataOutputStream lbClientOutput = new DataOutputStream(lbSocket.getOutputStream())) {
                    // Refresh the list of healthy servers for every client request
                    List<Server> healthyServers = healthCheck.getHealthyServers();
                    if (healthyServers.isEmpty()) {
                        System.err.println("No healthy server available");
                        lbClientOutput.writeUTF("503 Service Unavailable: No healthy servers");
                        continue;
                    }
                    Server selectedServer = strategy.selectServer(healthyServers);
                    try (Socket connectToServer = new Socket("localhost", selectedServer.getPort());
                         DataOutputStream lbServer = new DataOutputStream(connectToServer.getOutputStream());
                         DataInputStream lbServerResp = new DataInputStream(connectToServer.getInputStream())) {

                        // Send client request to selected server
                        int bytesRead = sendRequest(lbClient, lbServer);

                        // Read response from server and send it back to client
                        readResponse(lbClientOutput, lbServerResp, bytesRead);

                    } catch (IOException e) {
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
        while (dis.available() > 0 && (bytesRead = dis.read(buffer)) != -1) {
            dos.write(buffer, 0, bytesRead);
        }
        dos.flush();
        return bytesRead;
    }

    private static void readResponse(DataOutputStream dos, DataInputStream dis, int bytesRead) throws IOException {
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        while ((bytesRead = dis.read(buffer)) != -1) {
            responseBuffer.write(buffer, 0, bytesRead);
        }
        String responseFromServer = responseBuffer.toString();
        System.out.println("Resp received from server:\n" + responseFromServer);
        dos.write(responseBuffer.toByteArray());
        dos.flush();
    }
}
