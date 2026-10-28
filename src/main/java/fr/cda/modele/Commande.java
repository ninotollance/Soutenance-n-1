package fr.cda.modele;

import java.util.*;

// Classe de definition d'une commande
//
public class Commande {
    // Les caracteristiques d'une commande
    //
    private int     numero;         // numero de la commande
    private String  date;           // date de la commande. Au format JJ/MM/AAAA
    private String  client;         // nom du client
    private ArrayList<String> references;// les references des produits de la commande
    private boolean livree; // status de la commande (livrer ou non)
    private String infoCommande; // texte libre

    //Constructeur
    public Commande (int numero, String  date, String  client, ArrayList<String> references) {
        this.numero = numero;
        this.date = date;
        this.client = client;
        this.references = references;
        this.livree = false;
        this.infoCommande = "";
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();

        res.append("\n Commande            : " + numero + " \n");
        res.append("      Date               : " + date + " \n");
        res.append("      Client             : " + client + " \n");
        res.append("      RefProduits  : \n");

        for (String ref : references) {
            res.append("           " + ref + "\n");
        }
        return res.toString();
    }



    public void ajoutReference(String reference) {
        this.references.add(reference);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<String> references) {
        this.references = references;
    }

    public boolean isLivree() {
        return livree;
    }

    public void setLivree(boolean livree) {
        this.livree = livree;
    }

    public String getInfoCommande() {
        return infoCommande;
    }

    public void setInfoCommande(String infoCommande) {
        this.infoCommande = infoCommande;
    }
}