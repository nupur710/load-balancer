package org.loadbalancer.algorithms;

import org.loadbalancer.server.Server;

import java.util.ArrayList;
import java.util.List;

public class WeightedRoundRobin implements LoadBalancerStrategy{

    private int currentIndex= -1; //start at -1 because round robin begins from server at index 0 in healthy server list
    private int currentWeight= 0;
    private List<Integer> weights;

    @Override
    public Server selectServer(List<Server> healthyServers)
    {
        initializeWeight(healthyServers);
        int noOfServers= healthyServers.size();
        int maxWeight= getMaxWeight(weights);
        while(true) {
            //Increment current index
            currentIndex= (currentIndex + 1) % noOfServers;
            if(currentIndex== 0) { //we have gone through all the servers, decrease current weight by 1
                currentWeight -= 1;
                if (currentWeight <= 0) { //if max weight becomes 0 or less, reset it
                    currentWeight= maxWeight;
                }
            }
            if(weights.get(currentIndex) >= currentWeight) {
                return healthyServers.get(currentIndex);
            }
        }
    }

    private void initializeWeight(List<Server> healthyServers) {
        weights= assignWeight(healthyServers);
    }
    private int getMaxWeight(List<Integer> weight) {
        int max= weight.get(0);
        for(int i= 0; i< weight.size(); i++) {
            if(weight.get(i) > max) {
                max = weight.get(i);
            }
        }
        return max;
    }

    private List<Integer> assignWeight(List<Server> healthyServers) {
        List<Integer> weights= new ArrayList<>(healthyServers.size());
        for(int i= 0; i<healthyServers.size(); i++) {
            weights.add((i+1));
        } return weights;
    }
}
