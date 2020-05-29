package fr.willy.linky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class LoginActivity extends AppCompatActivity {

    EditText login_text;
    Button login;
    Button activity;
    Button understand;
    LoginActivity loginActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final CustomPopUp customPopUStartApplication = new CustomPopUp(loginActivity); //on créer le popup d'ajout

        customPopUStartApplication.test_bluid(); //on affiche le popup

        login_text = findViewById(R.id.login_text);
        login      = findViewById(R.id.login);
        activity   = findViewById(R.id.activity);

        customPopUStartApplication.getUnderstand().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopUStartApplication.dismiss();//on ferme le popup
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = login_text.getText().toString();
                if (ip.equals("")){
                    makeText(getApplicationContext(),"Veuillez saisir une adresse ip.", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent myIntentDevice = new Intent(v.getContext(), HubActivity.class);
                    myIntentDevice.putExtra("ip_for_sending",ip);
                    startActivity(myIntentDevice);
                    finish();
                }
            }
        });

        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntentDevice = new Intent(v.getContext(), DevicesCredis.class);
                startActivity(myIntentDevice);
                finish();
            }
        });
    }
}
