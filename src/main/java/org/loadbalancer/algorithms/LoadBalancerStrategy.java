package org.loadbalancer.algorithms;

import org.loadbalancer.server.Server;

import java.util.List;

public interface LoadBalancerStrategy {
    Server selectServer(List<Server> healthyServers);
}
