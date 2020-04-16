package fr.willy.linky;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContainer;
import androidx.appcompat.widget.ActionBarContextView;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

    private ListView list_devices_in_list_view;

    private ArrayList<String> device_list ; //liste des appareils disponibles (créée dans res/values/string)
    private Button button_add_device; //bouton permettant d'ajouter un appareil (génération du popup)
    private DeviceActivity device_activity; //on stock notre activité dans un attribut pour pouvoir avoir accès à son contenu partout

    /**
     ** Préparation instance de la base de données (contenant les appareils de la maison)
     */
    private DeviceDataBase db = null;

    private GridLayout dynamic_device_layout; //scrollview avec la liste des appareils
    private ViewGroup.LayoutParams device_params, device_params1;

    private Button delete_device;

    private ImageView air_conditioner,alarm,camera,car_load,cmv,cooking_hood
            ,cooking_tools,dishwasher,food_processor,freezer,fridge,garage_doors
            ,garden_mower,garden_tools,hair_dryer,heat_pump,hot_water_tank,lamp
            ,laptop,microwave,movement_detector,other,oven,phone_load,portal
            ,printer,radiator,sewing_machine,shutters,straightener,toaster
            ,tumble_dryer,tv,vacuum,washing_machine;

    private String ip_for_sending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device); //on charge le layout de l'activité =! du layout du popup

        Bundle extras = getIntent().getExtras();
        ip_for_sending = extras.getString("ip_for_sending");

        /*
        db.open(); //on ouvre la base de données

        //on supprime son contenu /!\ présent uniquement pendant le developpement de l'appli
        //cette ligne sera ensuite supprimé car on veut garder en mémoire la liste des appareils et leurs infos.
        db.removeAll();
        db.displayDevices();
        db.close();
        */
        device_activity       = this; //on stock la classe dans un attribut pour y avoir acces plus tard
        button_add_device     = findViewById(R.id.button_add_device);
        delete_device         = findViewById(R.id.delete_device);

        // OBSOLOTE Ne sert plus
        // dynamic_device_layout = (GridLayout) findViewById(R.id.dynamic_device_layout);

        device_params = new ActionBar.LayoutParams(150,150);

        //on créer une interaction avec le bouton "ajouter un appareil"
        //génération d'un popup, pour selectionner l'appareil en question dans une liste.
        button_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDevice(); //protocole d'ajout d'appareils, lancé
            }
        });

        delete_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_listview_of_Devices(true);
            }
        });

        /**
         * NEW : Création Instance Base de données contenant les appareils électriques
         */
        Log.i("BD:", "Création instance base de données  ");
        db = new DeviceDataBase(this);

        /**
         * NEW : Affichage de la listView contenant tous les appareils
         */
        display_listview_of_Devices(false);



    }
    /**
     * NEW : Permet d'afficher la listeView contenant les appareils de la base de données
     * ----------------------------------------------------------------------------------
     */
    private void display_listview_of_Devices(Boolean delete)
    {
        /**
         * Ouverture Base de données contenant les appareils électriques
         */
        db.open();
        if (delete.equals(true)){
            db.remove(db.getSize());
        }
        db.displayDevices();

        /**
         * Retrieving the Cursor
         *
         * Perform A Query for all equipement from the database
         * In order to use a CursorAdapter, we need to query a SQLite database and
         * get back a Cursor representing the result set
         * => Cursors are like iterators or pointers, they contain NOTHING
         * but a mechanism for transversing the data
         * The Cursor must include a column named _id or this class will not work.
         * https://stackoverflow.com/questions/3359414/android-column-id-does-not-exist
         */
        Log.i("BD:", "Récupération Cursor sur tous les équipements  ");
        Cursor myCursor = db.return_cursor_bd();

        /**
         * Attaching the Adapter to a ListView
         */
        list_devices_in_list_view = (ListView)findViewById(R.id.list_device);

        Log.i("BD:", "Création d'un Cursor Adapteur pour remplir listView  ");
        DeviceCursorAdapter myAdapter = new DeviceCursorAdapter(this, myCursor);

        Log.i("BD:", "Remplissage listView  ");
        list_devices_in_list_view.setAdapter(myAdapter);

        db.close();
    }

    /**
     * Protocole d'ajout d'appareils dans la base de données
     * -----------------------------------------------------
     */
    public void addDevice(){
        final CustomPopUp customPopUpAdding = new CustomPopUp(device_activity, "adding"); //on créer le popup d'ajout

        customPopUpAdding.test_bluid(); //on affiche le popup

        //on créer une interaction avec le champ "confirmer" du popup
        //lorsque l'on appuie sur le champ "confirmer", on confirme l'ajout de l'appareil selcetionné dans notre base de données.
        customPopUpAdding.getConfirm_text().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selected_device = customPopUpAdding.getSpinnerData(); //on récupere l'appareil selectionné dans la liste du popup
                if (selected_device.equals("Choisir appareil")){
                    makeText(getApplicationContext(),"Choix invalide, veuillez choisir une autre catégorie.", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Ouverture de la Base de données
                    db.open();
                    Log.i("BD:", "Préparation ajout à la base de données contenant " + db.getSize() + " appareils ");


                    customPopUpAdding.dismiss();//on ferme le popup

                    final CustomPopUp customPopUpConfig = new CustomPopUp(device_activity, "config"); //on créer le popup de config
                    customPopUpConfig.test_bluid(); //on affiche le popup
                    customPopUpConfig.configuration_protocol(selected_device,customPopUpConfig,ip_for_sending);
                    makeText(getApplicationContext(),selected_device + " ajouté. ", Toast.LENGTH_SHORT).show();

                    // Insertion d'un appareil
                    Devices a = new Devices(db.getSize()+1,selected_device, "111");
                    db.insert(a);
                    db.close();


                    display_listview_of_Devices(false);
                }
            }
        });

        //idem pour le champ "annuler" sauf que cette fois-ci on annule la procédure et on prévient l'utilisateur.
        customPopUpAdding.getCancel_text().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeText(getApplicationContext(),"Ajout annulé", Toast.LENGTH_SHORT).show();
                customPopUpAdding.dismiss();
            }
        });
    }


    /**
     *  MAJ : Retourne un index sur la "ressource drawable" en fonction du nom de device
     *  --------------------------------------------------------------------------------
     */
    private int return_index_icon(String deviceName)
    {
        int index_icon;

        if (deviceName.equals("Lampe")                      ) {
            index_icon = R.drawable.lamp;
        } else if (deviceName.equals("Four")                ) {
            index_icon = R.drawable.oven;
        } else if (deviceName.equals("Télévision")          ) {
            index_icon = R.drawable.tv;
        } else if (deviceName.equals("Aspirateur")          ) {
            index_icon = R.drawable.vacuum;
        } else if (deviceName.equals("Frigo")               ) {
            index_icon = R.drawable.fridge;
        } else if (deviceName.equals("Machine à laver")     ) {
            index_icon = R.drawable.washing_machine;
        } else if (deviceName.equals("Portes de garage")    ) {
            index_icon = R.drawable.garage_doors;
        } else if (deviceName.equals("Lave vaiselle")       ) {
            index_icon = R.drawable.dishwasher;
        } else if (deviceName.equals("Seche linge")         ) {
            index_icon = R.drawable.tumble_dryer;
        } else if (deviceName.equals("Micro-ondes")         ) {
            index_icon = R.drawable.microwave;
        } else if (deviceName.equals("Grille pain")         ) {
            index_icon = R.drawable.toaster;
        } else if (deviceName.equals("Hotte")               ) {
            index_icon = R.drawable.cooking_hood;
        } else if (deviceName.equals("Congele")             ) {
            index_icon = R.drawable.freezer;
        } else if (deviceName.equals("Radiateur")           ) {
            index_icon = R.drawable.radiator;
        } else if (deviceName.equals("Imprimante")          ) {
            index_icon = R.drawable.printer;
        } else if (deviceName.equals("Ordinateur")          ) {
            index_icon = R.drawable.laptop;
        } else if (deviceName.equals("Volets")              ) {
            index_icon = R.drawable.shutters;
        } else if (deviceName.equals("Portail éléctrique")  ) {
            index_icon = R.drawable.portal;
        } else if (deviceName.equals("Clim")                ) {
            index_icon = R.drawable.air_conditioner;
        } else if (deviceName.equals("Machine à coudre")    ) {
            index_icon = R.drawable.sewing_machine;
        } else if (deviceName.equals("Seche cheveux")       ) {
            index_icon = R.drawable.hair_dryer;
        } else if (deviceName.equals("Ustensile de cuisine")) {
            index_icon = R.drawable.cooking_tools;
        } else if (deviceName.equals("Robot cuisine")       ) {
            index_icon = R.drawable.food_processor;
        } else if (deviceName.equals("Lisseur cheveux")     ) {
            index_icon = R.drawable.straightener;
        } else if (deviceName.equals("Vmc")                 ) {
            index_icon = R.drawable.cmv;
        } else if (deviceName.equals("Charge téléphone")    ) {
            index_icon = R.drawable.phone_load;
        } else if (deviceName.equals("Tondeuse électrique") ) {
            index_icon = R.drawable.garden_mower;
        } else if (deviceName.equals("Pompe à chaleur")     ) {
            index_icon = R.drawable.heat_pump;
        } else if (deviceName.equals("Detecteur de mouvements")) {
            index_icon = R.drawable.movement_detector;
        } else if (deviceName.equals("Alarme")              ) {
            index_icon = R.drawable.alarm;
        } else if (deviceName.equals("Ballon eau chaude")   ) {
            index_icon = R.drawable.hot_water_tank;
        } else if (deviceName.equals("Outils exterieurs")       ) {
            index_icon = R.drawable.garden_tools;
        } else if (deviceName.equals("Charge voiture electrique")) {
            index_icon = R.drawable.car_load;
        } else {
            index_icon = R.drawable.other;
        }
        return(index_icon);
    }




    /**
     * OBSOLETE - OBSOLETE
     * Remplacé par display_listview_of_Devices
     *
     * Affiche en plus un appareil (uniquement l'image de l'appareil pour l'instant)
     * creation + affichage
     * ---------------------------------------------------------------------------------------------
     */
    public void generateDevice(String device) {

        int index_icon;

        ImageView test = new ImageView(device_activity);

        test.setLayoutParams(device_params);
        test.setAdjustViewBounds(true);

        //test.setPadding(200,200,200,200);
        index_icon = return_index_icon(device);
        test.setBackgroundResource(index_icon);

        dynamic_device_layout.addView(test);
    }

    /**
     * NEW : Permet à partir d'un cursor de remplir la ligne d'une liste View
     * ---------------------------------------------------------------------------------------------
     * We need to define the adapter to describe the process of projecting the Cursor's data into a View.
     * To do this we need to override the newView method and the bindView method.
     * The naive approach to this (without any view caching) looks like the following:
     */
    public class DeviceCursorAdapter extends CursorAdapter {

        // Constructeur
        public DeviceCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.device_row, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            int icon_index;

            // Find fields to populate in inflated template
            TextView tvDeviceName  = (TextView)  view.findViewById(R.id.device_name);
            TextView tvDevicePower = (TextView)  view.findViewById(R.id.device_power);
            ImageView imDevice     = (ImageView) view.findViewById(R.id.device_thumbnail);

            // Extract properties from cursor
            String device_name   = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String device_power  = cursor.getString(cursor.getColumnIndexOrThrow("power"));

            // Get index on the drawable icon
            icon_index          = return_index_icon(device_name);

            // Populate fields with extracted properties
            tvDeviceName.setText(device_name);
            tvDevicePower.setText("Conso en route : " + device_power + " Watts");
            imDevice.setImageResource(icon_index);
        }
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
