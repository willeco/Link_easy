package fr.willy.linky;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    /**
     * Le but de cette classe est de générer une fenêtre pop up permettant à l'utilisateur d'être guidé.
     * Différents constructeurs sont utilisés en fonction de l'activité dans laquelle ils sont appelés.
     *
     * Les pop ups de cette classe se distinguent en deux catégories :
     *      - les pop ups informatifs (definition, tutoriel)
     *      - les pop ups protocolaires (protocoles de création d'un appareil)
     *
     * Chaque catégories de popup possède un layout spécifique éditable.
     */

    //fields
    private TextView            confirm_text,
                                cancel_text,
                                dont_show_again,
                                power_name,
                                power_definition;

    private Button              understand,
                                next,
                                tuto_understand,
                                confirm_delete,
                                cancel_delete,
                                quit_infomration;

    private Spinner             device_spinner;

    private LoginActivity       loginActivity;
    private HubActivity         hubActivity;
    private DeviceActivity      deviceActivity;
    private QuickConfigActivity quickConfigActivity;

    private CustomPopUp         popUp = this;




    /**
     * 4 constructeur différents pour 4 classes différentes
     */
    //constructor DeviceActivity
    public CustomPopUp(DeviceActivity deviceActivity, String popup) {
        super(deviceActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        //on charge notre layout associé au popup
        if      (popup.equals(  "adding")) {        setContentView(R.layout.popup_device_adding); }

        else if (popup.equals(  "tuto add")){       setContentView(R.layout.popup_device_tuto_add); }

        else if (popup.equals(  "tuto config")){    setContentView(R.layout.popup_device_tuto_config); }

        else {                                      setContentView(R.layout.popup_device_configuration); }

        this.setCancelable(false); //empeche l'utilisateur de fermer le popup en appuyant à l'exterieur de celui-ci.
        this.confirm_text       = findViewById(R.id.confirm_text);
        this.cancel_text        = findViewById(R.id.cancel_text);
        this.device_spinner     = findViewById(R.id.device_spinner);
        this.tuto_understand    = findViewById(R.id.tuto_understand);
        this.next               = findViewById(R.id.next);
        this.dont_show_again    = findViewById(R.id.dont_show_again);
        this.deviceActivity     = deviceActivity;
    }



    //constructor LoginActivity
    public CustomPopUp(LoginActivity loginActivity) {
        super(loginActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        setContentView(R.layout.popup_start_application);
        this.setCancelable(false);
        this.understand     = findViewById(R.id.understand);
        this.loginActivity  = loginActivity;
    }



    //constructor QuickConfigActivity
    public CustomPopUp(QuickConfigActivity quickConfigActivity) {
        super(quickConfigActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        setContentView(R.layout.popup_delete);
        this.setCancelable(false);
        this.confirm_delete         = findViewById(R.id.confirm_delete);
        this.cancel_delete          = findViewById(R.id.cancel_delete);
        this.quickConfigActivity    = quickConfigActivity;
    }



    //constructor HubActivity
    public CustomPopUp(HubActivity hubActivity, String power) {
        super(hubActivity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        setContentView(R.layout.popup_information_power);
        this.setCancelable(false);
        this.power_name         = findViewById(R.id.power_name);
        this.power_definition   = findViewById(R.id.power_definition);
        this.quit_infomration   = findViewById(R.id.quit_information);
        this.hubActivity        = hubActivity;
    }





    /**
     * Getter
     */
    public Button   getQuit_information(){  return this.quit_infomration;}
    public Button   getConfirm_delete(){    return this.confirm_delete;}
    public Button   getCancel_delete(){     return this.cancel_delete;}
    public Button   getUnderstand(){        return this.understand;}
    public Button   getNext(){              return this.next;}
    public Button   getTuto_understand(){   return this.tuto_understand;}

    public TextView getDont_show_again(){   return this.dont_show_again;}
    public TextView getPower_name(){        return this.power_name;}
    public TextView getPower_definition(){  return this.power_definition;}
    public TextView getConfirm_text(){      return confirm_text;}
    public TextView getCancel_text(){       return cancel_text;}


    public Spinner  getDevice_spinner(){    return this.device_spinner;}
    //recupere l'appareil selectionné dans le spinner (menu déroulant)
    public String   getSpinnerData(){       return this.device_spinner.getSelectedItem().toString();}





    //affiche le popup
    public void test_bluid(){ show();}




    /**
     * Pop up protocolaire
     */
    @SuppressLint("LongLogTag")
    public void configuration_protocol(String device, final String ip_for_sending) {

        TextView deviceTextView             = findViewById(R.id.textView);
        final TextView protocolTextView     = findViewById(R.id.textView3);
        final TextView tauxUtilisation      = findViewById(R.id.textView18);
        final EditText tauxUtilisationEdit  = findViewById(R.id.editTextTextPersonName);
        final Button faitButton             = findViewById(R.id.button);
        final Button ajouterButton          = findViewById(R.id.button2);
        final CheckBox debranchable         = findViewById(R.id.checkBox);

        //instanciation des puissances
        final int[]     pappUnplugged           = new int[1];
        final int[]     pappOn                  = new int[1];
        final int[]     pappOff                 = new int[1];
        final int[]     diffPappOnOff           = new int[1];
        final int[]     diffPappUnpluggedOff    = new int[1];
        final int[]     counter                 = {6};
        final String[]  tauxUtilisationDouble   = new String[1];

        //visibilité de l'affichage
        ajouterButton.setVisibility(        GONE);
        faitButton.setVisibility(           GONE);
        debranchable.setVisibility( View.VISIBLE);

        //affectation des textes
        faitButton.setText("OK");
        deviceTextView.setText(device);
        protocolTextView.setText(R.string.protocol_start);


        /**
         * Ajout d'un temps d'utilisation à l'appareil
         */
        tauxUtilisationEdit.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){ faitButton.setVisibility(GONE);}

            public void afterTextChanged(Editable s){
                if(!(tauxUtilisationEdit.getText().toString().matches(""))){ //si on a bien renseigné un taux d'utilisation
                    tauxUtilisationDouble[0] = tauxUtilisationEdit.getText().toString(); //on récupere le taux d'utilisation
                    faitButton.setVisibility(View.VISIBLE); //on autorise l'utilisateur à poursuivre le protocole
                }
            }
        });



        /**
         * Lancement du protocole
         */
        faitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //visibilité de l'affichage
                tauxUtilisation.setVisibility(      GONE);
                tauxUtilisationEdit.setVisibility(  GONE);
                faitButton.setVisibility(           GONE);
                debranchable.setVisibility(         GONE);

                /**
                 * On lance la télé-info, pour être sur de bien analyser la consommation de l'appareil
                 */
                HubActivity.ask_tele_info(ip_for_sending, 10001);


                /**
                 * Étape supplémentaire si l'appareil est débranchable
                 */
                if (debranchable.isChecked() == true){
                    //débrancher l'appareil
                    new CountDownTimer(5000, 1000){ //on laisse le temps à l'utilisateur de rdébrancher son appareil
                        public void onTick(long millisUntilFinished){
                            protocolTextView.setText("Veuillez débrancher votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                            counter[0]--;
                            protocolTextView.setText("Veuillez débrancher votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                        }
                        public void onFinish(){
                            protocolTextView.setText("Veuillez débrancher votre appareil.\n0 secondes");
                            faitButton.setVisibility(View.VISIBLE);
                        }
                    }.start();


                    /**
                     * rebrancher et allumer l'appareil
                     */
                    faitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pappUnplugged[0] = Integer.parseInt(HubActivity.papp);
                            protocolTextView.setText("Veuillez rebrancher et allumer votre appareil.\n" + String.valueOf(counter[0]) + " secondes");
                            counter[0] = 6;
                            faitButton.setVisibility(GONE);

                            new CountDownTimer(5000, 1000){
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



                            /**
                             * Éteindre l'appareil
                             */
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


                                    /**
                                     * Protocole terminé, on passe à la calcul des puissances
                                     */
                                    faitButton.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v){
                                            pappOff[0] = Integer.parseInt(HubActivity.papp);
                                            faitButton.setText("OK");
                                            faitButton.setVisibility(GONE);
                                            diffPappOnOff[0] = pappOn[0] - pappOff[0];
                                            diffPappUnpluggedOff[0] = pappOff[0] - pappUnplugged[0];
                                            deviceActivity.setPower(diffPappOnOff[0]);
                                            deviceActivity.setstandbypower(diffPappUnpluggedOff[0]);
                                            int mean_power = Math.round((deviceActivity.getPower() * Float.parseFloat(tauxUtilisationDouble[0]) + deviceActivity.getstandbypower() * (24 - Float.parseFloat(tauxUtilisationDouble[0]))) / 24);
                                            protocolTextView.setText("La configuration est terminée.");
                                            ajouterButton.setVisibility(View.VISIBLE);

                                            // Insertion d'un appareil
                                            int icon_index = deviceActivity.return_index_icon(deviceActivity.getSelected_device());
                                            final Devices a = new Devices(deviceActivity.getDb().getSize() + 1, icon_index, deviceActivity.getSelected_device(), deviceActivity.getPower(), deviceActivity.getstandbypower(), mean_power , Float.parseFloat(tauxUtilisationDouble[0]),0);
                                            a.setMeanPower();
                                            /**
                                             * Permet d'ajouter l'appareil
                                             */
                                            ajouterButton.setOnClickListener(new View.OnClickListener(){
                                                @Override
                                                public void onClick(View v) {
                                                    popUp.dismiss();
                                                    deviceActivity.getDb().insert(a);
                                                    deviceActivity.getDb().close();
                                                    deviceActivity.display_listview_of_Devices();
                                                    makeText(deviceActivity.getApplicationContext(), deviceActivity.getSelected_device() + " ajouté. ", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                /**
                 * Protocole différent si l'appareil n'est pas débranchable
                 */
                else {
                    protocolTextView.setText("Veuillez allumer votre appareil.\n");
                    counter[0] = 6;
                    faitButton.setVisibility(GONE);

                    /**
                     * Allumer l'appareil
                     */
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


                    /**
                     * Éteindre l'appareil
                     */
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


                            /**
                             * Protocole terminé, on passe au calcul des puissances
                             */
                            faitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pappOff[0] = Integer.parseInt(HubActivity.papp);
                                    faitButton.setText("OK");
                                    faitButton.setVisibility(GONE);
                                    diffPappOnOff[0] = pappOn[0] - pappOff[0];
                                    deviceActivity.setPower(diffPappOnOff[0]);
                                    protocolTextView.setText("La configuration est terminé.");
                                    ajouterButton.setVisibility(View.VISIBLE);

                                    // Insertion d'un appareil
                                    int icon_index = deviceActivity.return_index_icon(deviceActivity.getSelected_device());
                                    final Devices a = new Devices(deviceActivity.getDb().getSize() + 1, icon_index, deviceActivity.getSelected_device(), deviceActivity.getPower(), 0, (pappOn[0]*Float.parseFloat(tauxUtilisationDouble[0])+pappOff[0]*(24-Float.parseFloat(tauxUtilisationDouble[0])))/24, Float.parseFloat(tauxUtilisationDouble[0]),0);
                                    a.setMeanPower();
                                    /**
                                     * Permet d'ajouter l'appareil
                                     */
                                    ajouterButton.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {
                                            popUp.dismiss();
                                            deviceActivity.getDb().insert(a);
                                            deviceActivity.getDb().close();
                                            deviceActivity.display_listview_of_Devices();
                                            makeText(deviceActivity.getApplicationContext(), deviceActivity.getSelected_device() + " ajouté. ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
