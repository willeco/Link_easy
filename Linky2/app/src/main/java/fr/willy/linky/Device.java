package fr.willy.linky;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Device extends AppCompatActivity {

    /*
    L'idee c'est de pouvoir ajouter un appareil à la liste, pour pouvoir voir sa consommation energetique.
    Pour rendre l'application plus belle et ergonomique, pourquoi pas faire une banque d'images des potentiels
    appareils électriques d'une maison.
    Il faut donc trouver un moyen de proposer dans une liste déroulante diffrents appareils.
     */

    /* Listes des appareils électriques potentiels :
    four
    lampes
    machine à laver
    portes de garage
    lave vaiselle
    seche linge
    micro onde
    grille pain
    tele
    hotte
    frigo
    congele
    radiateur
    imprimante
    ordinateur
    volets
    portail éléctrique
    aspirateur
    clim
    machine à coudre
    seche cheveux
    ustensile de cuisine
    robot cuisine
    lisseur cheveux
    vmc
    charge téléphone
    tondeuse électrique
    pompe à chaleur
    detecteur de mouvements
    alarme*
    ballon d'eau chaude
    outils exterieurs
    charge voiture electrique
     */

    private ArrayList<String> device_list ;
    private Button button_add_device;
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        device = this;
        button_add_device = findViewById(R.id.button_add_device);

        button_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                //creation d'un pop up pour ajouter un appareil
                AlertDialog.Builder popup_device_adding = new AlertDialog.Builder(device);
                popup_device_adding.setTitle("Ajout d'un appareil");
                popup_device_adding.setMessage("Veuillez choisir votre appareil");

                popup_device_adding.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Votre appareil a bien été ajouté à votre liste", Toast.LENGTH_SHORT).show();
                    }
                });

                popup_device_adding.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Ajout de l'appreil annulé", Toast.LENGTH_SHORT).show();
                    }
                });

                popup_device_adding.show();
                */

                CustomPopUp customPopUp = new CustomPopUp(device);
                //customPopUp.setTitle("Ajout d'un appareil");
                //customPopUp.setTitle("Veuillez choisir un appareil");
                customPopUp.test_bluid();
            }
        });
    }
}
