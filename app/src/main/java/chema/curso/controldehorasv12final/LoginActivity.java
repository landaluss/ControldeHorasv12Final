package chema.curso.controldehorasv12final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;



import androidx.appcompat.app.AppCompatActivity;
import chema.curso.controldehorasv12final.Clases.SinglentonVolley;

import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.android.volley.AuthFailureError;
        import com.android.volley.NetworkError;
        import com.android.volley.NetworkResponse;
        import com.android.volley.NoConnectionError;
        import com.android.volley.ParseError;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.ServerError;
        import com.android.volley.TimeoutError;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.JsonRequest;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.UnsupportedEncodingException;
        import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Button btnLogin;
    private EditText name;
    private EditText pass;
    private String mail;
    private String imei;
    private Switch switchRemenber;
    private Context mContext;
    private RequestQueue fRequestQueue;
    private SinglentonVolley volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this.getApplicationContext();
        volley = SinglentonVolley.getInstance(this);
        fRequestQueue = volley.getRequestQueue();
        imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        getSupportActionBar().hide();

        name = (EditText) findViewById(R.id.nombre);
        pass = (EditText) findViewById(R.id.pass);
        switchRemenber = (Switch) findViewById(R.id.switchRemenber);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        prefs = getSharedPreferences("Preferences" , Context.MODE_PRIVATE);
        String namePrefs;
        String passPrefs;
        String remenberPrefs;

        namePrefs = prefs.getString("username",
                "");
        passPrefs = prefs.getString("pass",
                "");
        remenberPrefs = prefs.getString("remenber",
                "");

        if(remenberPrefs == "checked"){
            switchRemenber.setChecked(true);
            name.setText(namePrefs);
            pass.setText(passPrefs);
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*String correo = name.getText().toString();
                String password = pass.getText().toString();
                */
                JSONObject post = new JSONObject();
                JSONObject usuario = new JSONObject();
                try {
                    usuario.put("imei", imei);
                    usuario.put("nombre", name.getText().toString());
                    usuario.put("clave", pass.getText().toString());
                    post.put("usuario",usuario);
                    postRequestLogin(post);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //Toast.makeText(MainActivity.this, "Nombre: " + correo + " / Pass: " + password, Toast.LENGTH_LONG).show();
                Toast.makeText(LoginActivity.this, "Autentificando...", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void postRequestLogin(JSONObject data) {
        fRequestQueue = volley.getRequestQueue();
        String url = "https://informehoras.es/verificar-token.php";

        JsonObjectRequest jsonRequestLogin=new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(mContext, response.getString("Msg"), Toast.LENGTH_LONG).show();

                            if (Boolean.valueOf(response.getString("Autenticacion"))){
                                Toast.makeText(mContext, "Hola" + " " +response.getString("nombre") + " " + response.getString("apellidos"), Toast.LENGTH_LONG).show();

                                String nombre = response.getString("nombre");
                                String apellidos = response.getString("apellidos");
                                String username = name.getText().toString();
                                String password = pass.getText().toString();
                                String correo = response.getString("correo");

                                if(switchRemenber.isChecked()){
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("email" , correo);
                                    editor.putString("pass" , password);
                                    editor.putString("username" , username);
                                    editor.putString("name" , nombre);
                                    editor.putString("apellidos" , apellidos);
                                    editor.putString("imei" , imei);
                                    editor.putString("remenber" , "checked");
                                    editor.commit();
                                    editor.apply();
                                } else {
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("email" , correo);
                                    editor.putString("pass" , password);
                                    editor.putString("username" , username);
                                    editor.putString("name" , nombre);
                                    editor.putString("apellidos" , apellidos);
                                    editor.putString("imei" , imei);
                                    editor.putString("remenber" , "nochecked");
                                    editor.commit();
                                    editor.apply();
                                }

                                Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //intent.putExtra("usuario",usuario);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Handle error
                Log.v("RESPUESTAERROR", error.toString());

                if (error instanceof TimeoutError) {
                    //Toast.makeText(mContext,mContext.getString(R.string.error_network_timeout),Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "Timeout error...", Toast.LENGTH_LONG).show();
                }else if(error instanceof NoConnectionError){
                    //Toast.makeText(mContext,mContext.getString(R.string.error_network_timeout),Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "No connection...", Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    try {
                        Toast.makeText(mContext, "Login incorrecto...", Toast.LENGTH_LONG).show();
                        Log.v("RESPUESTAERRORATH", new String(error.networkResponse.data, "UTF-8"));
                        onStart();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //TODO
                } else if (error instanceof ServerError) {
                    //TODO
                    Log.v("RESPUESTAERROR.ServerE", ".");
                } else if (error instanceof NetworkError) {
                    Log.v("RESPUESTAERROR.NetworkE", ".");
                    //TODO
                } else if (error instanceof ParseError) {
                    //TODO
                    Log.v("RESPUESTAERROR", "ParseError");
                }
            }
        });
        fRequestQueue.add(jsonRequestLogin);

    }

    private String getUsernamePrefs(){
        return prefs.getString("username" , "");
    }

    private String getpassPrefs(){
        return prefs.getString("pass" , "");
    }

    /*private void saveOnPrefences(String name , String pass , String mail){
        if(switchRemenber.isChecked()){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email" , mail);
            editor.putString("pass" , pass);
            editor.putString("name" , name);
            editor.commit();
            editor.apply();
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email" , mail);
            editor.putString("name" , name);
            editor.commit();
            editor.apply();
        }
    }*/
}
