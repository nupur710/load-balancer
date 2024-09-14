package org.loadbalancer;

import org.loadbalancer.server.MultipleServers;

/**
 * We start both the servers and load balancer from the same process (same jvm).
 *  Previously when running them as different programs meant they have different
 *  memory spaces i.e., when servers were created from MultipleServers.java
 *  & stored server objs in ConcurrentHashMap serverObj, they were not accessible to
 *  LoadBalancer.java class
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Start servers
        new Thread(() -> MultipleServers.main(args)).start();
        Thread.sleep(1000);
        // Start load balancer
        new Thread(() -> LoadBalancer.main(args)).start();
    }
}
