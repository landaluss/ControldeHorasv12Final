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
    private TextView TvNombre;
    private TextView TvCorreo;
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


        TvNombre = (TextView) findViewById(R.id.textViewNombre);
        TvCorreo = (TextView) findViewById(R.id.textViewCorreo);

        String nombre;
        String apellidos;
        String correo;
        String remenber;

        SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        nombre = prefs.getString("nombre",
                "");
        apellidos = prefs.getString("apellidos",
                "");
        correo = prefs.getString("correo",
                "");

        //Toast.makeText(PrincipalActivity.this, "Remenber= " + remenber, Toast.LENGTH_LONG).show();

        // eliminar preferences
        //prefs.edit().clear().apply();

        TvNombre.setText(nombre + " " + apellidos);
        TvCorreo.setText(correo);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);

        //Toast.makeText(PrincipalActivity.this, "Remenber= " + remenber, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.finishAffinity();
    }

}
