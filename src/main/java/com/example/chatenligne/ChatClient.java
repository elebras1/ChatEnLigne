package com.example.chatenligne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public static void main(String[] args) {
        String host = "22HP014";
        int port = 5555;
        ChatClient client = new ChatClient();
        try {
            client.openConnexion(host, port);
            client.addPseudo("Erwan");
            client.addMessage("c'est un message");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openConnexion(String host, int port){
        InetAddress adr = null;
        try {
            adr = InetAddress.getByName(host);
            socket = new Socket(adr, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPseudo(String pseudo) {
        try {
            this.output.writeObject(pseudo);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String message) {
        try {
            this.output.writeObject(message);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void deconnexion() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
