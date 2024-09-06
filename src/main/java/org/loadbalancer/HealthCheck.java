package org.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HealthCheck implements Runnable {
    private final List<Server> ports;
    private final List<Server> healthyServer;
    private final ScheduledExecutorService scheduler;
    private final int checkInterval;
    HealthCheck(List<Server> ports, int checkInterval) {
        this.ports= ports;
        healthyServer= new ArrayList<>();
        this.scheduler= Executors.newScheduledThreadPool(1);
        this.checkInterval= checkInterval;
    }

    public void startScheduler() {
        scheduler.scheduleAtFixedRate(this, 0, checkInterval,TimeUnit.SECONDS);
    }
    @Override
    public void run() {
        for (Server s1 : ports) {
            if(isServerHealthy(s1)) {
                if(healthyServer.contains(s1)) {
                    continue; //we compare based on port no because arraylist allows duplicate values
                }
                healthyServer.add(s1);
                System.out.println("added to lb "+ s1.getPort());
                System.out.println(healthyServer);
            } else {
                healthyServer.remove(s1);
                System.out.println("removed from lb " + s1.getPort());
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
