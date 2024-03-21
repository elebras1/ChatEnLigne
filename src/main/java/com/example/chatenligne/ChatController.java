package com.example.chatenligne;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
    private boolean estConnecte = false;

    @FXML
    void actionBoutonConnexion(ActionEvent event) {
        if(!this.estConnecte) {
            try {
                if (!this.entreeAdresseIP.getText().isEmpty() && !this.entreePort.getText().isEmpty()) {
                    MulticastSocket socket = new MulticastSocket(4446);
                    InetAddress group = InetAddress.getByName("224.0.0.0");
                    socket.joinGroup(group);

                    // Créez un nouveau thread de lecture pour chaque connexion
                    Thread readThread = new Thread(() -> {
                        try {
                            while (true) {
                                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                                socket.receive(packet);
                                String received = new String(packet.getData(), 0, packet.getLength());
                                System.out.println("Message reçu : " + received);
                                String ht[] = received.split(":");
                                if (ht[0].equals(received.substring(0, 8))) {
                                    try {
                                        this.client.openConnexion(ht[0], Integer.parseInt(ht[1]));
                                        this.estConnecte = true;
                                    } catch (NumberFormatException | IOException e) {
                                        e.printStackTrace();
                                    }
                                    Platform.runLater(() -> this.labelEtatConnexion.setText("Connecté"));
                                    this.startReadMessages();
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            // Fermez le socket lorsque la connexion est terminée
                            if (!socket.isClosed()) {
                                socket.close();
                            }
                        }
                    });

                    readThread.start();
                    MulticastPublisher mp = new MulticastPublisher(); // Fournir la définition de MulticastPublisher
                    mp.multicast(this.entreeAdresseIP.getText() + ":" + this.entreePort.getText());
                }
            } catch (Exception e) {
                this.labelEtatConnexion.setText("Erreur de connexion");
            }
        }
    }

    @FXML
    void actionBoutonDeconnexion(ActionEvent event) {
        if(this.estConnecte) {
            this.client.deconnexion();
            this.estConnecte = false;
            this.labelEtatConnexion.setText("Déconnecté");
        }
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