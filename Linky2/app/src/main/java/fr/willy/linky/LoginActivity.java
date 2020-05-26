package fr.willy.linky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText login_text;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_text = findViewById(R.id.login_text);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntentDevice = new Intent(v.getContext(), MainActivity.class);
                myIntentDevice.putExtra("ip_for_sending",login_text.getText().toString());
                startActivity(myIntentDevice);
                finish();
            }
        });
    }
}
