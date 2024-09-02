package org.example;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    Process process;
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
            System.out.println(response);
        } catch (IOException e) {
           System.err.println("Error reading input " + e.getMessage());
        }
    }
}
