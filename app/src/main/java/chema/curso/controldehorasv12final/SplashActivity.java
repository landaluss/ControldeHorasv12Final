package chema.curso.controldehorasv12final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private String name;
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemClock.sleep(3000);

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        name = prefs.getString("name",
                "");

        mail = prefs.getString("email",
                "");

        //Toast.makeText(SplashActivity.this, "Nombre=" + name + " / Email = " + mail, Toast.LENGTH_SHORT).show();

        if(!name.isEmpty() && !mail.isEmpty()){
            Intent intentMain = new Intent(SplashActivity.this,PrincipalActivity.class);
            startActivity(intentMain);
        } else {
            Intent intentLogin = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intentLogin);
        }
    }
}
