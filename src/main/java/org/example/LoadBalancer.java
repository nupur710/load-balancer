package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    static private int serversStartPort = 8080;
    static private int noOfServersToRun = 5;
    static private int portToRun = serversStartPort;

    public static void main(String[] args) throws IOException, InterruptedException {
        List<Server> serversRunning= new ArrayList<>();
        for(int i= 0; i < noOfServersToRun; i++) {
            serversRunning.add(new Server(portToRun+i));
        }
        int i= 0;
        portToRun= serversRunning.get(0).getPort();
        HealthCheck healthCheck= new HealthCheck(serversRunning);
        healthCheck.run();
        List<Server> healthyServers= healthCheck.getHealthyServers();
        for ( Server s : healthyServers) {
            System.out.println("Healthy servers running at ports: " + s.getPort());
        }
        if(healthyServers.isEmpty()) System.out.println("No healthy servers");
        try (ServerSocket loadBalancer = new ServerSocket(1221);) {
            while (true) {
                try (Socket lbSocket = loadBalancer.accept();
                     DataInputStream lbClient = new DataInputStream(lbSocket.getInputStream());
                     DataOutputStream lbClientOutput = new DataOutputStream(lbSocket.getOutputStream());) {
                    if (portToRun > ((serversStartPort + noOfServersToRun) - 1)) {
                        portToRun = healthyServers.get(0).getPort();
                        i = 0;
                    }
                    Socket connectToServer = new Socket("localhost", portToRun);
                    i++;
                    if (i <= (healthyServers.size() - 1)) {
                        portToRun = healthyServers.get(i).getPort();
                    } else {
                        portToRun = healthyServers.get(i - 1).getPort() + 1;
                    }
                    //Send request to server
                    DataOutputStream lbServer = new DataOutputStream(connectToServer.getOutputStream());
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while (lbClient.available() > 0 && (bytesRead = lbClient.read(buffer)) != -1) //reads buffer.length bytes of data from input stream & stores it into buffer array
                    {
                        lbServer.write(buffer, 0, bytesRead);
                    }
                    lbServer.flush();
                    System.out.println("Send req to server");
                    //Read Response from server
                    DataInputStream lbServerResp = new DataInputStream(connectToServer.getInputStream());
                    ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
                    while ((bytesRead = lbServerResp.read(buffer)) != -1) {
                        responseBuffer.write(buffer, 0, bytesRead);
                    }
                    String responseFromServer = responseBuffer.toString();
                    System.out.println("Resp received from server:\n" + responseFromServer);
                    lbClientOutput.write(responseBuffer.toByteArray());
                    lbClientOutput.flush();
                    lbServerResp.close();
                    lbServer.close();
                    connectToServer.close();
                } catch (IOException e) {
                    System.err.println("Error handling client connection " + e.getMessage());
                    break; //break out of loop
                }
            }
        } catch (IOException e) {
            System.err.println("Error in load balancer: " + e.getMessage());
        }
    }

        Process sendRequest(int port) throws IOException {
            String cmd = "curl http://localhost:" + port;
            ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));
            processBuilder.directory(new File("\\Users\\lenovo"));
            Process process = processBuilder.start();
            return process;
        }

        String response(Process process) throws IOException {
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return response.toString().split(" ")[1];
    }
}