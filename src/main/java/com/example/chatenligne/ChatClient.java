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
            client.addPseudo("Erwan");

            // Créez un thread pour lire les messages du serveur en continu
            Thread readThread = new Thread(() -> {
                try {
                    client.readMessages();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

            // Envoi de messages
            client.sendMessage("Bonjour, je suis Erwan!");

            // Vous pouvez ajouter d'autres messages à envoyer

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

    public void addPseudo(String pseudo) throws IOException {
        output.writeObject(pseudo);
    }

    public void sendMessage(String message) throws IOException {
        output.writeObject(message);
    }

    public void readMessages() throws IOException, ClassNotFoundException {
        while (true) {
            Object received = input.readObject();
            if (received instanceof String) {
                String message = (String) received;
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
