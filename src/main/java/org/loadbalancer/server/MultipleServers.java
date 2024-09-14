package org.loadbalancer.server;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MultipleServers {
    private static Logger logger= Logger.getLogger(MultipleServers.class);
    static Properties props= new Properties();
    public static void main(String[] args) {
        try(InputStream inputStream = MultipleServers.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(inputStream);
        } catch (IOException e) {
            logger.error("Error loading properties file: " + e.getMessage());
        }
            int noOfServers= Integer.parseInt(props.getProperty("noOfServers"));
            int startingPort= Integer.parseInt(props.getProperty("server.startPort"));
            for(int i= 0; i< noOfServers; i++) {
                Server server= ServerManager.getOrCreateServer(startingPort);
                Thread threadServer= new Thread(server);
                threadServer.start();
                startingPort++;
            }
    }
}
