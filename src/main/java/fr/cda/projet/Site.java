package fr.cda.projet;

import java.io.*;
import java.util.*;

import fr.cda.modele.Commande;
import fr.cda.modele.Produit;
import fr.cda.util.*;

// Classe de definition du site de vente
//
public class Site {

    private ArrayList<Produit> stock;       // Les produits du stock
    private ArrayList<Commande> commandes;  // Les bons de commande

    private static final String FICHIER_STOCK = "src/data/produits.txt";
    private static final String FICHIER_COMMANDES = "src/data/commandes.txt";
    private static final String DOSSIER_ECRIRE = "src/data";

    // Constructeur
    //
    public Site() {
        stock = new ArrayList<Produit>();
        initialiserStock(FICHIER_STOCK);

        commandes = new ArrayList<Commande>();
        initialiserCommandes(FICHIER_COMMANDES);
    }
    
    // Methode qui retourne sous la forme d'une chaine de caractere
    //  tous les produits du stock
    //
    public String listerTousProduits() {
        StringBuilder res = new StringBuilder();
        res.append("Liste des produits : \n");

        for(Produit prod : stock) {
            res.append(prod.toString() + "\n");
            res.append( "=============================== \n");
        }

        return res.toString();
    }

    // Methode qui retourne sous la forme d'une chaine de caractere
    //  toutes les commandes
    //
    public String listerToutesCommandes() {
        StringBuilder res = new StringBuilder();
        res.append("Liste des commandes : \n");

        for (Commande cmd : commandes) {
            res.append(cmd.toString());
            res.append( "=============================== \n");
        }

        return res.toString();
    }

    public static String extraireReference(String ligneRef) {
        String [] colonne = ligneRef.split("=", 2);
        return colonne[0];
    }

    public static int extraireQuantite(String ligneRef) {
        String [] colonne = ligneRef.split("=", 2);
        return Integer.parseInt(colonne[1]);
    }

    // Livre toutes les commandes non livrees si le stock le permet
    // Retourne la liste des commandes non livrees
    public String livrerCommandes() {
        StringBuilder res = new StringBuilder();
        res.append( "Liste des commandes non livrées : \n");

        for (Commande cmd : commandes) {
            if (!cmd.isLivree()) {
                StringBuilder manque = new StringBuilder(); // permet de savoir si il y a des produits manquant

                for (String ref : cmd.getReferences()) {
                    String reference = extraireReference(ref);
                    int quantiteDemandee = extraireQuantite(ref);
                    Produit prod = rechercherProduit(reference);

                    if (prod.getQuantite() < quantiteDemandee) {
                        int quantiteManquante = quantiteDemandee - prod.getQuantite();
                        manque.append("il manque ").append(quantiteManquante).append(" ").append(reference).append("\n");
                    }
                }
                if (manque.length() == 0) {
                    for (String ref : cmd.getReferences()) {
                        String reference = extraireReference(ref);
                        int quantiteDemandee = extraireQuantite(ref);
                        Produit prod = rechercherProduit(reference);
                        prod.setQuantite(prod.getQuantite() - quantiteDemandee);
                    }
                    cmd.setLivree(true);
                } else {
                    cmd.setInfoCommande(manque.toString());
                    res.append(cmd);
                    res.append(cmd.getInfoCommande()).append("\n");
                    res.append( "=============================== \n");
                }
            }
        }
        return res.toString();
    }

    // Methode qui retourne sous la forme d'une chaine de caractere
    //  une commande
    //
    public String listerCommande(int numero) {
        Commande cmd = rechercherCommande(numero);
        if (cmd == null) {
            return "la commande " + numero + " n'existe pas";
        }
        return cmd + "\n";
    }

    // Methode qui permet de rechercher une commande par son numéro
    public Commande rechercherCommande(int numero) {
        for (Commande cmd : commandes) {
            if (numero == cmd.getNumero()) {
                return cmd;
            }
        }
        return null;
    }

    // Methode qui permet de vérifier si un produit
    // à la quatité recqui pour être commané
    private Produit rechercherProduit(String reference) {
        for (Produit prod : stock) {
            if (reference.equals(prod.getReference()) ) {
                return prod;
            }
        }
        return null;
    }



    // Chargement des données du stock
    //
    private void initialiserStock(String nomFichier) {

        String[] lignesProd = Terminal.lireFichierTexte(nomFichier);

        if (lignesProd != null) {
            for (String ligne : lignesProd) {
                String[] colonnes = ligne.split(";", 4);

                String reference = colonnes[0];
                String nom = colonnes[1];
                double prix = Double.parseDouble(colonnes[2]);
                int quantite = Integer.parseInt(colonnes[3]);

                Produit prod = new Produit(reference, nom, prix, quantite);
                stock.add(prod);

            }
        }
    }

    private void initialiserCommandes(String nomFichier) {

        String[] lignesCom = Terminal.lireFichierTexte(nomFichier);

        if (lignesCom != null) {
            for (String ligne : lignesCom) {
                String [] colonnes = ligne.split(";", 4);

                int numero = Integer.parseInt(colonnes[0]);
                String date = colonnes[1];
                String client = colonnes[2];
                String reference = colonnes[3];

                Commande cmd = rechercherCommande(numero);

                if (cmd == null) {
                    cmd = new Commande(numero, date, client, new ArrayList<>());
                    commandes.add(cmd);
                }
                cmd.ajoutReference(reference);
            }
        }
    }

    public String calculerVentes() {
        StringBuilder res = new StringBuilder();
        double somme = 0;


        for (Commande cmd : commandes) {
            if (cmd.isLivree()) {
                res.append("COMMANDE : " + cmd.getNumero() + "\n");
                for (String ref : cmd.getReferences()) {
                    String reference = extraireReference(ref);
                    int quantiteDemandee = extraireQuantite(ref);
                    Produit prod = rechercherProduit(reference);
                    double prix = prod.getPrix();
                    somme += quantiteDemandee * prix;
                    res.append(String.format("%-40s %5d %5.2f",prod.getNom(),quantiteDemandee,prix) + "\n");
                }

            }
        }
        res.append("=============================== \n");
        res.append( "SOMME : " + String.format("%5.2f", somme) + " euros \n");
        return res.toString();
    }


    public File ecrireCommandesLivrees() throws IOException {

        File rep = new File(DOSSIER_ECRIRE);
        rep.mkdir();

        String nomFichierSortie = rep + File.separator + "etat_commande.txt";

        File fichier = new File(nomFichierSortie);

        try (PrintWriter ecrire = new PrintWriter(new BufferedWriter(new FileWriter(fichier)))) {
            ecrire.println(calculerVentes());
        }

        return fichier;
    }
}