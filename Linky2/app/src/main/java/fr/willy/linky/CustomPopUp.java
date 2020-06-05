package fr.willy.linky;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    private QuickConfigActivity quickConfigActivity;
    private HubActivity hubActivity;
    private Button understand;
    private Button next, tuto_understand;
    private TextView dont_show_again;
    private Button button_test_config;
    private Button confirm_delete, cancel_delete;
    private Button quit_infomration;
    private TextView power_name, power_definition;

    //constructor
    public CustomPopUp(DeviceActivity deviceActivity, String popup)
    {


        super(deviceActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        //on charge notre layout associé au popup
        if (popup.equals("adding")) {
            setContentView(R.layout.popup_device_adding);
        }
        else if(popup.equals("tuto add")){
            setContentView(R.layout.popup_device_tuto_add);
        }
        else if(popup.equals("tuto config")){
            setContentView(R.layout.popup_device_tuto_config);
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
        this.tuto_understand = findViewById(R.id.tuto_understand);
        this.next = findViewById(R.id.next);
        this.dont_show_again = findViewById(R.id.dont_show_again);
        this.parent_activity = deviceActivity;
    }

    //constructor 2
    public CustomPopUp(LoginActivity loginActivity)
    {
        super(loginActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        setContentView(R.layout.popup_start_application);

        //empeche l'utilisateur de fermer le popup en appuyant à
        //l'exterieur de celui-ci.
        this.setCancelable(false);
        this.understand = findViewById(R.id.understand);
        this.loginActivity = loginActivity;
    }

    //constructor 3
    public CustomPopUp(QuickConfigActivity quickConfigActivity)
    {
        super(quickConfigActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        setContentView(R.layout.popup_delete);

        //empeche l'utilisateur de fermer le popup en appuyant à
        //l'exterieur de celui-ci.
        this.setCancelable(false);
        this.confirm_delete = findViewById(R.id.confirm_delete);
        this.cancel_delete = findViewById(R.id.cancel_delete);
        this.quickConfigActivity = quickConfigActivity;
    }


    //constructor 4
    public CustomPopUp(HubActivity hubActivity, String power)
    {
        super(hubActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        setContentView(R.layout.popup_information_power);

        //empeche l'utilisateur de fermer le popup en appuyant à
        //l'exterieur de celui-ci.
        this.setCancelable(false);
        this.power_name = findViewById(R.id.power_name);
        this.power_definition = findViewById(R.id.power_definition);
        this.quit_infomration = findViewById(R.id.quit_information);
        this.hubActivity = hubActivity;
    }


    public Button getQuit_infomration(){ return this.quit_infomration;}
    public Button getConfirm_delete(){ return this.confirm_delete;}
    public Button getCancel_delete(){ return this.cancel_delete;}
    public Button getUnderstand(){ return this.understand;}
    public Button getNext(){ return this.next;}
    public Button getTuto_understand(){ return this.tuto_understand;}
    public TextView getDont_show_again(){ return this.dont_show_again;}
    public TextView getPower_name(){ return this.power_name;}
    public TextView getPower_definition(){ return this.power_definition;}
    public Spinner getDevice_spinner(){ return this.device_spinner;}


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
    @SuppressLint("LongLogTag")
    public void configuration_protocol(String device, final CustomPopUp popUp, final String ip_for_sending)
    {
        int debug =0;
        if(debug == 0) {
            if (!device.equals("")) {
                TextView deviceTextView = findViewById(R.id.textView);
                final TextView protocolTextView = findViewById(R.id.textView3);
                final Button faitButton = findViewById(R.id.button);
                final Button ajouterButton = findViewById(R.id.button2);
                final CheckBox debranchable = findViewById(R.id.checkBox);
                final TextView tauxUtilisation = findViewById(R.id.textView18);
                final EditText tauxUtilisationEdit = findViewById(R.id.editTextTextPersonName);

                final int[] pappUnplugged = new int[1];
                final int[] pappOn = new int[1];
                final int[] pappOff = new int[1];
                final int[] diffPappOnOff = new int[1];
                final int[] diffPappUnpluggedOff = new int[1];

                final int[] counter = {6};

                final String[] tauxUtilisationDouble = new String[1];

                ajouterButton.setVisibility(GONE);
                faitButton.setVisibility(View.GONE);
                debranchable.setVisibility(View.VISIBLE);

                faitButton.setText("OK");
                deviceTextView.setText(device);
                protocolTextView.setText("Pour configurer votre appareil, veillez à être proche de celui ci. Si vous avez la possibilité de le débrancher, veuillez cocher " +
                        "la case ci-dessous, puis indiquez le taux d'utilisation de l'appareil. A présent, veuillez éteindre votre appareil.");

                tauxUtilisationEdit.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        faitButton.setVisibility(View.GONE);
                    }

                    //Si le texte du taux d'utilisation est change, on affiche "ok"
                    public void afterTextChanged(Editable s) {
                        if(tauxUtilisationEdit.getText().toString().matches(""))
                        {

                        }
                        else
                        {
                            tauxUtilisationDouble[0] = tauxUtilisationEdit.getText().toString();
                            faitButton.setVisibility(View.VISIBLE);
                        }

                    }
                });

                faitButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        tauxUtilisation.setVisibility(GONE);
                        tauxUtilisationEdit.setVisibility(GONE);
                        faitButton.setVisibility(GONE);
                        debranchable.setVisibility(GONE);
                        HubActivity.ask_tele_info(ip_for_sending, 10001);


                        //Demarage du protocol pour un appareil debranchable
                        if (debranchable.isChecked() == true) {
                            new CountDownTimer(5000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    protocolTextView.setText("Veuillez débrancher votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                    counter[0]--;
                                    protocolTextView.setText("Veuillez débrancher votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                }

                                public void onFinish() {
                                    protocolTextView.setText("Veuillez débrancher votre appareil.\n0 secondes");
                                    faitButton.setVisibility(View.VISIBLE);
                                }
                            }.start();


                            faitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pappUnplugged[0] = Integer.parseInt(HubActivity.papp);
                                    protocolTextView.setText("Veuillez rebrancher et allumer votre appareil.\n" + String.valueOf(counter[0]) + " secondes");

                                    counter[0] = 6;
                                    faitButton.setVisibility(GONE);

                                    new CountDownTimer(5000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            protocolTextView.setText("Veuillez rebrancher et allumer votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                            counter[0]--;
                                            protocolTextView.setText("Veuillez rebrancher et allumer votre appareil. \n" + String.valueOf(counter[0]) + " secondes");
                                        }

                                        public void onFinish() {
                                            protocolTextView.setText("Veuillez rebrancher et allumer votre appareil.\n0 secondes");
                                            faitButton.setVisibility(View.VISIBLE);
                                        }
                                    }.start();


                                    faitButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            pappOn[0] = Integer.parseInt(HubActivity.papp);

                                            counter[0] = 6;
                                            faitButton.setVisibility(GONE);

                                            new CountDownTimer(5000, 1000) {
                                                public void onTick(long millisUntilFinished) {
                                                    protocolTextView.setText("Veuillez éteindre votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                                    counter[0]--;
                                                    protocolTextView.setText("Veuillez éteindre votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                                }

                                                public void onFinish() {
                                                    protocolTextView.setText("Veuillez éteindre votre appareil.\n0 secondes");
                                                    faitButton.setVisibility(View.VISIBLE);
                                                }
                                            }.start();

                                            faitButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    pappOff[0] = Integer.parseInt(HubActivity.papp);
                                                    faitButton.setText("OK");
                                                    faitButton.setVisibility(GONE);
                                                    diffPappOnOff[0] = pappOn[0] - pappOff[0];
                                                    diffPappUnpluggedOff[0] = pappOff[0] - pappUnplugged[0];
                                                    parent_activity.setPower(diffPappOnOff[0]);
                                                    parent_activity.setstandbypower(diffPappUnpluggedOff[0]);
                                                    protocolTextView.setText("La configuration est terminée.");
                                                    ajouterButton.setVisibility(View.VISIBLE);


                                                    // Insertion d'un appareil

                                                    int icon_index = parent_activity.return_index_icon(parent_activity.getSelected_device());

                                                    //Creation de l'objet device
                                                    Devices a = new Devices(parent_activity.getDb().getSize() + 1,
                                                            icon_index,
                                                            parent_activity.getSelected_device(),
                                                            parent_activity.getPower(),
                                                            parent_activity.getstandbypower(),
                                                            (parent_activity.getPower()*Float.parseFloat(tauxUtilisationDouble[0])+parent_activity.getstandbypower()*(24-Float.parseFloat(tauxUtilisationDouble[0])))/24 ,
                                                            Float.parseFloat(tauxUtilisationDouble[0]),0);

                                                    parent_activity.getDb().insert(a);
                                                    parent_activity.getDb().close();

                                                    parent_activity.display_listview_of_Devices();
                                                    makeText(parent_activity.getApplicationContext(), parent_activity.getSelected_device() + " ajouté. ", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        //Demarrage du protocol pour un device non debranchable
                        } else {
                            protocolTextView.setText("Veuillez allumer votre appareil.\n");

                            counter[0] = 6;
                            faitButton.setVisibility(GONE);

                            new CountDownTimer(5000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    protocolTextView.setText("Veuillez allumer votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                    counter[0]--;
                                    protocolTextView.setText("Veuillez allumer votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                }

                                public void onFinish() {
                                    protocolTextView.setText("Veuillez allumer votre appareil.\n0 secondes");
                                    faitButton.setVisibility(View.VISIBLE);
                                }
                            }.start();


                            faitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pappOn[0] = Integer.parseInt(HubActivity.papp);

                                    counter[0] = 6;
                                    faitButton.setVisibility(GONE);

                                    new CountDownTimer(5000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            protocolTextView.setText("Veuillez éteindre votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                            counter[0]--;
                                            protocolTextView.setText("Veuillez éteindre votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                                        }

                                        public void onFinish() {
                                            protocolTextView.setText("Veuillez éteindre votre appareil.\n0 secondes");
                                            faitButton.setVisibility(View.VISIBLE);
                                        }
                                    }.start();

                                    faitButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            pappOff[0] = Integer.parseInt(HubActivity.papp);
                                            faitButton.setText("OK");
                                            faitButton.setVisibility(GONE);
                                            diffPappOnOff[0] = pappOn[0] - pappOff[0];
                                            parent_activity.setPower(diffPappOnOff[0]);
                                            ;
                                            protocolTextView.setText("La configuration est terminé.");
                                            ajouterButton.setVisibility(View.VISIBLE);


                                            // Insertion d'un appareil
                                            int icon_index = parent_activity.return_index_icon(parent_activity.getSelected_device());
                                            //Creation de l'objet device
                                            Devices a = new Devices(parent_activity.getDb().getSize() + 1, icon_index, parent_activity.getSelected_device(), parent_activity.getPower(), 0, (pappOn[0]*Float.parseFloat(tauxUtilisationDouble[0])+pappOff[0]*(24-Float.parseFloat(tauxUtilisationDouble[0])))/24, Float.parseFloat(tauxUtilisationDouble[0]),0);
                                            parent_activity.getDb().insert(a);
                                            parent_activity.getDb().close();
                                            parent_activity.display_listview_of_Devices();
                                            makeText(parent_activity.getApplicationContext(), parent_activity.getSelected_device() + " ajouté. ", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });
                        }


                    }
                });
                ajouterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUp.dismiss();
                    }
                });
            } else {
                popUp.dismiss();
                int icon_index = parent_activity.return_index_icon(parent_activity.getSelected_device());
                Devices a = new Devices(parent_activity.getDb().getSize() + 1, icon_index, parent_activity.getSelected_device(), parent_activity.getPower(), 0, 0, 0,0);
                parent_activity.getDb().insert(a);
                parent_activity.getDb().close();
                parent_activity.display_listview_of_Devices();
                makeText(parent_activity.getApplicationContext(), parent_activity.getSelected_device() + " ajouté. ", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            popUp.dismiss();
            int icon_index = parent_activity.return_index_icon(parent_activity.getSelected_device());
            Devices a = new Devices(parent_activity.getDb().getSize() + 1, icon_index, parent_activity.getSelected_device(), parent_activity.getPower(), 0, 0, 0,0);
            parent_activity.getDb().insert(a);
            parent_activity.getDb().close();
            parent_activity.display_listview_of_Devices();
            makeText(parent_activity.getApplicationContext(), parent_activity.getSelected_device() + " ajouté. ", Toast.LENGTH_SHORT).show();
        }
    }

}
