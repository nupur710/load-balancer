package org.example;

import org.example.server.Server;

import java.io.IOException;


public class MultipleServers {
    public static void main(String[] args) throws IOException {
        int noOfServers= 3;
        int j= 8080;
        for(int i= 0; i < noOfServers; i++) {
            new Server(j);
            j++;
        }
    }
}
