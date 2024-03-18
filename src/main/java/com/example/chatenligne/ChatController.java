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
        if(!this.entreeAdresseIP.getText().isEmpty() && this.entreePort.getText().isEmpty()) {
            this.client.openConnexion(this.entreeAdresseIP.getText(), Integer.parseInt(this.entreePort.getText()));
            this.labelEtatConnexion = new Label("Connecté");
        }
    }

    @FXML
    void actionBoutonDeconnexion(ActionEvent event) {
        this.client.deconnexion();
    }

    @FXML
    void actionBoutonEnvoyer(ActionEvent event) {
        /*if(!this.entreePseudo.getText().isEmpty() && !this.entreeMessage.getText().isEmpty()) {
            this.client.addPseudo(this.entreePseudo.getText());
            //this.client.addMessage(this.entreeMessage.getText());
        }*/
    }

    @FXML
    void initialize() {

    }

}