package org.loadbalancer;

import org.apache.log4j.Logger;
import org.loadbalancer.algorithms.LeastConnections;
import org.loadbalancer.algorithms.LoadBalancerStrategy;
import org.loadbalancer.algorithms.WeightedRoundRobin;
import org.loadbalancer.algorithms.RoundRobin;
import org.loadbalancer.server.MultipleServers;
import org.loadbalancer.server.Server;
import org.loadbalancer.server.ServerManager;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBalancer {
    static Properties props= new Properties();
    static private byte[] buffer = new byte[8192];
    static private LoadBalancerStrategy strategy;
    private static final Logger logger= Logger.getLogger(LoadBalancer.class);
    public static void main(String[] args) {
        try(InputStream inputStream = LoadBalancer.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(inputStream);
        } catch (IOException e) {
            logger.error("Error loading properties file: " + e.getMessage());
        }
        String loadBalancerStrategy= props.getProperty("loadbalancer.strategy");
        long sessionTimeout= Long.parseLong(props.getProperty("sessionTimeout"));
        int noOfServersToRun= Integer.parseInt(props.getProperty("noOfServers"));
        int serversStartPort= Integer.parseInt(props.getProperty("server.startPort"));
        int healthCheckInterval= Integer.parseInt(props.getProperty("healthCheck.interval"));
        int loadBalancerPort= Integer.parseInt(props.getProperty("loadBalancer.port"));
        strategy= getLoadBalancerStrategyObj(loadBalancerStrategy, sessionTimeout);
        List<Server> serversRunning = new ArrayList<>();
        for (int i = 0; i < noOfServersToRun; i++) {
            serversRunning.add(ServerManager.getServer(serversStartPort+i));
        }
        HealthCheck healthCheck = new HealthCheck(serversRunning, healthCheckInterval);
        healthCheck.startScheduler();

        try (ServerSocket loadBalancer = new ServerSocket(loadBalancerPort)) {
            //Handle incoming requests in multiple threads
            ExecutorService threadPool = Executors.newFixedThreadPool(1);
            while (true) {
                Socket lbSocket = loadBalancer.accept();
                InetAddress hostIp= lbSocket.getInetAddress();
                threadPool.submit(() -> {
                    handleRequest(lbSocket, healthCheck, hostIp);
                });
            }
        } catch (IOException e) {
            logger.error("Error in load balancer: " + e.getMessage());
        }
    }

    private static void handleRequest(Socket lbSocket, HealthCheck healthCheck, InetAddress hostIp) {
        try (DataInputStream lbClient= new DataInputStream(lbSocket.getInputStream());
            DataOutputStream lbClientOutput= new DataOutputStream(lbSocket.getOutputStream());) {
            // Refresh the list of healthy servers for every client request
            List<Server> healthyServers = healthCheck.getHealthyServers();
            if (healthyServers.isEmpty()) {
                logger.error("No healthy server available");
                lbClientOutput.writeUTF("503 Service Unavailable: No healthy servers");
                return;
            }
            Server selectedServer = strategy.selectServer(healthyServers, hostIp);
            try (Socket connectToServer = new Socket("localhost", selectedServer.getPort());
                 DataOutputStream lbServer = new DataOutputStream(connectToServer.getOutputStream());
                 DataInputStream lbServerResp = new DataInputStream(connectToServer.getInputStream())) {
                // Send client request to selected server
                int bytesRead = sendRequest(lbClient, lbServer);

                // Read response from server and send it back to client
                readResponse(lbClientOutput, lbServerResp, bytesRead);

            } catch (IOException e) {
                logger.error("Error connecting to server: " + e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Error handling client connection: " + e.getMessage());
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
            logger.info("Load balancer ---> Resp received from server:\n" + responseFromServer);
            dos.write(responseBuffer.toByteArray());
            dos.flush();
    }

    private static LoadBalancerStrategy getLoadBalancerStrategyObj(String loadBalancerStrategy, long timeout) {
        switch (loadBalancerStrategy) {
            case "Round Robin":
                return new RoundRobin(timeout);
            case "Weighted Round Robin":
                return new WeightedRoundRobin(timeout);
            case "Least Connections":
                return new LeastConnections(timeout);
            default:
                return new RoundRobin(timeout);
        }
    }
}
