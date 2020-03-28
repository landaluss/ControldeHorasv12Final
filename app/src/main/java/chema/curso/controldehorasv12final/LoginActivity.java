package chema.curso.controldehorasv12final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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



    private Button btnLogin;
    private EditText name;
    private EditText pass;
    private Context mContext;
    private RequestQueue fRequestQueue;
    private SinglentonVolley volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this.getApplicationContext();
        volley = SinglentonVolley.getInstance(this);
        fRequestQueue = volley.getRequestQueue();

        getSupportActionBar().hide();

        final EditText name = (EditText) findViewById(R.id.nombre);
        final EditText pass = (EditText) findViewById(R.id.pass);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String correo = name.getText().toString();
                String password = pass.getText().toString();
                */
                JSONObject post = new JSONObject();
                JSONObject usuario = new JSONObject();
                try {
                    usuario.put("imei", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
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
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
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



}
