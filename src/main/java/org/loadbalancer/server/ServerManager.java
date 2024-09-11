package org.loadbalancer.server;

import java.text.CompactNumberFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ServerManager {
    /**
     * Using ServerManager class to make server objs created in MultipleServer.java
     * available to LoadBalancer.java. Previously, LoadBalancer.java was creat
     */
    private static final ConcurrentHashMap<Integer, Server> serverObjs= new ConcurrentHashMap<>();
    public static Server getOrCreateServer(int port) {
        return serverObjs.computeIfAbsent(port, Server::new); //if key already exists in map, return server obj, if key does not exist, create new server obj
    }
    public static Server getServer(int port) {
        return serverObjs.get(port);
    }
}
