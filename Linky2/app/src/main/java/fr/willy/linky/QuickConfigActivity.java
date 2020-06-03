package fr.willy.linky;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class QuickConfigActivity extends AppCompatActivity {

    /**
     * Le but de cette activité est de pouvoir modifier les informations d'un appareil
     * présent dans la base de données.
     * L'activité permet donc de modifier :
     *      - son nom
     *      - son temps d'utilisation (en heures par jours)
     *      - sa puissance en route
     *      - sa puissance en veille
     *
     * Sa puissance moyenne est mise à jour en fonction des modifications apportées
     *
     * L'activité permet également de supprimer l'appareil si l'utilisateur le désir.
     */

    private DeviceDataBase      db                  = null;
    private DeviceActivity      activity_device     = new DeviceActivity();
    private QuickConfigActivity quickConfigActivity = this;





    /**
     * ####################################################################################################
     * Création de l'activité
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_conifg); //on charge le layout de l'activité

        /**
            Initialisation de l'activité.
            On récupère la ligne séléctionnée dans DeviceActivity (appareil positionné sur une ligne)
         */
        Intent intent = getIntent();

        //recuperation de l'identifiant de l'objet
        if (intent.hasExtra("rowid")){ //vérifie que l'on a bien récupéré une ligne (un appareil)”
            final int rowId = intent.getIntExtra("rowid",0);
            //récupération de la base de données
            db = new DeviceDataBase(this);
            db.open();

            if (rowId != 0) {
                //cast necessaire à l'ustilisation d'une bibliothèque
                String rowId_string = Integer.toString(rowId);
                String[] rowId_array = new String[1];
                rowId_array[0] = rowId_string;

                /**
                 * Récupération de l'appareil sélectionné
                 */
                final Devices device = db.selectWithRowID(new String [] {rowId_string} );

                if (device != null){
                    final EditText instant_power  = findViewById(R.id.instant_power);
                    final EditText stand_by_power = findViewById(R.id.stand_by_power);
                    final EditText use_rate       = findViewById(R.id.device_use_rate);
                    final EditText device_name    = findViewById(R.id.device_name);
                    final TextView mean_power     = findViewById(R.id.mean_power);
                    Button delete_device_button   = findViewById(R.id.delete_device_button);
                    Button cancel_button          = findViewById(R.id.cancel_button);
                    Button confirm_button         = findViewById(R.id.confirm_button);
                    ImageView device_icon         = findViewById(R.id.device_icon);


                    int index = activity_device.return_index_icon(device.getName()); //récupération de l'indice lié à l'icon de l'appareil

                    /**
                     * affichage des informations antérieurs de l'appareil
                     */
                    device.displayIcon(     activity_device, device_icon);
                    device_name.setHint(    device.getName());
                    use_rate.setText(       Float.toString(device.getUseRate()));
                    instant_power.setText(  Integer.toString(device.getPower()));
                    stand_by_power.setText( Float.toString(device.getStandbyPower()));
                    mean_power.setText(     Float.toString(device.getMeanPower()));


                    /**
                        Interraction de l'activité
                        Possibilité de modifier le nom, les puissances de l'appareil ou de le supprimer.
                        On peut également annuler les modifications.
                     */

                    /**
                     * Modification des informations
                     */
                    confirm_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (device_name.getText().toString().equals("")){ //pas de modification du nom si l'utilisateur n'a rien renseigné
                                device.setName(device.getName());
                            }
                            else{
                                device.setName(device_name.getText().toString()); //modification du nom de l'appareil (local)
                            }
                            //modification des informations si besoin (local)
                            device.setUseRate(      Float.parseFloat(use_rate.getText().toString()));
                            device.setPower(        Integer.parseInt(instant_power.getText().toString()));
                            device.setStandbyPower( Float.parseFloat(stand_by_power.getText().toString()));

                            //mise à jour de la puissance moyenne (local)
                            device.setMeanPower();

                            //sauvegarde des modifications dans la base de données
                            db.open();
                            db.update(rowId, device);
                            makeText(getApplicationContext(),"Modifications enregistrées.", Toast.LENGTH_SHORT).show();
                            finish(); //on quitte l'activité pour revenir à DeviceActivity
                        }
                    });


                    /**
                     * Suppression de l'appareil
                     */
                    delete_device_button.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onClick(View v) {
                            final CustomPopUp customPopUpDelete = new CustomPopUp(quickConfigActivity); //on créer le popup de confirmation de suppression
                            customPopUpDelete.test_bluid();                                             //on affiche le pop up


                            //on sauvegarde la demande de suppression dans la base de données
                            customPopUpDelete.getConfirm_delete().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    customPopUpDelete.dismiss();
                                    device.setDelete(1); //demande de suppression (local)
                                    //sauvegarde de la demande de suppression dans la base de données
                                    db.open();
                                    db.update(device.getId(),device);
                                    makeText(getApplicationContext(),"Demande de suppression enregistrée.", Toast.LENGTH_LONG).show();
                                    finish(); //on quitte l'activité pour revenir à DeviceActivity, l'appareil y sera supprimé
                                }
                            });

                            //annulation de la demande de suppression
                            customPopUpDelete.getCancel_delete().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    customPopUpDelete.dismiss();
                                    makeText(getApplicationContext(),"Suppression annulée.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });


                    /**
                     * Annulation des modifications
                     */
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeText(getApplicationContext(),"Modifications non enregistrées.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

                    db.close();
                }
                else{
                    makeText(getApplicationContext(),"Une erreur est survenue", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else{
                makeText(getApplicationContext(),"Une erreur est survenue", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else {
            makeText(getApplicationContext(),"Une erreur est survenue", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
