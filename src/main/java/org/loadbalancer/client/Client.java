package org.loadbalancer.client;

import org.apache.log4j.Logger;

import java.io.*;

public class Client implements Runnable {
    Process process;
    private static final Logger logger= Logger.getLogger(Client.class);
    public Client(Process process)  {
        this.process= process;
    }

    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            logger.info("Client ---> Received response from server \n " + response);
        } catch (IOException e) {
            logger.error("Error reading input " + e.getMessage());
        }
    }
}
