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
    private boolean rememberPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemClock.sleep(3000);

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        rememberPrefs = prefs.getBoolean("remember",
                false);
        if(rememberPrefs){
            Intent intentMain = new Intent(SplashActivity.this,PrincipalActivity.class);
            startActivity(intentMain);
        } else {
            Intent intentLogin = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(intentLogin);
        }
    }
    @Override
    public void onBackPressed(){
        finish();
    }
}
