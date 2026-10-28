package fr.cda.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Service d'envoi d'emails via le relais SMTP de Brevo.
 *
 * Permet d'envoyer l'état des commandes livrées sous forme de pièce jointe.
 * Les identifiants SMTP sont lus dans le fichier config.properties
 * (placé dans src/main/resources et exclu de Git).
 */
public class MailService {


    private static final String HOST = "smtp-relay.brevo.com";
    private static final int PORT = 587;
    private static final String FICHIER_CONFIG = "config.properties";

    private static final String EXPEDITEUR = "ntollance@greta-bretagne-sud.fr";
    private static final String DESTINATAIRE = "ntollance@greta-bretagne-sud.fr";
    private static final String SUJET = "Soutenance n°1";
    private static final String CORPS =
            "Bonjour,\n\nVeuillez trouver ci-joint l'état des commandes livrées.\n\nCordialement.";

    /** Session SMTP authentifiée. */
    private final Session session;

    /**
     * Construit le service : charge les identifiants SMTP depuis
     * config.properties, puis configure la session SMTP
     * (serveur, port, authentification, STARTTLS, délais d'attente).
     *
     * @throws IOException si le fichier de configuration est absent ou illisible
     */
    public MailService() throws IOException {

        // Chargement du fichier de configuration
        Properties config = new Properties();
        try (InputStream in = MailService.class.getClassLoader()
                .getResourceAsStream(FICHIER_CONFIG)) {
            // Fichier absent : on lève une exception avec un message clair
            if (in == null) {
                throw new IOException("Fichier " + FICHIER_CONFIG + " introuvable");
            }
            config.load(in);
        }

        // Récupération des identifiants
        String login = config.getProperty("smtp.login");
        String key = config.getProperty("smtp.key");

        // Configuration de la connexion au serveur SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", String.valueOf(PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "10000"); // 10 s max pour se connecter
        props.put("mail.smtp.timeout", "10000");           // 10 s max par réponse

        // Session authentifiée avec les identifiants chargés
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login, key);
            }
        });
    }

    /**
     * Envoie par email l'état des commandes livrées en pièce jointe.
     * Le destinataire, le sujet et le corps du message sont définis en constantes.
     *
     * @param fichier le fichier texte contenant l'état des commandes livrées
     * @throws Exception si la création du message ou l'envoi SMTP échoue
     */
    public void envoyer(File fichier) throws Exception {
        // Création du message rattaché à la session SMTP Brevo
        MimeMessage msg = new MimeMessage(session);

        // En-têtes expéditeur, destinataire et sujet
        msg.setFrom(new InternetAddress(EXPEDITEUR, "Site de vente", "UTF-8"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(DESTINATAIRE));
        msg.setSubject(SUJET, "UTF-8");

        // le texte du mail
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(CORPS, "UTF-8");

        // la pièce jointe (état des commandes livrées)
        MimeBodyPart fichierPart = new MimeBodyPart();
        fichierPart.attachFile(fichier);

        // "mixed" = texte + fichier joint dans le même message
        Multipart contenu = new MimeMultipart("mixed");
        contenu.addBodyPart(textPart);
        contenu.addBodyPart(fichierPart);
        msg.setContent(contenu);

        // Envoi via le serveur SMTP
        Transport.send(msg);
    }
}
