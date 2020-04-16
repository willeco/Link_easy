package fr.willy.linky;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
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
    private Activity parent_activity;
    private Button button_test_config;

    //constructor
    public CustomPopUp(Activity activity, String popup)
    {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        //on charge notre layout associé au popup
        if (popup.equals("adding")){
            setContentView(R.layout.popup_device_adding);
        }
        else if (popup.equals("config")){
            setContentView(R.layout.popup_device_configuration);
        }
        else{
            setContentView(R.layout.popup_device_quick_config);
        }
        //empeche l'utilisateur de fermer le popup en appuyant à
        //l'exterieur de celui-ci.
        this.setCancelable(false);
        this.confirm_text = findViewById(R.id.confirm_text);
        this.cancel_text  = findViewById(R.id.cancel_text);
        this.titleView    = findViewById(R.id.device_popup_title);
        this.subtitleView = findViewById(R.id.device_popup_subtitle);
        this.device_spinner = findViewById(R.id.device_spinner);
        this.button_test_config = findViewById(R.id.button_test_config);
        this.parent_activity = activity;
    }

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
    public void configuration_protocol(String device)
    {
        if(device.equals("Lampe"))
        {
            TextView deviceTextView = findViewById(R.id.textView);
            TextView protocolTextView = findViewById(R.id.textView3);
            final Button faitButton = findViewById(R.id.button);
            final TextView timerTextView = findViewById(R.id.textView4);

            faitButton.setVisibility(View.VISIBLE);
            timerTextView.setVisibility(GONE);

            deviceTextView.setText(device);
            protocolTextView.setText("Alors voilà comment ça va se passer mon ptit pote, tu va te rapprocher de ta jolie lampe et tu va l'éteindre. Une fois que " +
                    "c'est éteint tu va appuyer le bouton 'c'est fait' tu aura alors 3 secondes pour allumer puis 3 autres secondes pour l'éteindre !" +
                    "Ne t'inquiète pas tout va bien se passer mon chaton");
            faitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    faitButton.setVisibility(GONE);
                    timerTextView.setVisibility(View.VISIBLE);
                    //timerTextView.setTextColor();

                    new CountDownTimer(30000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            timerTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            timerTextView.setText("done!");
                        }
                    }.start();


                }
            });
        }
    }

}
