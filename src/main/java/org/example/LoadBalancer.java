package org.example;

import org.example.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadBalancer {
    static private int serversStartPort= 8080;
    static private int noOfServersToRun= 1;
    static private int portToRun= serversStartPort;
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket loadBalancer= new ServerSocket(1221); //client will send request to port 1221
        while(true) {
            Socket lbSocket = loadBalancer.accept();
            //Read request from client
            DataInputStream lbClient= new DataInputStream(lbSocket.getInputStream());
            if(portToRun > (serversStartPort+noOfServersToRun)-1) { portToRun= serversStartPort; }
            Socket connectToServer = new Socket("localhost", portToRun);
            portToRun++;
            //Send request to server
            DataOutputStream lbServer= new DataOutputStream(connectToServer.getOutputStream());
            byte[] buffer= new byte[8192];
            int bytesRead;
            while((bytesRead= lbClient.read(buffer)) != -1) //reads buffer.length bytes of data from input stream & stores it into buffer array
            {
                System.out.print((char) bytesRead);
                lbServer.write(buffer, 0, bytesRead);
            }
            lbServer.flush();
//            DataInputStream inputStream = new DataInputStream(lbSocket.getInputStream());
//            String ip = inputStream.readUTF();
            //if(lbSocket.isConnected()) System.out.println("connected!");





            //System.out.println("To loadbalancer: " + ip);

            //DataOutputStream dataOutputStream = new DataOutputStream(connectToServer.getOutputStream());
            //dataOutputStream.writeUTF(ip);

            /**Read req from client **/
            DataInputStream clientInputStream= new DataInputStream(lbSocket.getInputStream());
            StringBuilder requestBuilder= new StringBuilder();
            String line;
            while(!(line= clientInputStream.readLine()).isEmpty()) {
                requestBuilder.append(line);
            }

            System.out.println(" * * * " + requestBuilder);
            /**Write req to server **/
            DataOutputStream writeToServerStream= new DataOutputStream(connectToServer.getOutputStream());
            writeToServerStream.writeBytes(requestBuilder.toString());
            //Read response from server
            DataInputStream serverStreamResponse= new DataInputStream(connectToServer.getInputStream());
//            byte[] buffer= new byte[1024];
//            int bytesRead;
//            DataOutputStream lbOutputStream= new DataOutputStream(lbSocket.getOutputStream());
//            while((bytesRead = serverStreamResponse.read(buffer)) != -1) {
//                System.out.println((char)bytesRead);
//                lbOutputStream.write(buffer, 0, bytesRead);
//            }

            connectToServer.close();
            lbSocket.close();

//            dataOutputStream.close();
//            inputStream.close();

        }
    }
}
