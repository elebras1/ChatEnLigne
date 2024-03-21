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
    private Object message;
    
    public Object getmessage(){
        return this.message;
    }
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

    public void startReadMessages() {
        Thread readThread = new Thread(() -> {
            try {
                this.readMessages();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        readThread.start();
    }

    public void readMessages() throws IOException, ClassNotFoundException {
        while (true) {
            message = this.input.readObject();
        }
    }

    public void deconnexion() {
        try {
            if (this.output != null)
                this.output.close();
            if (this.input != null)
                this.input.close();
            if (this.socket != null && !this.socket.isClosed())
                this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
