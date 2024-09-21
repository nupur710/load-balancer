package org.loadbalancer;


import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.loadbalancer.server.Server;
import static org.junit.Assert.assertEquals;

public class ServerTest {
    private Server server;
    private final int PORT= 8080;

    @BeforeEach
    void setUp() {
        server= new Server(PORT);
    }
    @Test
    void testGetPort() {
        assertEquals(PORT, server.getPort());
    }
    @Test
    void parseRequest() {}
    @Test
    void testRun() {}
    @Test
    void testGetActiveConnections() {}
    @Test
    void testEquals() {}
    @Test
    void getLastAccessedTime() {}
}
