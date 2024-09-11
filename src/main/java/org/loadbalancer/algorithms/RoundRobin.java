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
        if(sessionPersistence.get(hostIp)!= null && !isSessionExpired(selectedServer)) {
            selectedServer= sessionPersistence.get(hostIp);
        } else {
            selectedServer = healthyServers.get(currentIndex);
            sessionPersistence.put(hostIp, selectedServer);
            currentIndex = (currentIndex + 1) % healthyServers.size();
        }
        return selectedServer;
    }

    private boolean isSessionExpired(Server selectedServer) {
        if(selectedServer== null) return false;
        long ct= System.currentTimeMillis();
        System.out.println("current time is " + ct);
        long lastAccess= selectedServer.getLastAccessedTime();
        System.out.println("server was last accessed at " + lastAccess);
        System.out.println("diff is " + (ct-lastAccess));
        return System.currentTimeMillis() - selectedServer.getLastAccessedTime() > this.timeOut;

    }
}
