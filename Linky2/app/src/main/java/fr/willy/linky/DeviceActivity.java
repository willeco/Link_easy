package fr.willy.linky;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.widget.Toast.makeText;

public class DeviceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    /**
     Le but est de pouvoir ajouter un appareil à la liste, pour voir sa consommation energetique.
     On choisi l'appareil parmis une liste déroulante. Si l'appareil souhaité n'est pas présent,
     on peut choisir "autre catégorie".
     Suite à ce choix, l'utilisateur se voit suivre un protocole grace à un popup.
     Ce protocole sert à calculer la consommation de l'appareil. Le protocole peut varier en fonction
     de l'appareil mais les informations désirées sont identiques. D'où l'interet d'utiliser l'orientée objet.

     L'appareil possede plusieurs attributs :
     - un id
     - un icon
     - un nom
     - une puissance en route
     - une puissance en veille
     - un taux d'utilisation
     - une puissance moyenne
     (à venir)
     - un protocol

     Une fois que l'appareil est créé, l'utilisateur a la possibilité de configurer différement celui-ci
     (exemple : taux d'utilisation) et de le supprimer. (Géré dans QuickConfigActivity)
*/

    /*
     (à venir)
     L'utilisateur peut également choisir le type de classement pour les appareils créés dans la base de données :
     - conso en route instantannée
     - conso en veille instantannée
     - conso moyenne (en fonction du taux d'utilisation)
     - cout annuel
     */

    private ListView        list_devices_in_list_view;
    private Button          button_add_device;      //bouton permettant d'ajouter un appareil (génération du popup)
    private DeviceActivity  device_activity = this; //on stock notre activité dans un attribut pour pouvoir avoir accès à son contenu partout
    private DeviceDataBase  db              = null; //Préparation instance de la base de données (contenant les appareils de la maison)

    private String          ip_for_sending,
                            selected_device;

    private int             power,
                            standbypower;

    private boolean         dont_show_again = false;
    private ImageView       air_conditioner,alarm,camera,car_load,cmv,cooking_hood
                            ,cooking_tools,dishwasher,food_processor,freezer,fridge,garage_doors
                            ,garden_mower,garden_tools,hair_dryer,heat_pump,hot_water_tank,lamp
                            ,laptop,microwave,movement_detector,other,oven,phone_load,portal
                            ,printer,radiator,sewing_machine,shutters,straightener,toaster
                            ,tumble_dryer,tv,vacuum,washing_machine;








   /**
     * ####################################################################################################
     * Création de l'activité
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device); //on charge le layout de l'activité

        /**
         * début du tutoriel
         */
        final CustomPopUp customPopUpTutoAdd = new CustomPopUp(device_activity, "tuto add"); //création d'un popup tutoriel
        customPopUpTutoAdd.test_bluid(); //on affiche le popup

        customPopUpTutoAdd.getNext().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //interaction avec le popup
                customPopUpTutoAdd.dismiss();
                final CustomPopUp customPopUpTutoConfig = new CustomPopUp(device_activity, "tuto config"); //on passe au popup suivant
                customPopUpTutoConfig.test_bluid(); //on affiche le popup

                customPopUpTutoConfig.getTuto_understand().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //interaction avec le popup
                        customPopUpTutoConfig.getDont_show_again().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //pas encore utilisable
                                //dont_show_again = true;
                            }
                        });
                        customPopUpTutoConfig.dismiss();
                    }
                });
            }
        });
        /**
         * fin du tutoriel
         */

        //récuperation de l'adresse IP
        Bundle extras = getIntent().getExtras();
        ip_for_sending = extras.getString("ip_for_sending");

        /**
         * Ajout d'appareils
         */
        button_add_device     = findViewById(R.id.button_add_device);
        button_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDevice(); //protocole d'ajout d'appareils, lancé
            }
        });

        /**
         * Création de la base de données
         */
        Log.i("BD:", "Création instance base de données  ");
        db = new DeviceDataBase(this);

        /**
         * Affichage des appareils
         */
        display_listview_of_Devices();
    }
    /**
     * ####################################################################################################
     */








    /**
     * ####################################################################################################
     * Permet de relancer l'activité en cas de besoin
     */
    public void onStart() {
        super.onStart();
        display_listview_of_Devices(); //raffraichissement de l'affichage
    }
    /**
     * ####################################################################################################
     */








    // Création des Getter
    public int getPower(){              return power;}
    public int getstandbypower(){       return standbypower;}
    public String getSelected_device(){ return selected_device;}
    public DeviceDataBase getDb(){      return db;}


    // Création des Setter
    public void setPower(       int power_input){       power = power_input;}
    public void setstandbypower(int standbypower_input){standbypower = standbypower_input;} //TA FOUTU QUOI LA ?








    // ---------------------------------------------------------------------------------------------
    // Fonction permettant l'affichage des appareils
    // ---------------------------------------------------------------------------------------------
    public void display_listview_of_Devices()
    {
        db.open(); //Ouverture Base de données contenant les appareils électriques

        /**
         * suppression d'un appareil si demande il y a eu
         */
        int delete_index = db.deleteDevices();  //Récupère l'id de l'appareil
        if (delete_index != 0){                 //si il y a un appareil à supprimer
            db.remove(delete_index);            //on le supprime
            delete_index=0;
        }

        /**
         * Affichage des appareils
         */
        list_devices_in_list_view = (ListView)findViewById(R.id.list_device);
        Cursor myCursor = db.return_cursor_bd();
        DeviceCursorAdapter myAdapter = new DeviceCursorAdapter(this, myCursor);
        list_devices_in_list_view.setAdapter(myAdapter);
        list_devices_in_list_view.setClickable(true);


        db.close(); //Fermeture de la base de données

        /**
         * Interaction avec les appareils pour les modifier
         */
        list_devices_in_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //vibration de 40 ms
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(40);
                }

                Cursor cursor = (Cursor) parent.getItemAtPosition(position); //on récupère l'appareil

                if (cursor != null) {
                    int rowId = cursor.getInt(cursor.getColumnIndexOrThrow("_id")); //on récupère l'id de l'appareil
                    Intent intent = new Intent(view.getContext(), QuickConfigActivity.class);   //on créer l'activité de configuration
                    intent.putExtra("rowid", rowId);                                     //on passe l'id à cette activité
                    startActivity(intent);
                }
                return false;
            }
        });
    }








    // ---------------------------------------------------------------------------------------------
    // Fonction permettant l'ajout d'un appareil
    // ---------------------------------------------------------------------------------------------
    public void addDevice(){

        /**
         * Choix de l'appareil à ajouter grâce à un pop up
         */
        final CustomPopUp customPopUpAdding = new CustomPopUp(device_activity, "adding"); //on créer le popup d'ajout
        ArrayAdapter adapter = ArrayAdapter.createFromResource( device_activity,                //affichage d'un menu déroulant
                                                                R.array.device_string,          //montrant la liste des appareils disponibles
                                                                R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        customPopUpAdding.getDevice_spinner().setAdapter(adapter); //on applique le menu déroulant au pop up
        customPopUpAdding.test_bluid();                            //on affiche le popup

        /**
         * Confirmation d'ajout
         */
        customPopUpAdding.getConfirm_text().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //interaction, confirmation de l'ajout

                selected_device = customPopUpAdding.getSpinnerData(); //on récupere l'appareil selectionné dans le menu déroulant du pop up
                if (selected_device.equals("Choisir appareil")){
                    makeText(getApplicationContext(),"Choix invalide, veuillez choisir une autre catégorie.", Toast.LENGTH_SHORT).show();
                }
                else{
                    //ouverture de la base de données
                    db.open();
                    customPopUpAdding.dismiss();

                    /**
                     * Lancement du protocole de calcul de puissances
                     */
                    final CustomPopUp customPopUpProtocol = new CustomPopUp(device_activity, "config");         //on créer le pop up du protocole
                    customPopUpProtocol.test_bluid();                                                                  //on affiche le popup
                    customPopUpProtocol.configuration_protocol( selected_device, ip_for_sending); //on lance le protocole à suivre
                }
            }
        });
        /**
         * Annulation de l'ajout
         */
        customPopUpAdding.getCancel_text().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //interaction, on annule l'ajout

                makeText(getApplicationContext(),"Ajout annulé", Toast.LENGTH_SHORT).show();
                customPopUpAdding.dismiss();
            }
        });
    }








    // ---------------------------------------------------------------------------------------------
    // Fonction permettant de retourner l'index lié à l'image drawable en fonction du nom de l'appareil
    // ---------------------------------------------------------------------------------------------
    public int return_index_icon(String deviceName)
    {
        int index_icon;

        if (deviceName.equals("Lampe")                      ) {
            index_icon = R.drawable.lamp;
        } else if (deviceName.equals("Four")                ) {
            index_icon = R.drawable.oven;
        } else if (deviceName.equals("Camera")                ) {
            index_icon = R.drawable.camera;
        }else if (deviceName.equals("Télévision")          ) {
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








    /* ---------------------------------------------------------------------------------------------
    * Fonction permettant de remplir une listeView à partir d'un cursor

     *      We need to define the adapter to describe the process of projecting the Cursor's data into a View.
     *      To do this we need to override the newView method and the bindView method.
     *      The naive approach to this (without any view caching) looks like the following:
     * ---------------------------------------------------------------------------------------------
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
        @SuppressLint("SetTextI18n")
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            int icon_index;

            // Find fields to populate in inflated template
            TextView tvDeviceName           = (TextView)  view.findViewById(R.id.device_name);
            TextView tvDevicePower          = (TextView)  view.findViewById(R.id.device_power);
            TextView tvDeviceStandbyPower   = (TextView)  view.findViewById(R.id.device_standby_power);
            TextView tvDeviceMeanPower      = (TextView)  view.findViewById(R.id.device_mean_power);
            TextView tvDeviceUseRate        = (TextView)  view.findViewById(R.id.device_userate);
            ImageView imDevice              = (ImageView) view.findViewById(R.id.device_thumbnail);

            // Extract properties from cursor
            String device_name              = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String device_power             = cursor.getString(cursor.getColumnIndexOrThrow("power"));
            String device_stand_by_power    = cursor.getString(cursor.getColumnIndexOrThrow("standbypower"));
            String device_mean_power        = cursor.getString(cursor.getColumnIndexOrThrow("meanpower"));
            String device_use_rate          = cursor.getString(cursor.getColumnIndexOrThrow("userate"));
            int    icon                     = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("icon")));

            // Populate fields with extracted properties
            tvDeviceName.setText(           device_name);
            tvDevicePower.setText(          "Consommation allumé : " + device_power + " Watts");
            tvDeviceStandbyPower.setText(   "Consommation éteint : " + device_stand_by_power + " Watts");
            tvDeviceMeanPower.setText(      device_mean_power + " Watts");
            tvDeviceUseRate.setText(        "Utilisation : "+device_use_rate.toString() + "h/j");
            imDevice.setImageResource(icon);
        }
    }








    // ---------------------------------------------------------------------------------------------
    // Fonction permettant d'afficher dans un Toast l'appreil selectionné.
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        makeText(this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
