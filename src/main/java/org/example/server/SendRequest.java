package org.example.server;

import java.io.File;
import java.io.IOException;

public class SendRequest {

    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
            String cmd= "curl http://localhost:1221";
            ProcessBuilder processBuilder= new ProcessBuilder(cmd.split(" "));
            processBuilder.directory(new File("\\Users\\lenovo"));
            Process process = processBuilder.start();
            Client client1 = new Client(processBuilder, process);
            Thread requests = new Thread(client1);
            requests.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
            } else {
                System.err.println("Curl command failed with exit code " + exitCode);
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
