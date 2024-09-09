package org.loadbalancer.algorithms;

import org.loadbalancer.server.Server;

import java.util.List;
//Strategy design pattern
public interface LoadBalancerStrategy {
    Server selectServer(List<Server> healthyServers);
}
