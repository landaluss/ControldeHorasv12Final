package chema.curso.controldehorasv12final;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PrincipalActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences prefs;
    private TextView nombre;
    private TextView correo;
    private ClipData.Item cerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_registros, R.id.nav_informe , R.id.nav_cuenta , R.id.nav_ayuda)
                .setDrawerLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);


        nombre = (TextView) findViewById(R.id.textViewNombre);
        correo = (TextView) findViewById(R.id.textViewCorreo);

        String name;
        String apellidos;
        String mail;
        String remenber;

        SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        name = prefs.getString("name",
                "");
        apellidos = prefs.getString("apellidos",
                "");
        mail = prefs.getString("email",
                "");

        //Toast.makeText(PrincipalActivity.this, "Remenber= " + remenber, Toast.LENGTH_LONG).show();

        // eliminar preferences
        //prefs.edit().clear().apply();

        nombre.setText(name + " " + apellidos);
        correo.setText(mail);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        boolean rememberPrefs = prefs.getBoolean("remember",
                false);

        switch (item.getItemId()){

            case R.id.action_settings:
                if(!rememberPrefs){
                    prefs.edit().clear().apply();
                    Intent intent = new Intent(PrincipalActivity.this , LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PrincipalActivity.this , LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //Toast.makeText(PrincipalActivity.this, "Remenber= " + remenber, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
