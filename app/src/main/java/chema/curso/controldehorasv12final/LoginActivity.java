package chema.curso.controldehorasv12final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText name;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        final EditText name = (EditText) findViewById(R.id.nombre);
        final EditText pass = (EditText) findViewById(R.id.pass);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correo = name.getText().toString();
                String password = pass.getText().toString();

                Toast.makeText(LoginActivity.this, "Nombre: " + correo + " / Pass: " + password, Toast.LENGTH_LONG).show();
                Toast.makeText(LoginActivity.this, "Autentificando...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this , PrincipalActivity.class);
                startActivity(intent);
            }
        });
    }
}
