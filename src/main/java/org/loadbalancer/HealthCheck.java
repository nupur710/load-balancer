package org.loadbalancer;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.log4j.Logger;
import org.loadbalancer.server.Server;

import java.io.IOException;
import java.text.ParseException;
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
    private static final Logger logger= Logger.getLogger(HealthCheck.class);
    HealthCheck(List<Server> ports, int checkInterval) {
        this.ports= ports;
        System.out.println(ports);
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
                logger.info("Server at port " + s1.getPort() + " added to healthy servers");
            } else {
                if (healthyServer.remove(s1)) {
                    logger.info("Server at port " + s1.getPort() + " removed from healthy servers");
                } else {
                    logger.info("Server at port " + s1.getPort() + " is not a healthy server");
                }
            }
        }
        logger.info("List of healthy servers: " + healthyServer);
    }

    private boolean isServerHealthy(Server server) {
        HttpGet httpGet= new HttpGet("http://localhost:"+server.getPort());
        try(CloseableHttpClient httpClient= HttpClients.createDefault()) {
            try(CloseableHttpResponse response= httpClient.execute(httpGet)) {
                return response.getCode()==200;
            }
        } catch (IOException e) {
            logger.error("Error sending HTTP HEAD request to check health of server");
            return false;
        }
    }
    private boolean isServerHealthy2(Server server) {
        try {
            Process process = new ProcessBuilder("curl", "-I", "http://localhost:" + server.getPort()) //http head request
                    .redirectErrorStream(true).start();
            int exitCode= process.waitFor();
            return exitCode== 0;
        } catch (Exception e) {
            logger.error("Error sending HTTP HEAD request to check health of server");
            return false;
        }
    }
    public synchronized List<Server> getHealthyServers() { //healthyServer list is accessed by both load balancer round robin logic & health check thread
        return new ArrayList<>(healthyServer); //return copy of healthyServer list to synchronize access to the list outside this method
        //this ensures that load balancer uses a copy of the list ensuring consistent data b/w load balancer copy & health check list
    }
}
