package chema.curso.controldehorasv12final;

import androidx.appcompat.app.AppCompatActivity;
import chema.curso.controldehorasv12final.Clases.SinglentonVolley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private boolean rememberPrefs;
    private Timer timer;
    private ProgressBar progressBar;
    private int i = 0;
    private Context mContext;
    private RequestQueue fRequestQueue;
    private SinglentonVolley volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this.getApplicationContext();
        volley = SinglentonVolley.getInstance(this);
        fRequestQueue = volley.getRequestQueue();
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        rememberPrefs = prefs.getBoolean("remember",
                false);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        final long intervalo = 15;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (i < 100){
                    progressBar.setProgress(i);
                    i++;
                }else{
                    timer.cancel();

                    if(rememberPrefs){
                        JSONObject post = new JSONObject();
                        JSONObject usuario = new JSONObject();
                        try {
                            usuario.put("imei", prefs.getString("imei",""));
                            usuario.put("nombre_login", prefs.getString("nombre_login",""));
                            usuario.put("clave", prefs.getString("clave",""));
                            post.put("usuario",usuario);
                            postRequestLogin(post);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Toast.makeText(mContext, "Autentificando...", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intentLogin = new Intent(mContext,LoginActivity.class);
                        startActivity(intentLogin);
                    }
                }
            }
        },0,intervalo);

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

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("registrosHoy", response.getString("registros"));
                                editor.putString("horariosHoy", response.getString("horarios"));
                                //editor.commit(); //sincrono
                                editor.apply();     //asincrono

                                Intent intent = new Intent(SplashActivity.this, PrincipalActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

                Intent intentLogin = new Intent(mContext,LoginActivity.class);
                startActivity(intentLogin);

                if (error instanceof TimeoutError) {
                    //Toast.makeText(mContext,mContext.getString(R.string.error_network_timeout),Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "Timeout error...", Toast.LENGTH_LONG).show();
                }else if(error instanceof NoConnectionError){
                    //Toast.makeText(mContext,mContext.getString(R.string.error_network_timeout),Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "No connection...", Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    Log.v("RESPUESTAERROR.AuthFail", ".");
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

    @Override
    public void onBackPressed(){
        finish();
    }
}
