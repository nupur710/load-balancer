package org.loadbalancer.algorithms;

import org.loadbalancer.Server;

import java.util.*;

public class WeightedRoundRobin implements LoadBalancerStrategy{

    private Map<Server, Integer> serverWeights= new HashMap<>();
    private Map<Server, Integer> currentWeights= new HashMap<>();
    private int currentIndex= 0;
    public WeightedRoundRobin() {}

    private void assignWeights(List<Server> healthyServers) {

    }

    @Override
    public Server selectServer(List<Server> healthyServers)
    {
        List<Integer> weight= assignWeight(healthyServers);
        int index= getMaxIndex(weight);
        Server selectedServer= healthyServers.get(index);
        return selectedServer;
    }

    private int getMaxIndex(List<Integer> weight) {
        int max= weight.get(0);
        int index= 0;
        for(int i= 0; i< weight.size(); i++) {
            if(weight.get(i) > max) {
                max = weight.get(i);
                index = i;
            }
        }
        return index;
    }

    private List<Integer> assignWeight(List<Server> healthyServers) {
        List<Integer> weights= new ArrayList<>(healthyServers.size());
        int w= 100;
        for(int i= 0; i<healthyServers.size(); i++) {
            weights.add(w);
            w /= 2;
        } return weights;
    }
}
