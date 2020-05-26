package fr.willy.linky;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.text.format.Formatter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyStore;

import java.util.Calendar;

import java.io.Serializable;




public class HubActivity extends AppCompatActivity {

    // Variables privées de notre class Activité
    static private TextView    m_tview_etat;
    private TextView    m_tview_ip_phone;
    static private TextView    m_tview_nb_trames;
    static private TextView    m_tview_papp;
    static private TextView    m_tview_ad_linky;
    static private TextView    m_tview_hp;
    static private ClientUDP   m_client_udp                    = null;
    static private int         m_nb_trames_recues              = 0;
    public int activity = 0;

    private Handler         graph_handler;

    static String papp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); //Creation du super
        setContentView(R.layout.activity_main);


        // ToolBar
        // -----------------------------------------------------------------------------------------
        Toolbar toolbar             = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab    = findViewById(R.id.fab);

        // Get widgets
        // -----------------------------------------------------------------------------------------
        Button button_ask_tele_info      = findViewById(R.id.button_demande_tele_info);
        Button button_change_activity    = findViewById(R.id.button_change_activity);
        Button button_device_consumption = findViewById(R.id.button_device_consumption);

        final TextView tview_ip_er             = findViewById(R.id.field_IP_ER             );
        m_tview_ip_phone                 = findViewById(R.id.field_IP_Phone          );
        TextView tview_port_phone2er     = findViewById(R.id.field_Port_phone2er     );
        m_tview_nb_trames                = findViewById(R.id.field_Nb_trâmes         );
        m_tview_ad_linky                 = findViewById(R.id.field_Ad_linky          );
        m_tview_papp                     = findViewById(R.id.field_PAPP              );
        m_tview_hp                       = findViewById(R.id.field_HP                );
        TextView tview_hc                = findViewById(R.id.field_HC                );
        m_tview_etat                     = findViewById(R.id.field_etat);


        // Affectation par défaut
        // -----------------------------------------------------------------------------------------
        tview_ip_er.setText(        "83.205.137.12");
        tview_port_phone2er.setText(  String.valueOf(10001));
        m_tview_nb_trames.setText(    String.valueOf(0));
        m_tview_ad_linky.setText(     " ");
        m_tview_papp.setText(        " ");
        m_tview_hp.setText(          " ");
        tview_hc.setText(           "  ");

        m_nb_trames_recues          = 0;

        // Tentative de Récuperation adresse IP Wi-Fi du smartphone
        // -----------------------------------------------------------------------------------------
        get_my_ip_address();


        // Association Bouton avec le handler handler_ask_tele_info
        // -----------------------------------------------------------------------------------------
        button_ask_tele_info.setOnClickListener(handler_ask_tele_info);

        button_change_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Graph.class);
                activity = 1;
                if(Integer.parseInt((m_tview_nb_trames.getText()).toString()) != 0) {
                    startActivity(myIntent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Pas de données, pas de graphs !", Toast.LENGTH_LONG).show();
                }
                //L'app crash si on n'a pas demandé de TI


            }
        });

        //permet de changer d'activité (consommation individuelle de chaque appareils de la maison)
        button_device_consumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntentDevice = new Intent(view.getContext(), DeviceActivity.class);
                myIntentDevice.putExtra("ip_for_sending",tview_ip_er.getText().toString());
                activity = 1;
                startActivity(myIntentDevice);
            }
        });

    }

    // ---------------------------------------------------------------------------------------------
    // get_my_ip_address
    // ---------------------------------------------------------------------------------------------
    // Pour connaitre son addresse IP depuis le smartphone,
    // Menu "Système" Puis "A propos du téléphone"

    private String get_my_ip_address() {

        int     ipAddress;
        String  ipAddress_str = "";

        WifiManager wm          = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo       = wm.getConnectionInfo();

        try {
            //int ipAddressNetmask    = wm.getDhcpInfo().netmask;
            ipAddress           = wm.getDhcpInfo().ipAddress;
            ipAddress_str       = intIpToStringIp(ipAddress);

            m_tview_ip_phone.setText(  ipAddress_str);
            m_tview_etat.setText("Addresse IP du smartphone récupérée " );
        } catch (Exception e) {
            // Affichage message d'erreur
            m_tview_etat.setText("Erreur récupération addresse IP " );
            m_tview_ip_phone.setText("?");
            Log.e("Linky Send UDP", "Problème récupération adresse IP", e);
        }

        return(ipAddress_str);
    }


    @Override
    protected void onStart() {
        super.onStart();
        activity = 0;
        //Toast.makeText(getApplicationContext(), "Linky = OnStart()", Toast.LENGTH_LONG).show();
    }

    // ---------------------------------------------------------------------------------------------
    // onStop
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first

        // Destruction du Thread ayant servi à envoyer et recevoir les messages UDP
        if(activity == 0){
            if (m_client_udp != null)
            {
                Toast.makeText(getApplicationContext(), "Linky : Fin du Thread", Toast.LENGTH_LONG).show();
                m_client_udp.fermer();
            }
        }


        // Avertir l'utilisateur
       // Toast.makeText(getApplicationContext(), "Linky: onStop called", Toast.LENGTH_LONG).show();
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

    // ---------------------------------------------------------------------------------------------
    // Gère les communications avec le thread réseau
    // ---------------------------------------------------------------------------------------------
    final static private Handler m_handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            if (msg.what == ClientUDP.CODE_RECEPTION)
            {

                // ---------------------------------------------------------------------------------------------
                // Test envoi a Graph




                // ---------------------------------------------------------------------------------------------



                m_nb_trames_recues += 1;
                m_tview_nb_trames.setText(    String.valueOf(m_nb_trames_recues));

                // Bundle bundle = msg.getData();
                // String string = bundle.getString(ClientUDP.CODE_RECEPTION);
                // m_tview_etat.setText( string );
                String trame_linky = (String) msg.obj;
                m_tview_etat.setText( "                                       "  );
                m_tview_etat.setText( "Reçu: " + trame_linky );

                // Transformation de trame en un objet JSON pour faciliter le parsing
                // https://www.tutorialspoint.com/android/android_json_parser.htm

                // https://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
                // Lire le post 32 "Here is a generic example of using a weak reference and
                // static handler class to resolve the problem (as recommended in the Lint documentation)"


                try {
                     JSONObject jsonObj = new JSONObject( trame_linky ); //passage de string à JSON
                     papp               = jsonObj.getString("PAPP");
                     m_tview_papp.setText(papp);
                     DataHolder.getInstance().deleteData();
                     DataHolder.getInstance().setData(papp);
                } catch (Exception e) {
                }
                try {
                    String adco;
                    JSONObject jsonObj = new JSONObject( trame_linky );
                    adco               = jsonObj.getString("ADCO");
                    m_tview_ad_linky.setText(adco);
                } catch (Exception e) {
                }
                try {
                    String base;
                    JSONObject jsonObj = new JSONObject( trame_linky );
                    base               = jsonObj.getString("BASE");
                    m_tview_hp.setText(base);
                } catch (Exception e) {
                }

            }
        }
    };

    // ---------------------------------------------------------------------------------------------
    // handler_ask_tele_info : Use to manage the push button "Demande de téléinformation"
    // ---------------------------------------------------------------------------------------------
    View.OnClickListener handler_ask_tele_info = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Step 0 - Mise à jour du champ état
            TextView tview_etat = findViewById(R.id.field_etat);
            tview_etat.setText("Demande de télé-info en cours");


            // Step 1 - Get IP erlinky
            TextView tview_ip_er          = findViewById(R.id.field_IP_ER  ); //Widget permet de recup adresse IP
            String ip_for_sending         = tview_ip_er.getText().toString(); //On recupere l'adresse IP



            // Step 2 - Get port number to send a request
            TextView tview_port_phone2er    = findViewById(R.id.field_Port_phone2er); //recuperation port Rasberry
            String port_for_sending         = tview_port_phone2er.getText().toString(); // String
            int port_phone2er               = Integer.parseInt(port_for_sending);   // conversion en int

            ask_tele_info(ip_for_sending, port_phone2er);
        }
    };

    // ---------------------------------------------------------------------------------------------
    // Fonction : Affichage message d'erreur
    // ---------------------------------------------------------------------------------------------
    public void DisplayMessage(String message, boolean error_message) {

        TextView tview_etat = findViewById(R.id.field_etat);

        Calendar calndr     = Calendar.getInstance();

        // Affichage message d'erreur dans un Toast
        Toast toast         = Toast.makeText(getApplicationContext(),  message + " " + calndr.getTime() , Toast.LENGTH_LONG);
        TextView toast_id   = (TextView) toast.getView().findViewById(android.R.id.message);

        // Gestion de la couleur du texte
        if ( error_message )  {
            toast_id.setTextColor(Color.RED);
        }

        toast.show();

        // Affichage du meme message d'erreur dans la zone Etat
        tview_etat.setText(message );

    }

    // ---------------------------------------------------------------------------------------------
    // Fonction : Transformation Entier en adresse IP pour affichage
    // ---------------------------------------------------------------------------------------------
    private String intIpToStringIp(int i) {

        int a = ( i & 0xFF);
        int b = ((i >> 8 ) & 0xFF);
        int c = ((i >> 16 ) & 0xFF);
        int d = ((i >> 24 ) & 0xFF );

        return a + "." + b + "." +  c + "." + d ;

        //return ((i >> 24 ) & 0xFF ) + "." +
        //        ((i >> 16 ) & 0xFF) + "." +
        //        ((i >> 8 ) & 0xFF) + "." +
        //        ( i & 0xFF) ;
    }

    public static void ask_tele_info(String ip_for_sending, int port_phone2er)
    {



        // Step 3 - Create a datagram socket to ask teleinformation
        try {

            // Attention, a chaque fois, que l'on appui sur le bouton, un Thread est créé par
            // la classe UDP. A revoir. car la mémoire du téléphone risque de saturer
            // Le Thread ne sera jamais tué.
            if (m_client_udp == null)
            {
                m_client_udp = new ClientUDP(m_handler);
            }

            if (m_client_udp != null)
            {
                m_client_udp.envoyer("Hello", ip_for_sending, port_phone2er); // à l'IP du Rpi et au port 10001
            }

            //DisplayMessage("Requête télé-info envoyée @IP:" + ip_for_sending +  " port:" + port_phone2er, false);

        } catch (Exception e) {
            String message_error  = "Envoi message UDP vers Linky impossible !!";
            //DisplayMessage(message_error, true);
            Log.e("Linky Send UDP", message_error, e);
        }
    }

}
