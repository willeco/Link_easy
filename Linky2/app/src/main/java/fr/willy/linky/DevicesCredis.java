package fr.willy.linky;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class DevicesCredis extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);

        TextView willy = findViewById(R.id.willy);
        TextView kauche = findViewById(R.id.kauche);
        TextView damien = findViewById(R.id.damien);
        TextView david = findViewById(R.id.david);

        kauche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        damien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        david.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        willy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeText(getApplicationContext(),R.string.lolo, Toast.LENGTH_LONG).show();
            }
        });

    }
}
