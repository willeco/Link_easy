package fr.willy.linky;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

// -------------------------------------------------------------------------------------------------
// Class contenant les méthodes de communication
//
// UTILISATION :
//
//   * Pour créer un client UDP :
//
//   ClientUDP clientUDP = new ClientUDP();
//
//   * Pour envoyer un message au serveur :
//
//   if (clientUDP != null)
//        {
//          clientUDP.envoyer(message, IP, port);
//        }
//
// -------------------------------------------------------------------------------------------------
public class ClientUDP {

    private InetAddress                                 m_serverAddr            = null;
    private DatagramSocket                              m_socket                = null;
    private TClientUDP                                  m_tClientUDP;
    private Thread                                      m_threadClientUDP;
    private LinkedBlockingQueue<DatagramPacket>         m_qEmission;
    private Handler                                     m_handler;

    // Codes utilisés par le handler de message
    public final static int CODE_CONNEXION                                      = 0;
    public final static int CODE_RECEPTION                                      = 1;
    public final static int CODE_EMISSION                                       = 2;

    // ---------------------------------------------------------------------------------------------
    // Constructeur du client UDP
    //
    //  - En entrée :
    //   Handler handlerUI : handler de l'IHM pour pouvoir mettre à jour l'IHM
    // ---------------------------------------------------------------------------------------------
    public ClientUDP(Handler handlerUI)
    {

        // Récupère l'handler de l'IHM
        m_handler = handlerUI;

        // Création d'un socket pour l'envoi et la réception des paquets UDP
        try
        {
            m_socket = new DatagramSocket();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Création d'une liste de paquets UDP à envoyer.
        // -----------------------------------------------------------------------------------------
        // En effet, les messages à envoyer au serveur
        // sont stockés dans une queue. Sur-dimensionné pour la première version de l'application
        // car un seul message est à envoyer pour réveiller le serveur. On pourrait imaginer
        // par la suite plusieurs boutons demandant d'autres informations au compteur
        // Linky. Il faut aussi pouvoir gérer des appuis multiples sur le bouton
        m_qEmission = new LinkedBlockingQueue<DatagramPacket>();
    }

    // ---------------------------------------------------------------------------------------------
    // Méthode pour streamMessage un message vers l'IHM
    // ---------------------------------------------------------------------------------------------
    public void streamMessage(int code, String message)
    {
        Message msg = Message.obtain();

        // On renseigne le code
        msg.what    = code;

        // On affecte le message
        if (message != null)
            msg.obj = message;

        // On  utilise le handler de message pour transmettre le message à l'interface
        if (m_handler != null)
        {
            m_handler.sendMessage(msg); //Envoi le msg au MainActiv
        } else   {
            Log.e("UDP", "1 - m_handler.sendMessage : null object reference");
        }

    }

    // ---------------------------------------------------------------------------------------------
    // Création du Thread pour envoyer et recevoir les paquets UDP.
    //
    // Cette méthode privée est appelée par la méthode envoyer
    // ---------------------------------------------------------------------------------------------
    private void startThread()
    {
        // On regarde si le Thread existe ?
        // Il existe si l'utilisateur avait auparavent appuyé sur le bouton demande
        // de télé-information
        if (m_threadClientUDP == null)
        {
            // La fonction run de tClientUDP sera à exéctuer par le Thread "threadClientUDP"
            m_tClientUDP      = new TClientUDP(this, m_socket);

            // Création d'un Thread pour l'envoi et la réception des paquets UDP
            // -------------------------------------------------------------------------------------
            // Attention, ce Thread ne doit pas modifier l'affichage
            // Seul, L’UI thread est responsable de l’affichage et des interactions
            // avec l’utilisateur
            // Ce thread ne pourra pas accéder directement à l’IHM car seul l’UI thread peut
            // modifier l’affichage.
            // Pour cela Android propose plusieurs solutions : la classe Handler.
            m_threadClientUDP = new Thread(m_tClientUDP);

            Log.i("UDP", "1 - Demande de démarrage du Thread");
            m_threadClientUDP.start();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Méthode pour arrêter le thread
    // ---------------------------------------------------------------------------------------------
  /*
    private void arreter_thread()
    {
        if (m_threadClientUDP != null)
        {
            m_tClientUDP.stop();
            Thread t = m_threadClientUDP;
            m_threadClientUDP = null;
            t.interrupt();
        }
    }
    */
    // ---------------------------------------------------------------------------------------------
    // Méthode pour préparer l'envoi des paquets
    // ---------------------------------------------------------------------------------------------
    public void envoyer(String leMessage, String adresseServeur, int portServeur)
    {
        byte[] message= new byte[leMessage.length()];

        message = leMessage.getBytes();


        // Récupération de l'adresse IP du compteur Linky
        // -----------------------------------------------------------------------------------------
        // Attention, ne fonctionne pas si adresseServeur = erlinky.home ????
        // A corriger
        try
        {
            m_serverAddr = InetAddress.getByName(adresseServeur);
            Log.i("UDP", "2 - Récupération addresse Linky");
        }
        catch (UnknownHostException e)
        {
            Log.e("UDP", "Impossible de transformer le hostname en adresse IP");
            e.printStackTrace();
        }

        // Ajout d'un message à la liste d'envoi
        // -----------------------------------------------------------------------------------------
        try
        {
            // Création d'un datagram avec l'adresse IP et le port
            DatagramPacket packet = new DatagramPacket(message, leMessage.length(), m_serverAddr, portServeur);

            // On ajoute un paquet dans la liste d'envoi
            m_qEmission.add(packet);
            Log.i("UDP", "3 - Ajout paquet UDP à la liste d'envoi");
        }
        catch(Exception e)
        {
            Log.e("UDP", "Impossible d'ajouter le paquet UDP");
            e.printStackTrace();
        }

        // Démarrage du Thread si celui-ci n'a jamais démarré
        // -----------------------------------------------------------------------------------------
        if (m_threadClientUDP == null)
        {
            startThread();
            Log.i("UDP", "4 - Création et démarrage du Thread UDP");
        }


    }


    // ---------------------------------------------------------------------------------------------
    // Méthode pour récupérer un paquet de la queue des paquets à envoyer
    // ---------------------------------------------------------------------------------------------
    public DatagramPacket getPackage()
    {
        // Retourne le datagram le plus ancien dans la queue
        // Il faut faire autant de getPackage qu'il y a de datagram dans la queue
        // afin de vider la queue
        return m_qEmission.poll();
    }
    // ---------------------------------------------------------------------------------------------
    // Arrête le thread et ferme le socket
    // ---------------------------------------------------------------------------------------------
    public void fermer()
    {
       // arreter_thread();
        if (m_threadClientUDP != null)
        {
            m_tClientUDP.stop();
            Thread t = m_threadClientUDP;
            m_threadClientUDP = null;
            t.interrupt();
        }
        m_socket.close();
    }
}
