package org.example.server;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    ProcessBuilder processBuilder;
    Process process;
    public Client(ProcessBuilder processBuilder, Process process)  {
        this.process= process;
        this.processBuilder= processBuilder;
    }

    @Override
    public void run() {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            System.out.println(response);
            processBuilder.command(
                    new String[]{"curl", "http://localhost:1221"});

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
