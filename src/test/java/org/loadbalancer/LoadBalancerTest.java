package org.loadbalancer;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Timeout;
import org.loadbalancer.server.Server;
import org.loadbalancer.server.ServerManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class LoadBalancerTest {

    private List<Server> servers = new ArrayList<>();
    private Thread loadBalancerThread;
    private Thread[] serverThreads;

    private final int LOAD_BALANCER_PORT = 1221;
    private final int START_SERVER_PORT = 8080;
    private final int NO_OF_SERVERS = 3;

    @Before
    public void setUp() throws InterruptedException {
        serverThreads = new Thread[NO_OF_SERVERS];
        for (int i = 0; i < NO_OF_SERVERS; i++) {
            Server server = ServerManager.getOrCreateServer(START_SERVER_PORT + i);
            servers.add(server);
            serverThreads[i] = new Thread(server);
            serverThreads[i].start();
        }
        loadBalancerThread = new Thread(() -> LoadBalancer.main(new String[]{}));
        loadBalancerThread.start();
        Thread.sleep(2000);
    }
    @Timeout(60000)
    @Test
    public void testLoadBalancerDistributesRequests() throws IOException {
        for (int i = 0; i < NO_OF_SERVERS; i++) {
            try (Socket socket = new Socket("localhost", LOAD_BALANCER_PORT);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                writer.println("GET / HTTP/1.1");
                writer.println("Host: localhost");
                writer.println("Connection: close");
                writer.println();
                String responseLine;
                StringBuilder response = new StringBuilder();
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine).append("\n");
                }
                boolean validResp = response.toString().contains("Response 200OK");
                assertTrue(validResp);
            }
        }
    }
}
