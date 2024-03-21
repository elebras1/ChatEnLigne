package com.example.chatenligne;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController extends ChatClient {
    private final ChatClient client = new ChatClient();

    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    Thread readThread = new Thread(() ->{
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
            if ("localhost:5555".equals(received)) {
                String ht[]=received.split(":");
                try {
                    this.client.openConnexion(ht[0], Integer.parseInt(ht[1]));
                } catch (NumberFormatException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                this.labelEtatConnexion.setText("Connecté");
                this.startReadMessages();
                break;
            }
        }
        try {
            socket.leaveGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socket.close();
    });
    

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea areaDiscussion;

    @FXML
    private TextField entreeAdresseIP;

    @FXML
    private TextField entreeMessage;

    @FXML
    private TextField entreePort;

    @FXML
    private TextField entreePseudo;

    @FXML
    private Label labelEtatConnexion;

    @FXML
    void actionBoutonConnexion(ActionEvent event) {
        try {
            if(!this.entreeAdresseIP.getText().isEmpty() && !this.entreePort.getText().isEmpty()) {
                MulticastPublisher mp=new MulticastPublisher();
                this.readThread.start();
                mp.multicast(this.entreeAdresseIP.getText()+":"+this.entreePort.getText());
            }
        } catch(Exception e) {
            this.labelEtatConnexion.setText("Erreur de connexion");
        }
    }

    @FXML
    void actionBoutonDeconnexion(ActionEvent event) {
        this.client.deconnexion();
        this.labelEtatConnexion.setText("Déconnecté");
    }

    @FXML
    void actionBoutonEnvoyer(ActionEvent event) {
        if(!this.entreeMessage.getText().isEmpty()) {
            this.client.sendMessage(this.entreePseudo.getText(), this.entreeMessage.getText());
        }
    }

    @FXML
    void initialize() {

    }
    @Override
    public void readMessages() throws ClassNotFoundException{
        while(true) {
            try {
                Object message = this.client.getInput().readObject();
                if (message == null) {
                    Platform.runLater(() -> this.labelEtatConnexion.setText("deconnexion"));
                }
                if(message instanceof Message){
                    Platform.runLater(() -> this.areaDiscussion.appendText(message + "\n"));
                }
            }catch (IOException e) {
                Platform.runLater(() -> this.labelEtatConnexion.setText("deconnexion"));
                break;
            }
        }
    }
}