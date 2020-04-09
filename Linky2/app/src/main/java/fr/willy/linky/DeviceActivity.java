package fr.willy.linky;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceActivity extends AppCompatActivity {

    /*
    L'idee c'est de pouvoir ajouter un appareil à la liste, pour pouvoir voir sa consommation energetique.
    Pour rendre l'application plus belle et ergonomique, pourquoi pas faire une banque d'images des potentiels
    appareils électriques d'une maison.
    Il faut donc trouver un moyen de proposer dans une liste déroulante diffrents appareils.
     */

    private ArrayList<String> device_list ;
    private Button button_add_device;
    private DeviceActivity device;
    private DeviceDataBase db = new DeviceDataBase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        db.open();
        db.removeAll();

        Devices a = new Devices(155,"Four", "666");

        db.insert(a);
        db.displayDevices();
        db.close();



        device = this;
        button_add_device = findViewById(R.id.button_add_device);

        button_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CustomPopUp customPopUp = new CustomPopUp(device);
                customPopUp.getConfirm_text().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Appareil ajouté à la liste", Toast.LENGTH_SHORT).show();
                        customPopUp.dismiss();
                    }
                });

                customPopUp.getCancel_text().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Ajout annulé", Toast.LENGTH_SHORT).show();
                        customPopUp.dismiss();
                    }
                });

                customPopUp.test_bluid();
            }
        });
    }
}
