package chema.curso.controldehorasv12final.ui.consultas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import chema.curso.controldehorasv12final.LoginActivity;
import chema.curso.controldehorasv12final.PrincipalActivity;
import chema.curso.controldehorasv12final.R;

public class ConsultasFragment extends Fragment {

    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_consultas, container, false);
        return rootView;
    }
}

/////////////Consultar Registro
    /*
    public void onClick(View v) {

        JSONObject post = new JSONObject();
        JSONObject usuario = new JSONObject();
        JSONObject consulta_registro = new JSONObject();
        try {
            usuario.put("imei", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            usuario.put("nombre", name.getText().toString());
            usuario.put("clave", pass.getText().toString());
            post.put("usuario", usuario);

            consulta_registro.put("desde", "2020-03-23");
            consulta_registro.put("hasta", "2020-03-27");
            post.put("consulta_registro", consulta_registro);

            postRequestConsultaRegistro(post);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void postRequestConsultaRegistro(JSONObject data) {
        fRequestQueue = volley.getRequestQueue();
        String url = "https://informehoras.es/consultaRegistros.php";

        JsonObjectRequest jsonRequestConsultaRegistro=new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(mContext, response.getString("Msg"), Toast.LENGTH_LONG).show();
                            if (Boolean.valueOf(response.getString("Autenticacion"))){
                                mostrarRegistro(response);
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
                    Log.v("RESPUESTAERROR", "ParseError" );
                }
            }
        });
        fRequestQueue.add(jsonRequestConsultaRegistro);
    }

    private void mostrarRegistro(JSONObject response) throws JSONException {
        //Log.v("Ultimo registro",response.getJSONArray("ultReg")[0].getString("fecha"));
        JSONArray r = response.getJSONArray("registros");
        for (int i=0;i<r.length();i++){
            JSONObject datos = r.getJSONObject(i);
            Log.v("Registros","\n\tfecha: " + datos.getString("fecha") + "\n\thora: " + datos.getString("hora") + "\n\ttipo: " + datos.getString("tipo"));
        }
    }
*/
