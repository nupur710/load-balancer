package org.loadbalancer.algorithms;

import org.loadbalancer.server.Server;

import java.net.InetAddress;
import java.util.List;
//Strategy design pattern
public interface LoadBalancerStrategy {
    Server selectServer(List<Server> healthyServers, InetAddress hostIp);
    default boolean isSessionExpired(Server selectedServer, long timeOut) {
        if (selectedServer == null) return false;
        long currentTime = System.currentTimeMillis();
        long lastAccess = selectedServer.getLastAccessedTime();
        return (currentTime - lastAccess) > timeOut;
    }
}
