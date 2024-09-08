package org.loadbalancer.algorithms;

import org.loadbalancer.Server;

import java.util.List;

public interface LoadBalancerStrategy {
    Server selectServer(List<Server> healthyServers);
}
