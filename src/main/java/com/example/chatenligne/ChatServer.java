package com.example.chatenligne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private List<ObjectOutputStream> clientsOutputStreams = new ArrayList<>();

    public static void main(String[] args) {
        ChatServer sv = new ChatServer();
        sv.startsv("5555");
        try {
            sv.listenMulticastRequest("230.0.0.0", 4446, "localhost", 5555);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startsv(String port) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouveau client connecté: " + socket.getInetAddress().getHostAddress());

                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                this.clientsOutputStreams.add(output);

                Thread clientHandler = new Thread(new ClientHandler(socket));
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void listenMulticastRequest(String multicastAddress, int multicastPort, String serverAddress, int serverPort) throws IOException {
        InetAddress group = InetAddress.getByName(multicastAddress);
        MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
        multicastSocket.joinGroup(group);
        byte[] buffer = new byte[1000];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            multicastSocket.receive(packet);
            String response = serverAddress + ":" + serverPort;
            DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.length(), packet.getAddress(), packet.getPort());
            multicastSocket.send(responsePacket);
        }
    }

    class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream input;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.input = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Message message = (Message) this.input.readObject();
                    if (message == null) {
                        // Client disconnected, close the connection and break the loop
                        this.clientSocket.close();
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
                    clientsOutputStreams.remove(this.clientSocket.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void broadcastMessage(Message message) {
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
