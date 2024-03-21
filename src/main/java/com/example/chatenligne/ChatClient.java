package com.example.chatenligne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5555;
        ChatClient client = new ChatClient();
        try {
            client.openConnexion(host, port);
            // thread pour lire les messages en continue
            Thread readThread = new Thread(() -> {
                try {
                    client.readMessages();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

            client.sendMessage("Erwan", "Bonjour tout le monde");

            // client.deconnexion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openConnexion(String host, int port) throws IOException {
        InetAddress adr = InetAddress.getByName(host);
        socket = new Socket(adr, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessage(String pseudo, String message) {
        Message msg;
        if(pseudo.isEmpty()) {
            msg = new Message(message);
        } else {
            msg = new Message(pseudo, message);
        }
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readMessages() throws IOException, ClassNotFoundException {
        while (true) {
            Object message = this.input.readObject();
            if (message instanceof String) {
                System.out.println("Message reçu : " + message);
            }
        }
    }

    public void deconnexion() {
        try {
            if (output != null)
                output.close();
            if (input != null)
                input.close();
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
