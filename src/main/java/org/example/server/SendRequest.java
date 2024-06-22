package org.example.server;

import java.io.IOException;

public class SendRequest {

    public static void main(String[] args) throws IOException {
        while (true) {
            Client client1 = new Client(1221);
            Thread requests = new Thread(client1);
            requests.start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
