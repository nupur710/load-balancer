package org.loadbalancer.algorithms;

import org.loadbalancer.server.Server;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LeastConnections implements LoadBalancerStrategy{
    private ConcurrentHashMap<InetAddress, Server> sessionPersistence= new ConcurrentHashMap<>();
    private Server selectedServer;
    private final long timeOut;
    public LeastConnections(long timeOut) {
        this.timeOut= timeOut;
    }

    @Override
    public Server selectServer(List<Server> healthyServers, InetAddress hostIp) {
        if(sessionPersistence.get(hostIp) != null && !isSessionExpired(selectedServer, this.timeOut)) {
            System.out.println("Selected serv is " + selectedServer);
        } else {
            selectedServer= healthyServers.stream()
                    .min(Comparator.comparingInt(Server::getActiveConnections))
                    .orElse(null);
            sessionPersistence.put(hostIp, selectedServer);
        } return selectedServer;
    }
}
