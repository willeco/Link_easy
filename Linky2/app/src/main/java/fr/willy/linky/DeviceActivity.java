package fr.willy.linky;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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

    //liste des appareils disponibles (créée dans res/values/string)
    private ArrayList<String> device_list ;
    //bouton permettant d'ajouter un appareil (génération du popup)
    private Button button_add_device;
    //on stock notre activité dans un attribut pour pouvoir avoir accès à son
    //contenu partout.
    private DeviceActivity device;
    //création de la base de données (contenant des futurs appareils de la maison)
    private DeviceDataBase db = new DeviceDataBase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device); //on charge le layout de l'activité =! du layout du popup

        //on ouvre la base de données
        db.open();
        //on supprime son contenu /!\ présent uniquement pendant le developpement de l'appli
        //cette ligne sera ensuite supprimé car on veut garder en mémoire la liste des appareils et leurs infos.
        db.removeAll();

        //test de création statique d'un appareil
        Devices a = new Devices(db.getSize()+1,"Four", "666");

        //ajout de cet appareil
        db.insert(a);

        Devices b = new Devices(db.getSize()+1,"Cafe", "666");

        db.insert(b);
        db.displayDevices();
        db.close();

        //on stock la classe dans un attribut pour y avoir acces plus tard
        device = this;
        button_add_device = findViewById(R.id.button_add_device);

        //on créer une interaction avec le bouton "ajouter un appareil"
        //génération d'un popup, pour selectionner l'appareil en question dans une liste.
        button_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //on créer le popup
                final CustomPopUp customPopUp = new CustomPopUp(device);

                //on créer une interaction avec le champ "confirmer" du popup
                //lorsque l'on appuie sur le champ "confirmer", on confirme l'ajout de l'appareil selcetionné dans notre base de données.
                customPopUp.getConfirm_text().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //on précise à l'utilisateur que son ajout à bien été pris en compte.
                        makeText(getApplicationContext(),"Appareil ajouté à la liste", Toast.LENGTH_SHORT).show();
                        //on ferme le popup
                        customPopUp.dismiss();
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

                //on récupere l'appreil selectionné dans la liste du popup
                customPopUp.getSpinnerData();
                //on affiche le popup
                customPopUp.test_bluid();
            }
        });

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
