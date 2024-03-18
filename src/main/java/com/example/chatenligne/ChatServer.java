package com.example.chatenligne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private List<ObjectOutputStream> clientsOutputStreams = new ArrayList<>();

    public static void main(String[] args) {
        ChatServer sv = new ChatServer();
        sv.startsv("5555");
    }

    public void startsv(String port) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                clientsOutputStreams.add(output);

                Thread clientHandler = new Thread(new ClientHandler(socket));
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream input;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                input = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = (String) input.readObject();
                    if (message == null) {
                        // Client disconnected, close the connection and break the loop
                        clientSocket.close();
                        break;
                    }
                    System.out.println("Message received: " + message);
                    broadcastMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e);
            } finally {
                // Remove client's output stream when client disconnects
                try {
                    clientsOutputStreams.remove(clientSocket.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void broadcastMessage(String message) {
            for (ObjectOutputStream clientOutput : clientsOutputStreams) {
                try {
                    clientOutput.writeObject(message);
                    clientOutput.flush();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }
}
