package fr.willy.linky;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.util.Calendar;

import static android.widget.Toast.makeText;


public class HubActivity extends AppCompatActivity {

    private HubActivity         hubActivity     = this;
    static private ClientUDP    m_client_udp    = null;

    @SuppressLint("StaticFieldLeak")
    static ImageView            firstLine,
                                secondLine,
                                thirdLine,
                                firstPoint,
                                secondPoint;

    @SuppressLint("StaticFieldLeak")
    static TextView             etatText;

    @SuppressLint("StaticFieldLeak")
    static Button               pappButton,
                                baseButton,
                                iinstButton,
                                ptecButton,
                                information_papp,
                                information_inst,
                                information_base,
                                information_ptec,
                                button_ask_tele_info,
                                button_device_consumption;

    public int                  activity    = 0;

    static double               timerStart  = 0;
    static double               timerEnd    = 0;
    static double               timeOut     = 0;

    static String               papp        = "0"; //on initialise à Zero
    static String               iinst       = "0";
    static String               base        = "0";//random
    static String               ptec        = "0";
    static String               IP;


    /**
     * ####################################################################################################
     * Création de l'activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //on charge l'affichage de de l'ctivité

        //récupération de l'adresse IP renseigné dans LoginActivity
        Intent intent = getIntent();
        if (intent.hasExtra("ip_for_sending")){
            IP = intent.getStringExtra("ip_for_sending");
        }

        //récupération entités d'affichage
        Toolbar toolbar             = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button_ask_tele_info        = findViewById(R.id.button_demande_tele_info);
        button_device_consumption   = findViewById(R.id.button_device_consumption);
        pappButton                  = findViewById(R.id.button3);
        iinstButton                 = findViewById(R.id.button4);
        baseButton                  = findViewById(R.id.button5);
        ptecButton                  = findViewById(R.id.button6);
        firstLine                   = findViewById(R.id.imageView5);
        secondLine                  = findViewById(R.id.imageView2);
        thirdLine                   = findViewById(R.id.imageView3);
        firstPoint                  = findViewById(R.id.imageView6);
        secondPoint                 = findViewById(R.id.imageView4);
        etatText                    = findViewById(R.id.textView17);
        information_papp            = findViewById(R.id.information_papp);
        information_inst            = findViewById(R.id.information_inst);
        information_base            = findViewById(R.id.information_hp);
        information_ptec            = findViewById(R.id.information_hc);


        /**
         * Tentative de Récuperation adresse IP Wi-Fi du smartphone
         */
        get_my_ip_address();



