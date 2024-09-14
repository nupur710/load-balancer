package org.loadbalancer.client;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendRequest {

    private static final Logger logger= Logger.getLogger(SendRequest.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService exec= Executors.newFixedThreadPool(5);
        while (true) {
            String cmd= "curl http://localhost:1221";
            ProcessBuilder processBuilder= new ProcessBuilder(cmd.split(" "));
            processBuilder.directory(new File("\\Users\\lenovo"));
            Process process = processBuilder.start();
            Client client1 = new Client(process);
            exec.submit(client1);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Curl command failed with exit code " + exitCode);
            }
            process.destroy();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
