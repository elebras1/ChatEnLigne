package com.example.chatenligne;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    private final ChatClient client = new ChatClient();

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
    void actionBoutonConnexion(ActionEvent event) throws Exception {
        if(!this.entreeAdresseIP.getText().isEmpty() && !this.entreePort.getText().isEmpty()) {
            this.client.openConnexion(this.entreeAdresseIP.getText(), Integer.parseInt(this.entreePort.getText()));
            this.labelEtatConnexion.setText("Connecté");
            this.client.startReadMessages();
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
            if (this.client.getmessage() instanceof Message) {
                
            }
        }
    }

    @FXML
    void initialize() {

    }

}