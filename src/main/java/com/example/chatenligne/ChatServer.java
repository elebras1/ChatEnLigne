package com.example.chatenligne;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) {
        ChatServer sv=new ChatServer();
        sv.startsv("5555");
    }
    public void startsv(String port){
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));

            while(true) {
                Socket socket = serverSocket.accept();
                try {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    while(true) {
                        String message = (String) input.readObject();
                        System.out.println(message);
                        output.writeObject(message);
                    }
                }
                catch(IOException  | ClassNotFoundException e) {
                    System.err.println(e);
                }
            }
        }
        catch(IOException e) {
            System.err.println(e);
        }
    }
}
