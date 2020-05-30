package fr.willy.linky;

import java.io.IOException;
import java.net.*;

import android.graphics.PorterDuff;
import android.util.Log;
import android.content.Context;


import static fr.willy.linky.HubActivity.etatText;

// -------------------------------------------------------------------------------------------------
// Création d'une classe ayant pour objectif d'exécuter le code du Thread
// -------------------------------------------------------------------------------------------------
public class TClientUDP implements Runnable {

    private ClientUDP           clientUDP           = null;
    private DatagramSocket      socket              = null;
    private volatile boolean    fini                = false;


    private static final int MAX_UDP_DATAGRAM_LEN   = 1500;

    // ---------------------------------------------------------------------------------------------
    // Constructeur
    // ---------------------------------------------------------------------------------------------
    public TClientUDP(ClientUDP clientUDP, DatagramSocket socket)
    {
        // Class contenant les paquets UDP à transmettre
        this.clientUDP          = clientUDP;

        // Socket utilisé pour envoyer les paquets UDP
        this.socket             = socket;
        fini                    = false;

        HubActivity.etatText.setText("Connexion...");
    }

    // ---------------------------------------------------------------------------------------------
    // Méthode appelée lors de l'éxécution du Thread
    // ---------------------------------------------------------------------------------------------
    public void run()
    {
        // Pour la réception
        byte[]          ReceivedMessage = new byte[MAX_UDP_DATAGRAM_LEN];
        DatagramPacket  dp              = new DatagramPacket(ReceivedMessage, ReceivedMessage.length);
        String lText;

        Log.i("UDP", "RUN Thread exécuté");

        while (!fini)
        {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HubActivity.firstLine.setColorFilter(R.color.colorLinkyBar, PorterDuff.Mode.LIGHTEN);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            HubActivity.firstPoint.setColorFilter(R.color.white, PorterDuff.Mode.LIGHTEN);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            // -------------------------------------------------------------------------------------
            // Envoi des paquets stockés dans la liste d'envoi
            // -------------------------------------------------------------------------------------

            // Récupération d'un paquet UDP
            DatagramPacket packet = clientUDP.getPackage(); //si pas de paquet passage en reception

            // Boucle sur tous les paquets stockés dans la liste d'envoi
            while (packet != null)
            {
                try
                {
                    // Transmet en UDP le paquet récupéré en utilisant la méthode send() de l'objet socket
                    this.socket.send(packet);

                    // Message d'information accessible dans le Logcat (Smartphone connecté en USB)
                    Log.i("UDP", "5 - Paquet envoyé !!");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                // Y a t-il un autre paquet UDP stocké dans la liste d'envoi ?
                packet = clientUDP.getPackage();
            }

            // -------------------------------------------------------------------------------------
            // Réception des paquets émis par le LINKY  :
            // Attention programme MAL CONCUE : la réception est bloquante
            // Si le linky n'envoie pas des paquets, il sera impossible d'en émettre de nouveau
            //
            // -------------------------------------------------------------------------------------

            // Le fait de vouloir recevoir des paquets en passant le test à true
            // oblige a appuyer 2 fois sur le bouton !!!! Bug à corriger
            if (true) {
                // Une fois tous les paquets transmis, on peut recevoir les paquets en provenance du Linky
                try
                {
                    // Réception paquet en provenance du Linky (Fonction bloquante : Attention)
                    //Log.i("UDP:", "En attente de réception d'un paquet");

                    // set the timeout in millisecounds.
                    // https://stackoverflow.com/questions/10055913/set-timeout-for-socket-receive
                    this.socket.setSoTimeout(1000);

                    //Test


                    // recieve data until timeout
                    this.socket.receive(dp);

                    // On récupère le message
                    // dp.getPort()
                    lText               = new String(dp.getData());


                    // Diffusion du message vers l'interface
                    this.clientUDP.streamMessage(this.clientUDP.CODE_RECEPTION, lText);


                    // Pour observer le paquet reçu avec les LOG,
                    //
                    //      1°) il faut que le smartphone soit connecté
                    //          à l'ordi en USB. (Débuggage USB activé sur le smartphone)
                    //
                    //      2°) Les log sont accessibles dans la fenêtre logcat (Choisir le filtre Info)
                    //Log.i("UDP packet reçu:", lText);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }



        }
    }


    // ---------------------------------------------------------------------------------------------
    // Méthode à appeler quand on veut arrêter l'envoi des paquets UDP
    // ---------------------------------------------------------------------------------------------
    public void stop()
    {
        if (fini == false)
        {
            fini = true;
        }
    }
}
