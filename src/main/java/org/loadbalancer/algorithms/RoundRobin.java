package org.loadbalancer.algorithms;

import org.loadbalancer.server.Server;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RoundRobin implements LoadBalancerStrategy{
    static private int currentIndex= 0;
    private Server selectedServer;
    private final long timeOut;
    public RoundRobin(long timeOut) {
        this.timeOut= timeOut;
    }
    private ConcurrentHashMap<InetAddress, Server> sessionPersistence= new ConcurrentHashMap<>();
    @Override
    public Server selectServer(List<Server> healthyServers, InetAddress hostIp) {
        if(sessionPersistence.get(hostIp)!= null && !isSessionExpired(selectedServer, this.timeOut)) {
            selectedServer= sessionPersistence.get(hostIp);
        } else {
            selectedServer = healthyServers.get(currentIndex);
            sessionPersistence.put(hostIp, selectedServer);
            currentIndex = (currentIndex + 1) % healthyServers.size();
        }
        return selectedServer;
    }

}
