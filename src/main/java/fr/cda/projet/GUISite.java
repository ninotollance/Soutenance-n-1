package fr.cda.projet;

import fr.cda.ihm.*;
import fr.cda.modele.Commande;
import fr.cda.service.MailService;

import javax.swing.*;
import java.io.File;

// Classe de definition de l'IHM principale du compte
//
public class GUISite implements FormulaireInt {
    private Site site;  // Le site

    // Constructeur
    //
    public GUISite(Site site) {
        this.site = site;

        // Creation du formulaire
        Formulaire form = new Formulaire("Site de vente",this,1100,730);
        
        //  Creation des elements de l'IHM
        //
        form.addLabel("Afficher tous les produits du stock");
        form.addButton("AFF_STOCK","Tous le stock");
        form.addLabel("");
        form.addLabel("Afficher tous les bons de commande");
        form.addButton("AFF_COMMANDES","Toutes les commandes");
        form.addLabel("");
        form.addText("NUM_COMMANDE","Numero de commande",true,"1");
        form.addButton("AFF_COMMANDE","Afficher");
        form.addLabel("");
        form.addLabel("Modifier");
        form.addButton("MOD_COMMANDE","Modifier");
        form.addLabel("");
        form.addLabel("Livrer les commandes");
        form.addButton("AFF_LIVRER","Livrer");
        form.addLabel("");
        form.addLabel("Calculer les ventes");
        form.addButton("AFF_CALCULER","Calculer ventes");
        form.addLabel("");
        form.addLabel("Envoyer un email");
        form.addButton("ENV_MAIL","Envoyer email");
        form.addLabel("");

        form.setPosition(400,0);
        form.addZoneText("RESULTATS","Resultats",
                            true,
                            "",
                            600,700);

        // Affichage du formulaire
        form.afficher();
    }

    // Methode appellee quand on clique dans un bouton
    //
    public void submit(Formulaire form,String nomSubmit) {

        // Affichage de tous les produits du stock
        //
        if (nomSubmit.equals("AFF_STOCK")) {
            String res = site.listerTousProduits();
            form.setValeurChamp("RESULTATS",res);
        }

        // Affichage de toutes les commandes
        //
        if (nomSubmit.equals("AFF_COMMANDES")) {
            String res = site.listerToutesCommandes();
            form.setValeurChamp("RESULTATS",res);
        }

        // Affichage d'une commande
        // Vérification du nombre saisi avec try catch
        //
        if (nomSubmit.equals("AFF_COMMANDE")) {
            String numStr = form.getValeurChamp("NUM_COMMANDE");
            try  {
                int num = Integer.parseInt(numStr);
                String res = site.listerCommande(num);
                form.setValeurChamp("RESULTATS",res);
            } catch (NumberFormatException e) {
                form.setValeurChamp("RESULTATS", "Veuillez saisir un numéro valide !");
            }

        }
        // Livre les commandes et affiche celles qui sont non livrées
        if (nomSubmit.equals("AFF_LIVRER")) {
            String res = site.livrerCommandes();
            form.setValeurChamp("RESULTATS",res);
        }



        if (nomSubmit.equals("MOD_COMMANDE")) {
            String numStr = form.getValeurChamp("NUM_COMMANDE");
            try {
                int num = Integer.parseInt(numStr);
                Commande commande = site.rechercherCommande(num);
                if (commande == null) {
                    form.setValeurChamp("RESULTATS", "Cette commande n'existe pas !");
                } else if (commande.isLivree()) {
                    form.setValeurChamp("RESULTATS", "Cette commande a déjà été livrée !");
                } else {
                    new GUIModifierCommande(commande);
                }
            } catch (NumberFormatException e) {
                form.setValeurChamp("RESULTATS", "Veuillez saisir un numéro valide !");
            }
        }

        // calculer les ventes des commandes livrées
        if (nomSubmit.equals("AFF_CALCULER")) {
            String res = site.calculerVentes();
            form.setValeurChamp("RESULTATS",res);
        }

        // envoyer un email des commandes livrées
        if (nomSubmit.equals("ENV_MAIL")) {
            try {
                File fichier = site.ecrireCommandesLivrees(); // 1. créer le fichier
                MailService mail = new MailService();
                mail.envoyer(fichier); // 2. l'envoyer

                JOptionPane.showMessageDialog(null, "Email envoyé avec succès !",
                        "Email", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Échec de l'envoi : " + e.getMessage(),
                        "Email", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}