package org.example;

import org.example.server.Server;
public class MultipleServers {
    public static void main(String[] args) {
        int noOfServers= 3;
        int j= 8080;
        for(int i= 0; i< noOfServers; i++) {
            Server server= new Server(j);
            Thread threadServer= new Thread(server);
            threadServer.start();
            j++;
        }
    }
}
