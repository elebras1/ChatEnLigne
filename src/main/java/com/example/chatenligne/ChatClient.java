package com.example.chatenligne;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public static void main(String[] args) {

    }

    public void openConnexion(String host, int port) throws IOException {
        InetAddress adr = InetAddress.getByName(host);
        socket = new Socket(adr, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessage(String pseudo, String message) {
        Message msg;
        if (pseudo.isEmpty()) {
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
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        readThread.start();
    }

    public void readMessages() throws ClassNotFoundException {
        while (true) {
            try {
                Object message = this.input.readObject();
                if (message == null) {
                    System.out.println("Le serveur s'est déconnecté.");
                }
            } catch (IOException e) {
                System.out.println("Erreur de lecture depuis le serveur: " + e.getMessage());
                break;
            }
        }
    }

    public ObjectInputStream getInput() {
        return this.input;
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
                break;
            }
            try {
                socket.leaveGroup(group);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            socket.close();
        }
    }
}
