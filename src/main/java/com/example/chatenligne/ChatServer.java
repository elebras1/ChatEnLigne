package com.example.chatenligne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
            MulticastReceiver mulR = new MulticastReceiver();
            mulR.start();

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

    public class MulticastPublisher {
        private DatagramSocket socket;
        private InetAddress group;
        private byte[] buf;

        public void multicast(String multicastMessage) throws IOException {
            socket = new DatagramSocket();
            group = InetAddress.getByName("230.0.0.0");
            buf = multicastMessage.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
            socket.send(packet);
            socket.close();
        }
    }

    public class MulticastReceiver extends Thread {
        protected MulticastSocket socket = null;
        protected byte[] buf = new byte[256];

        public void run() {
            try {
                socket = new MulticastSocket(4446);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            InetAddress group = null;
            try {
                group = InetAddress.getByName("230.0.0.0");
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            try {
                socket.joinGroup(group);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String received = new String(
                        packet.getData(), 0, packet.getLength());
                System.out.println("Message reçu : " + received);
                if ("230.0.0.0:4446".equals(received)) {
                    MulticastPublisher mulP = new MulticastPublisher();
                    try {
                        mulP.multicast("localhost:5555");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            try {
                socket.leaveGroup(group);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            socket.close();
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
