package com.example.chatenligne;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

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
            group = InetAddress.getByName("224.0.0.0");
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
            if ("224.0.0.0:4446".equals(received)) {
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