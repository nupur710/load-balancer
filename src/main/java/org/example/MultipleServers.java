package org.example;

import org.example.server.Server;
public class MultipleServers {
    public static void main(String[] args) {
        int noOfServers= 1;
        int startingPort= 8080;
        for(int i= 0; i< noOfServers; i++) {
            Server server= new Server(startingPort);
            Thread threadServer= new Thread(server);
            threadServer.start();
            startingPort++;
        }
    }
}
