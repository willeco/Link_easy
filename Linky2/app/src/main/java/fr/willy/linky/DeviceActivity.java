package fr.willy.linky;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class DeviceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    /*
    L'idee c'est de pouvoir ajouter un appareil à la liste, pour pouvoir voir sa consommation energetique.
    Pour rendre l'application plus belle et ergonomique, pourquoi pas faire une banque d'images des potentiels
    appareils électriques d'une maison.
    Il faut donc trouver un moyen de proposer dans une liste déroulante diffrents appareils.
     */


    private ArrayList<String> device_list ; //liste des appareils disponibles (créée dans res/values/string)
    private Button button_add_device; //bouton permettant d'ajouter un appareil (génération du popup)
    private DeviceActivity device_activity; //on stock notre activité dans un attribut pour pouvoir avoir accès à son contenu partout
    private DeviceDataBase db = new DeviceDataBase(this); //création de la base de données (contenant des futurs appareils de la maison)
    private LinearLayout dynamic_device_layout; //scrollview avec la liste des appareils
    private ViewGroup.LayoutParams device_params;
    private ImageView air_conditioner,alarm,camera,car_load,cmv,cooking_hood
            ,cooking_tools,dishwasher,food_processor,freezer,fridge,garage_doors
            ,garden_mower,garden_tools,hair_dryer,heat_pump,hot_water_tank,lamp
            ,laptop,microwave,movement_detector,other,oven,phone_load,portal
            ,printer,radiator,sewing_machine,shutters,straightener,toaster
            ,tumble_dryer,tv,vacuum,washing_machine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device); //on charge le layout de l'activité =! du layout du popup

        db.open(); //on ouvre la base de données

        //on supprime son contenu /!\ présent uniquement pendant le developpement de l'appli
        //cette ligne sera ensuite supprimé car on veut garder en mémoire la liste des appareils et leurs infos.
        db.removeAll();
        Devices a = new Devices(db.getSize()+1,"Four", "666"); //test de création statique d'un appareil
        db.insert(a); //ajout de cet appareil
        Devices b = new Devices(db.getSize()+1,"Cafe", "666");
        db.insert(b);
        db.displayDevices();
        db.close();

        device_activity       = this; //on stock la classe dans un attribut pour y avoir acces plus tard
        button_add_device     = findViewById(R.id.button_add_device);
        dynamic_device_layout = (LinearLayout) findViewById(R.id.dynamic_device_layout);

        device_params = new ActionBar.LayoutParams(140,160, 200);

        //on créer une interaction avec le bouton "ajouter un appareil"
        //génération d'un popup, pour selectionner l'appareil en question dans une liste.
        button_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDevice(); //protocole d'ajout d'appareils, lancé
            }
        });
    }


    //protocole d'ajout d'appareils, lancé
    public void addDevice(){
        final CustomPopUp customPopUp = new CustomPopUp(device_activity); //on créer le popup

        customPopUp.test_bluid(); //on affiche le popup

        //on créer une interaction avec le champ "confirmer" du popup
        //lorsque l'on appuie sur le champ "confirmer", on confirme l'ajout de l'appareil selcetionné dans notre base de données.
        customPopUp.getConfirm_text().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customPopUp.dismiss();//on ferme le popup

                String selected_device = customPopUp.getSpinnerData(); //on récupere l'appreil selectionné dans la liste du popup
                if (selected_device.equals("Choisir appareil")){
                    makeText(getApplicationContext(),"Choix invalide, veuillez choisir une autre catégorie.", Toast.LENGTH_SHORT).show();
                }
                else{
                    makeText(getApplicationContext(),selected_device + " ajouté", Toast.LENGTH_SHORT).show();
                    generateDevice(selected_device);
                }
            }
        });

        //idem pour le champ "annuler" sauf que cette fois-ci on annule la procédure et on prévient l'utilisateur.
        customPopUp.getCancel_text().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeText(getApplicationContext(),"Ajout annulé", Toast.LENGTH_SHORT).show();
                customPopUp.dismiss();
            }
        });
    }

    /*
    on genere un appareil (uniquement l'image de l'appareil pour l'instant)
    creation + affichage
     */
    public void generateDevice(String device){

        ImageView test = new ImageView(device_activity);
        test.setLayoutParams(device_params);

        if (device.equals("Lampe")) {
            test.setBackgroundResource(R.drawable.lamp);
        } else if (device.equals("Four")) {
            test.setBackgroundResource(R.drawable.oven);
        } else if (device.equals("Télévision")) {
            test.setBackgroundResource(R.drawable.tv);
        } else if (device.equals("Aspirateur")) {
            test.setBackgroundResource(R.drawable.vacuum);
        } else if (device.equals("Frigo")) {
            test.setBackgroundResource(R.drawable.fridge);
        } else if (device.equals("Machine à laver")) {
            test.setBackgroundResource(R.drawable.washing_machine);
        } else if (device.equals("Portes de garage")) {
            test.setBackgroundResource(R.drawable.garage_doors);
        } else if (device.equals("Lave vaiselle")) {
            test.setBackgroundResource(R.drawable.dishwasher);
        } else if (device.equals("Seche linge")) {
            test.setBackgroundResource(R.drawable.tumble_dryer);
        } else if (device.equals("Micro-ondes")) {
            test.setBackgroundResource(R.drawable.microwave);
        } else if (device.equals("Grille pain")) {
            test.setBackgroundResource(R.drawable.toaster);
        } else if (device.equals("Hotte")) {
            test.setBackgroundResource(R.drawable.cooking_hood);
        } else if (device.equals("Congele")) {
            test.setBackgroundResource(R.drawable.freezer);
        } else if (device.equals("Radiateur")) {
            test.setBackgroundResource(R.drawable.radiator);
        } else if (device.equals("Imprimante")) {
            test.setBackgroundResource(R.drawable.printer);
        } else if (device.equals("Ordinateur")) {
            test.setBackgroundResource(R.drawable.laptop);
        } else if (device.equals("Volets")) {
            test.setBackgroundResource(R.drawable.shutters);
        } else if (device.equals("Portail éléctrique")) {
            test.setBackgroundResource(R.drawable.portal);
        } else if (device.equals("Clim")) {
            test.setBackgroundResource(R.drawable.air_conditioner);
        } else if (device.equals("Machine à coudre")) {
            test.setBackgroundResource(R.drawable.sewing_machine);
        } else if (device.equals("Seche cheveux")) {
            test.setBackgroundResource(R.drawable.hair_dryer);
        } else if (device.equals("Ustensile de cuisine")) {
            test.setBackgroundResource(R.drawable.cooking_tools);
        } else if (device.equals("Robot cuisine")) {
            test.setBackgroundResource(R.drawable.food_processor);
        } else if (device.equals("Lisseur cheveux")) {
            test.setBackgroundResource(R.drawable.straightener);
        } else if (device.equals("Vmc")) {
            test.setBackgroundResource(R.drawable.cmv);
        } else if (device.equals("Charge téléphone")) {
            test.setBackgroundResource(R.drawable.phone_load);
        } else if (device.equals("Tondeuse électrique")) {
            test.setBackgroundResource(R.drawable.garden_mower);
        } else if (device.equals("Pompe à chaleur")) {
            test.setBackgroundResource(R.drawable.heat_pump);
        } else if (device.equals("Detecteur de mouvements")) {
            test.setBackgroundResource(R.drawable.movement_detector);
        } else if (device.equals("Alarme")) {
            test.setBackgroundResource(R.drawable.alarm);
        } else if (device.equals("Ballon eau chaude")) {
            test.setBackgroundResource(R.drawable.hot_water_tank);
        } else if (device.equals("Outils exterieurs")) {
            test.setBackgroundResource(R.drawable.garden_tools);
        } else if (device.equals("Charge voiture electrique")) {
            test.setBackgroundResource(R.drawable.car_load);
        }
        else{
            test.setBackgroundResource(R.drawable.other);
        }
        dynamic_device_layout.addView(test);
    }

    //on affiche dans un Toast l'appreil selectionné.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        makeText(this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
