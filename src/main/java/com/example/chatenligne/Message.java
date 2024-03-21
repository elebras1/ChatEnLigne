package com.example.chatenligne;

import java.io.Serializable;

public class Message implements Serializable {
    private String auteur;
    private String contenu;


    public Message(String auteur, String contenu) {
        this.auteur = auteur;
        this.contenu = contenu;
    }

    public Message(String contenu) {
        this.auteur = "Anonyme";
        this.contenu = contenu;
    }
}
