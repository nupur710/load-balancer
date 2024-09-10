package org.loadbalancer.algorithms;

import org.loadbalancer.server.Server;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RoundRobin implements LoadBalancerStrategy{
    static private int currentIndex= 0;
    private ConcurrentHashMap<InetAddress, Server> sessionPersistence= new ConcurrentHashMap<>();
    @Override
    public Server selectServer(List<Server> healthyServers, InetAddress hostIp) {
        Server selectedServer= null;

        if(sessionPersistence.get(hostIp)!= null) {
            selectedServer= sessionPersistence.get(hostIp);
        } else {
            selectedServer = healthyServers.get(currentIndex);
            sessionPersistence.put(hostIp, selectedServer);
            currentIndex = (currentIndex + 1) % healthyServers.size();
        }
        return selectedServer;
    }
}
