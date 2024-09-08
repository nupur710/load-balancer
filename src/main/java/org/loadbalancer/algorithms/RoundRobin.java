package org.loadbalancer.algorithms;

import org.loadbalancer.server.Server;

import java.util.List;

public class RoundRobin implements LoadBalancerStrategy{
    static private int currentIndex= 0;
    @Override
    public Server selectServer(List<Server> healthyServers) {
        Server selectedServer= healthyServers.get(currentIndex);
        currentIndex= (currentIndex + 1) % healthyServers.size();
        return selectedServer;
    }
}
