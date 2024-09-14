package org.loadbalancer.client;

import org.apache.log4j.Logger;
import org.loadbalancer.server.MultipleServers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendRequest {

    private static final Logger logger= Logger.getLogger(SendRequest.class);
    private static Properties props= new Properties();
    public static void main(String[] args) throws IOException, InterruptedException {
        try(InputStream inputStream = MultipleServers.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(inputStream);
        } catch (IOException e) {
            logger.error("Error loading properties file: " + e.getMessage());
        }
        String userHome = System.getProperty("user.home");
        int loadBalancerPort= Integer.parseInt(props.getProperty("loadBalancer.port"));
        ExecutorService exec= Executors.newFixedThreadPool(5);

        while (true) {
            System.out.println(File.listRoots());
            String cmd= "curl http://localhost:"+loadBalancerPort;
            ProcessBuilder processBuilder= new ProcessBuilder(cmd.split(" "));
            processBuilder.directory(new File(userHome));
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