        /**
         * Demande de télé-information si appuie sur le bouton
         */
        button_ask_tele_info.setOnClickListener(handler_ask_tele_info);
        button_ask_tele_info.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view){
                etatText.setText("Demande d'informations..."); //on informe l'utilisateur que la demande a été envoyé
                timeOut = 0;
                secondPoint.setColorFilter( null);
                thirdLine.setColorFilter(   null);
            }
        });



        /**
         * Interaction avec le bouton PAPP
         */
        pappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!papp.equals("0"))
                {
                    Intent myIntent = new Intent(view.getContext(), Graph.class);
                    myIntent.putExtra("ip_for_sending",IP);
                    myIntent.putExtra("typeOfGraph","papp");
                    activity = 1;
                    startActivity(myIntent);
                }
                else{
                    makeText(getApplicationContext(),"Vous n'avez pas encore reçu cette information", Toast.LENGTH_SHORT).show();
                }
            }
        });



        /**
         * Interaction avec le bouton BASE
         */
        baseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                if(!base.equals("0"))
                {
                    Intent myIntent = new Intent(view.getContext(), Graph.class);
                    myIntent.putExtra("ip_for_sending",IP);
                    myIntent.putExtra("typeOfGraph","base");
                    activity = 1;
                    startActivity(myIntent);
                }
                else{
                    makeText(getApplicationContext(),"Vous n'avez pas encore reçu cette information", Toast.LENGTH_SHORT).show();
                }
                }

            }
        });



        /**
         * Interaction avec le bouton device consumption
         */
        //permet de changer d'activité (consommation individuelle de chaque appareils de la maison)
        button_device_consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntentDevice = new Intent(view.getContext(), DeviceActivity.class);
                myIntentDevice.putExtra("ip_for_sending",IP);
                activity = 1;
                startActivity(myIntentDevice);
            }
        });



        /**
         * Affichage des pop up
         */
        //------------------------------------------------
        //Definition de PAPP
        //------------------------------------------------
        information_papp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final CustomPopUp customPopUpInformation = new CustomPopUp(hubActivity, "papp"); //on créer le popup d'ajout

                customPopUpInformation.getPower_name().setText("PAPP");
                customPopUpInformation.getPower_definition().setText(R.string.papp_def);
                customPopUpInformation.test_bluid(); //on affiche le popup

                customPopUpInformation.getQuit_information().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customPopUpInformation.dismiss();
                    }
                });
            }
        });
        //------------------------------------------------
        //Definition de IINST
        //------------------------------------------------
        information_inst.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final CustomPopUp customPopUpInformation = new CustomPopUp(hubActivity, "papp"); //on créer le popup d'ajout

                customPopUpInformation.getPower_name().setText("INST");
                customPopUpInformation.getPower_definition().setText(R.string.inst_def);
                customPopUpInformation.test_bluid(); //on affiche le popup

                customPopUpInformation.getQuit_information().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customPopUpInformation.dismiss();
                    }
                });
            }
        });
        //------------------------------------------------
        // Définition de BASE
        //------------------------------------------------
        information_base.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final CustomPopUp customPopUpInformation = new CustomPopUp(hubActivity, "papp"); //on créer le popup d'ajout

                customPopUpInformation.getPower_name().setText("BASE");
                customPopUpInformation.getPower_definition().setText(R.string.base_def);

                customPopUpInformation.test_bluid(); //on affiche le popup

                customPopUpInformation.getQuit_information().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customPopUpInformation.dismiss();
                    }
                });
            }
        });
        //------------------------------------------------
        // Définition de PTEC
        //------------------------------------------------
        information_ptec.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final CustomPopUp customPopUpInformation = new CustomPopUp(hubActivity, "papp"); //on créer le popup d'ajout

                customPopUpInformation.getPower_name().setText("PTEC");
                customPopUpInformation.getPower_definition().setText(R.string.ptec_def);
                customPopUpInformation.test_bluid(); //on affiche le popup

                customPopUpInformation.getQuit_information().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customPopUpInformation.dismiss();
                    }
                });
            }
        });


        //DEMANDE DE TELE INFO
        ask_tele_info(IP,10001);
    }
    /**
     * ####################################################################################################
     */







    /**
     * ####################################################################################################
     * Permet de relancer l'activité en cas de besoin
     */
    @Override
    protected void onStart() {
        super.onStart();
        activity = 0;
    }






    /**
     * ####################################################################################################
     * Permet d'arreter l'application en cas de besoin
     */
    @Override
    protected void onStop() {
        super.onStop(); // Always call the superclass method first
        // Destruction du Thread ayant servi à envoyer et recevoir les messages UDP
        if(activity == 0){
            if (m_client_udp != null){
                Toast.makeText(getApplicationContext(), "Communication coupée", Toast.LENGTH_LONG).show();
                m_client_udp.fermer();
            }
        }
    }








    // ---------------------------------------------------------------------------------------------
    // Fonction permettant de récuperer l'adresse ip de son smartphone
    // ---------------------------------------------------------------------------------------------
    private void get_my_ip_address(){
        int         ip_address;
        String      ip_address_str  = "";
        WifiManager wm              = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert      wm              != null;
        WifiInfo    wifiInfo        = wm.getConnectionInfo();
        try {
            ip_address              = wm.getDhcpInfo().ipAddress;
            ip_address_str          = intIpToStringIp(ip_address);
        }
        catch (Exception e){
            // Affichage message d'erreur
            Log.e("Linky Send UDP", "Problème récupération adresse IP", e);
        }
    }




    // ---------------------------------------------------------------------------------------------
    // Fonction permettant la gestion des communications avec le thread réseau
    // ---------------------------------------------------------------------------------------------
    @SuppressLint("HandlerLeak")
    final static private Handler m_handler = new Handler(){
        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        public void handleMessage(Message msg){
            timeOut     = timeOut + timerEnd - timerStart;
            timerStart  = 0;
            timerEnd    = 0;
            timerStart = System.currentTimeMillis()%1000;
            super.handleMessage(msg);

            if (msg.what == ClientUDP.CODE_RECEPTION){
                etatText.setText("Demande d'informations...");
                HubActivity.secondLine.setColorFilter(R.color.dark_linky, PorterDuff.Mode.LIGHTEN);
                String trame_linky = (String) msg.obj;

                // Transformation de trame en un objet JSON pour faciliter le parsing
                // https://www.tutorialspoint.com/android/android_json_parser.htm
                // https://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
                // Lire le post 32 "Here is a generic example of using a weak reference and
                // static handler class to resolve the problem (as recommended in the Lint documentation)"

                /**
                 * Trie de l'information reçue
                 * on garde les étiquettes :
                 *      - PAPP
                 *      - IINST
                 *      - BASE
                 *      - PTEC
                 */
                //----------
                // PAPP
                //----------
                try { //on met à jour l'information sur la case PAPP
                     JSONObject jsonObj = new JSONObject( trame_linky ); //passage de string à JSON
                     papp               = jsonObj.getString("PAPP");
                     pappButton.setText("Puissance apparente\n\n"+papp+"\nVolt/Ampères");
                     DataHolder.getInstance().deleteData();
                     DataHolder.getInstance().setData(papp);
                } catch (Exception ignored){}
                //----------
                // IINST
                //----------
                try { //on met à jour l'information sur la case IINST
                    JSONObject jsonObj = new JSONObject( trame_linky );
                    iinst               = jsonObj.getString("IINST");
                    iinstButton.setText("Intensité instantanée\n\n"+iinst+"\nAmpères");
                } catch (Exception ignored){}
                //----------
                // BASE
                //----------
                try { //on met à jour l'information sur la case BASE
                    JSONObject jsonObj = new JSONObject( trame_linky );
                    base               = jsonObj.getString("BASE");
                    baseButton.setText("Base\n\n"+base+"\nWatt/heure");
                } catch (Exception ignored){}
                //----------
                // PTEC
                //----------
                try { //on met à jour l'information sur la case PTEC
                    JSONObject jsonObj = new JSONObject( trame_linky );
                    ptec               = jsonObj.getString("PTEC");
                    ptecButton.setText("PTEC\n\n"+"FORFAIT EN COURS"+"\n"+ptec);
                }
                catch (Exception e) {
                    ptecButton.setText("PTEC\n\n"+"FORFAIT EN COURS"+"\nBASE");
                }


                /**
                 * on informe l'utilisateur de la progression de la communication
                 */
                if(!pappButton.getText().toString().equals("PAPP") && !iinstButton.getText().toString().equals("IINST") && timeOut<=170){
                    etatText.setText("Réception...");
                    secondPoint.setColorFilter(R.color.dark_linky, PorterDuff.Mode.ADD);
                }
            }

            timerEnd = System.currentTimeMillis()%1000;


            /**
             * on informe l'utilisateur que la communication est terminée
             */
            if(timeOut >= 170){
                etatText.setText("Fin de réception.");
                thirdLine.setColorFilter(R.color.dark_linky, PorterDuff.Mode.LIGHTEN);
            }
        }
    };







    // ---------------------------------------------------------------------------------------------
    // handler_ask_tele_info : Use to manage the push button "Demande de téléinformation"
    // ---------------------------------------------------------------------------------------------
    View.OnClickListener handler_ask_tele_info = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ask_tele_info(IP, 10001);
        }
    };






    // ---------------------------------------------------------------------------------------------
    // Fonction : Affichage message d'erreur
    // ---------------------------------------------------------------------------------------------
    public void DisplayMessage(String message, boolean error_message){
        Calendar calndr     = Calendar.getInstance();

        // Affichage message d'erreur dans un Toast
        Toast toast         = Toast.makeText(getApplicationContext(),  message + " " + calndr.getTime() , Toast.LENGTH_LONG);
        TextView toast_id   = (TextView) toast.getView().findViewById(android.R.id.message);

        // Gestion de la couleur du texte
        if ( error_message ){ toast_id.setTextColor(Color.RED);}

        toast.show();
    }






    // ---------------------------------------------------------------------------------------------
    // Fonction : Transformation Entier en adresse IP pour affichage
    // ---------------------------------------------------------------------------------------------
    private String intIpToStringIp(int i){

        int a = ( i & 0xFF);
        int b = ((i >> 8 ) & 0xFF);
        int c = ((i >> 16 ) & 0xFF);
        int d = ((i >> 24 ) & 0xFF );

        return a + "." + b + "." +  c + "." + d ;
    }






    // ---------------------------------------------------------------------------------------------
    // Fonction : Transformation Entier en adresse IP pour affichage
    // ---------------------------------------------------------------------------------------------
    @SuppressLint("LongLogTag")
    public static void ask_tele_info(String ip_for_sending, int port_phone2er){
        // Step 3 - Create a datagram socket to ask teleinformation
        try {
            // Attention, a chaque fois, que l'on appuie sur le bouton, un Thread est créé par
            // la classe UDP. A revoir. car la mémoire du téléphone risque de saturer
            // Le Thread ne sera jamais tué.
            if (m_client_udp == null){
                m_client_udp = new ClientUDP(m_handler);
            }
            Log.i("TRUC DE BASE", " client udp n'est pas null");
            m_client_udp.envoyer("Hello", IP, 10001); // à l'IP du Rpi et au port 10001
        }
        catch (Exception e) {
            String message_error  = "Envoi message UDP vers Linky impossible !!";
            //DisplayMessage(message_error, true);
            Log.e("Linky Send UDP", message_error, e);
        }
    }






    // ---------------------------------------------------------------------------------------------
    // onCreateOptionsMenu
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    // onOptionsItemSelected
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}





