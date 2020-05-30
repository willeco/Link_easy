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

    private DeviceDataBase db = null;
    private DeviceActivity activity_device = new DeviceActivity();
    private QuickConfigActivity quickConfigActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_conifg);

        /**
            Initialisation de l'activité.
            On récupere le bon appareil et les informations correspondantes.
         */
        Intent intent = getIntent();

        //recuperation de l'identifiant de l'objet
        if (intent.hasExtra("rowid")){ // vérifie qu'une valeur est associée à la clé “edittext”
            final int rowId = intent.getIntExtra("rowid",0);
            //lecture de la base de données
            db = new DeviceDataBase(this);
            db.open();
            db.displayDevices();

            if (rowId != 0) {

                String rowId_string = Integer.toString(rowId);

                String[] rowId_array = new String[1];
                rowId_array[0] = rowId_string;

                //final Devices device = db.selectWithRowID(rowId_array);
                final Devices device = db.selectWithRowID(new String [] {rowId_string} );
                //makeText(getApplicationContext(),device.getName(), Toast.LENGTH_SHORT).show();

                if (device != null){

                    final EditText instant_power  = findViewById(R.id.instant_power);
                    final EditText stand_by_power = findViewById(R.id.stand_by_power);
                    final EditText mean_power     = findViewById(R.id.mean_power);
                    ImageView device_icon         = findViewById(R.id.device_icon);
                    final EditText device_name    = findViewById(R.id.device_name);
                    Button delete_device_button   = findViewById(R.id.delete_device_button);
                    Button cancel_button          = findViewById(R.id.cancel_button);
                    Button confirm_button         = findViewById(R.id.confirm_button);


                    instant_power.setText(Integer.toString(device.getPower()));
                    stand_by_power.setText(Float.toString(device.getStandbyPower()));
                    mean_power.setText(Float.toString(device.getMeanPower()));

                    device_name.setHint(device.getName());
                    int index = activity_device.return_index_icon(device.getName());
                    device.setIcon(index);
                    device.displayIcon(activity_device, device_icon);

                    /**
                        Interraction de l'activité
                        Possibilité de modifier le nom, la puissance de l'appareil ou de le supprimer.
                        On peut également annuler les modifications.
                     */

                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeText(getApplicationContext(),"Modifications non enregistrées.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    /**
                     /!\  Modification de l'appareil  /!\
                     */

                    confirm_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (device_name.getText().toString().equals("")){
                                device.setName(device.getName());
                            }
                            else{
                                device.setName(device_name.getText().toString());
                            }
                            device.setPower(Integer.parseInt(instant_power.getText().toString()));
                            device.setStandbyPower(Float.parseFloat(stand_by_power.getText().toString()));
                            device.setMeanPower(Float.parseFloat(mean_power.getText().toString()));
                            db.open();
                            db.update(rowId, device);
                            db.close();
                            makeText(getApplicationContext(),"Modifications enregistrées.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    db.close();


                    delete_device_button.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onClick(View v) {

                            final CustomPopUp customPopUpDelete = new CustomPopUp(quickConfigActivity); //on créer le popup d'ajout

                            customPopUpDelete.test_bluid(); //on affiche le popup

                            customPopUpDelete.getCancel_delete().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    customPopUpDelete.dismiss();
                                    makeText(getApplicationContext(),"Suppression annulée.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            customPopUpDelete.getConfirm_delete().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    customPopUpDelete.dismiss();
                                    makeText(getApplicationContext(),"Demande de suppression enregistrée.", Toast.LENGTH_SHORT).show();
                                    /*
                                    device.setDelete();
                                    Log.i("####################", "#######################");
                                    Log.i("DELETE MESSAGE BEFORE de", device.getName() +" est "+device.getDelete());
                                    finish();
                                     */
                                }
                            });
                        }
                    });


                }
                else{
                    Log.i("device", "On a pas recupéré l'appareil.");
                    makeText(getApplicationContext(),"On a pas recupéré l'appareil.", Toast.LENGTH_SHORT).show();
                }

            }
            else {
                //debugage
                makeText(getApplicationContext(),"il n'a même pas essayé de changer l'icon", Toast.LENGTH_SHORT).show();
                db.close();
            }


        }
        else {
            //debugage
            makeText(getApplicationContext(),"debug : pas d'extra.", Toast.LENGTH_SHORT).show();
        }


    }


}
