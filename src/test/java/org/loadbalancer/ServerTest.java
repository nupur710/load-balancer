package org.loadbalancer;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.loadbalancer.server.Server;
import org.loadbalancer.server.ServerManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ServerTest {
    private Server server;
    private final int PORT= 8080;

    @Before
    public void setUp() {
        server= ServerManager.getOrCreateServer(PORT);
    }
    @Test
    public void testGetPort() {
        assertEquals(PORT, server.getPort());
    }
    @Test
    public void parseRequestTest() {
        String request= "GET / HTTP/1.1Host: localhost:8080User-Agent: curl/8.7.1Accept: */*";
        assertTrue(server.parseRequest(request).contains("Response 200OK successfully received from server running at port " + PORT));
    }
    @Test
    public void testRunAndActiveConnections() {
        Thread thread= new Thread(server);
        thread.start();
        HttpGet httpGet= new HttpGet("http://localhost:"+PORT);
        try(CloseableHttpClient httpClient= HttpClients.createDefault()) {
            try(CloseableHttpResponse response= httpClient.execute(httpGet)) {
                int activeConnections= server.getActiveConnections();
                assertTrue(server.getActiveConnections()==1);
                assertTrue(response.getCode()==200);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    @Test
    public void testEquals() {
        Server server1= new Server(PORT);
        assertEquals(server, server1);
        assertEquals(server.hashCode(), server1.hashCode());
    }
    @Test
    public void getLastAccessedTime() {
        long time= System.currentTimeMillis();
        server.setLastAccessedTime(time);
        assertEquals(server.getLastAccessedTime(), time);
    }
}
