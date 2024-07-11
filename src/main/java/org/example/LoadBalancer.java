package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    static private int serversStartPort = 8080;
    static private int noOfServersToRun = 3;
    static private int portToRun = serversStartPort;

    public static void main(String[] args) throws IOException, InterruptedException {
        Health health = new Health(noOfServersToRun, serversStartPort);
        List<Integer> healthyServers= health.healthyServers();
        int i= 0;
        portToRun= healthyServers.get(0);
        ServerSocket loadBalancer = new ServerSocket(1221); //client will send request to port 1221
        while (true) {
            Socket lbSocket = loadBalancer.accept();
            DataInputStream lbClient = new DataInputStream(lbSocket.getInputStream());
            DataOutputStream lbClientOutput = new DataOutputStream(lbSocket.getOutputStream());
            if (portToRun >  ((serversStartPort + healthyServers.size()) - 1)) {
                portToRun = healthyServers.get(0);
                i= 0;
            }
            Socket connectToServer = new Socket("localhost", portToRun);
            i++;
            if(i < (healthyServers.size()-1)) {
                portToRun= healthyServers.get(i);
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
            //Read Response from server
            DataInputStream lbServerResp = new DataInputStream(connectToServer.getInputStream());
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();

            while ((bytesRead = lbServerResp.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, bytesRead);
            }

            String responseFromServer = responseBuffer.toString();
            System.out.println(responseFromServer);

            lbClientOutput.write(responseBuffer.toByteArray());
            lbClientOutput.flush();
            lbClient.close();
            lbClientOutput.close();
            lbServerResp.close();
            connectToServer.close();
            lbSocket.close();
        }
    }

    static class Health {
        int noOfServers;
        int startPort;

        Health(int noOfServers, int startPort) {
            this.noOfServers = noOfServers;
            this.startPort = startPort;
        }

        List<Integer> healthyServers() throws IOException {
            int port = startPort;
            List<Integer> healthyServers = new ArrayList<>();
            for (int i = 0; i < noOfServers; i++) {
                //Send request
                Process p = sendRequest(port);
                String status = response(p);
                if (status.equals("200OK")) {
                    healthyServers.add(port);
                }
                port++;
            }
            return healthyServers;
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
}