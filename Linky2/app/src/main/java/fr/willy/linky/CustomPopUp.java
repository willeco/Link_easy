package fr.willy.linky;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.GONE;
import static android.widget.Toast.makeText;

public class CustomPopUp extends Dialog {
    /*
    Cette classe permet de générer un popup personnalisé.
    Elle est associée à un layout spécifique nommé popup_device_adding.
     */

    //fields
    private String title;
    private String subtitle;
    private TextView confirm_text, cancel_text; //bouton "confirmer" et "annuler" du popup
    private TextView titleView, subtitleView;
    private Spinner device_spinner;
    private DeviceActivity parent_activity;
    private LoginActivity loginActivity;
    private Button understand;
    private Button button_test_config;

    //constructor
    public CustomPopUp(DeviceActivity deviceActivity, String popup)
    {


        super(deviceActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        //on charge notre layout associé au popup
        if (popup.equals("adding")){
            setContentView(R.layout.popup_device_adding);
        }else if(popup.equals("start_app")){
            setContentView(R.layout.popup_start_application);
        }
        else {
            setContentView(R.layout.popup_device_configuration);
        }

        //empeche l'utilisateur de fermer le popup en appuyant à
        //l'exterieur de celui-ci.
        this.setCancelable(false);
        this.confirm_text = findViewById(R.id.confirm_text);
        this.cancel_text  = findViewById(R.id.cancel_text);
        this.titleView    = findViewById(R.id.device_popup_title);
        this.subtitleView = findViewById(R.id.device_popup_subtitle);
        this.device_spinner = findViewById(R.id.device_spinner);
        this.parent_activity = deviceActivity;
    }

    //constructor 2
    public CustomPopUp(LoginActivity loginActivity, String popup)
    {


        super(loginActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        setContentView(R.layout.popup_start_application);

        //empeche l'utilisateur de fermer le popup en appuyant à
        //l'exterieur de celui-ci.
        this.setCancelable(false);
        this.understand = findViewById(R.id.understand);
        this.loginActivity = loginActivity;
    }

    public Button getUnderstand(){ return this.understand;}


    //changer le titre du popup
    public void setTitle(String title){this.title = title;}

    //changer le sous titre du popup
    public void setSubtitle(String subtitle){this.subtitle = subtitle;}

    //recupere l'appareil selectionné dans le spinner (menu déroulant)
    public String getSpinnerData(){
        final String s = this.device_spinner.getSelectedItem().toString();
        return s;
        //this.device_spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this.parent_activity);
    }

    //recupere le champ "confirmer" du popup
    public TextView getConfirm_text(){return confirm_text;}

    //recupere le champ "annuler" du popup
    public TextView getCancel_text(){return cancel_text;}

    public Button getButton_test_config(){return button_test_config;}

    //affiche le popup
    public void test_bluid(){
        show();
    }

    //gérer configure
    public void configuration_protocol(String device, final CustomPopUp popUp, final String ip_for_sending)
    {

        if(device.equals("Lampe"))
        {
            TextView deviceTextView = findViewById(R.id.textView);
            final TextView protocolTextView = findViewById(R.id.textView3);
            final Button faitButton = findViewById(R.id.button);
            final TextView timerTextView = findViewById(R.id.textView4);
            final Button ajouterButton = findViewById(R.id.button2);

            final int[] pappOn = new int[1];
            final int[] pappOff = new int[1];
            final int[] diffPapp = new int[1];

            ajouterButton.setVisibility(GONE);
            faitButton.setVisibility(View.VISIBLE);
            timerTextView.setVisibility(GONE);

            faitButton.setText("C'EST FAIT !");
            deviceTextView.setText(device);
            protocolTextView.setText("Alors voilà comment ça va se passer mon ptit pote, tu va te rapprocher de ta jolie lampe et tu va l'éteindre. Une fois que " +
                    "c'est éteint tu va appuyer le bouton 'c'est fait' tu aura alors 3 secondes pour allumer puis 3 autres secondes pour l'éteindre !" +
                    "Ne t'inquiète pas tout va bien se passer mon chaton");
            faitButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    HubActivity.ask_tele_info(ip_for_sending,10001); //Récupéré l'addresse ip peut être

                    faitButton.setVisibility(GONE);
                    timerTextView.setVisibility(View.VISIBLE);
                    protocolTextView.setText("ALLUME TON APPAREIL !");
                    new CountDownTimer(5000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            timerTextView.setText("" + millisUntilFinished / 1000 + "secondes");

                        }

                        public void onFinish() {
                            pappOn[0] = Integer.parseInt(HubActivity.papp);
                            protocolTextView.setText("ETEINT TON APPAREIL ! \n pappOn = "+pappOn[0]);
                            new CountDownTimer(5000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    timerTextView.setText("" + millisUntilFinished / 1000+ "secondes");
                                }

                                public void onFinish() {
                                    pappOff[0] = Integer.parseInt(HubActivity.papp);
                                    faitButton.setText("AJOUTER APPAREIL");
                                    timerTextView.setVisibility(GONE);
                                    diffPapp[0] = pappOn[0]-pappOff[0];
                                    parent_activity.setPower(diffPapp[0]);
                                    protocolTextView.setText("OK on est bon \n pappOff = "+pappOff[0]+"\n papp = "+diffPapp[0]);
                                    ajouterButton.setVisibility(View.VISIBLE);

                                    // Insertion d'un appareil
                                    int icon_index = parent_activity.return_index_icon(parent_activity.getSelected_device());
                                    Devices a = new Devices(parent_activity.getDb().getSize()+1,parent_activity.getSelected_device(), parent_activity.getPower(),icon_index);
                                    parent_activity.getDb().insert(a);
                                    parent_activity.getDb().close();
                                    parent_activity.display_listview_of_Devices(false);
                                    makeText(parent_activity.getApplicationContext(),parent_activity.getSelected_device() + " ajouté. ", Toast.LENGTH_SHORT).show();

                                }
                            }.start();

                        }
                    }.start();




                }

            });

            ajouterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUp.dismiss();
                }
            });
        }
        else{
            popUp.dismiss();
            int icon_index = parent_activity.return_index_icon(parent_activity.getSelected_device());
            Devices a = new Devices(parent_activity.getDb().getSize()+1,parent_activity.getSelected_device(), 0,icon_index);
            parent_activity.getDb().insert(a);
            parent_activity.getDb().close();
            parent_activity.display_listview_of_Devices(false);
            makeText(parent_activity.getApplicationContext(),parent_activity.getSelected_device() + " ajouté. ", Toast.LENGTH_SHORT).show();
        }
    }

}
