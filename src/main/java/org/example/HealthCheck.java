package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HealthCheck implements Runnable {
    private final List<Server> ports;
    private final List<Server> healthyServer;
    HealthCheck(List<Server> ports) {
        this.ports= ports;
        healthyServer= new ArrayList<>();
    }
    @Override
    public void run() {
        for (Server s1 : ports) {
            if(isServerHealthy(s1)) {
                healthyServer.add(s1);
            }
        }
    }
    private boolean isServerHealthy(Server server) {
        try {
            Process process = new ProcessBuilder("curl", "-I", "http://localhost:" + server.getPort()) //http head request
                    .redirectErrorStream(true).start();
            int exitCode= process.waitFor();
            return exitCode== 0;
        } catch (Exception e) {
            System.err.println("Error sending HTTP HEAD request to check health of server");
            return false;
        }
    }
    public List<Server> getHealthyServers() {
        return healthyServer;
    }
}
