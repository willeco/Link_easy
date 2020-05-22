package fr.willy.linky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;
import static fr.willy.linky.DeviceDataBase.*;

public class Quick_custom extends AppCompatActivity {

    private DeviceDataBase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_custom);

        Intent intent = getIntent();

        //recuperation de l'identifiant de l'objet
        if (intent.hasExtra("rowid")){ // vérifie qu'une valeur est associée à la clé “edittext”
            String[] rowId = intent.getStringArrayExtra("rowId"); // on récupère la valeur associée à la clé
            Log.i("quick_c:", "rowId"+rowId);
            //lecture de la base de données
            db = new DeviceDataBase(this);
            db.open();
            db.displayDevices();
            if (rowId != null) {
                Devices device = db.selectWithRowID(rowId);
                Log.i("device", device.toString());

                TextView tv_power = findViewById(R.id.qc_power);

                tv_power.setText(device.getPower());
                //mise à jour de l'interface
            }

            db.close();
        }
        else {
            makeText(getApplicationContext(),"debug : pas d'extra.", Toast.LENGTH_SHORT).show();
        }


    }


}
