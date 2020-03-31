package chema.curso.controldehorasv12final.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import chema.curso.controldehorasv12final.Clases.SinglentonVolley;
import chema.curso.controldehorasv12final.LoginActivity;
import chema.curso.controldehorasv12final.PrincipalActivity;
import chema.curso.controldehorasv12final.R;

import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment implements OnMapReadyCallback , LocationListener {

    private View rootView;
    private GoogleMap mMap;
    private MapView mapView;

    private LocationManager locManager;
    private double latitude;
    private double longitud;

    private Button entrada;
    private Button salida;

    private Context mContext;
    private RequestQueue fRequestQueue;
    private SinglentonVolley volley;
    private SharedPreferences prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = this.getContext();
        volley = SinglentonVolley.getInstance(mContext);
        fRequestQueue = volley.getRequestQueue();
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        mapView = (MapView) rootView.findViewById(R.id.map);
        if(mapView!= null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        entrada = (Button) rootView.findViewById(R.id.entrada);
        salida = (Button) rootView.findViewById(R.id.salida);


        entrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject post = new JSONObject();
                    JSONObject usuario = new JSONObject();
                    JSONObject nuevo_registro = new JSONObject();

                    usuario.put("imei", prefs.getString("imei",""));
                    usuario.put("nombre", prefs.getString("username",""));
                    usuario.put("clave", prefs.getString("pass",""));
                    post.put("usuario", usuario);

                    nuevo_registro.put("latitud", "-3.333");
                    nuevo_registro.put("longitud", "40.00");
                    nuevo_registro.put("tipo", "he");
                    post.put("nuevo_registro", nuevo_registro);

                    postRequestActualizarRegistro(post);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                entrada.setVisibility(View.INVISIBLE);
                salida.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Me voy, sale el de salida", Toast.LENGTH_SHORT).show();
            }
        });

        salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject post = new JSONObject();
                    JSONObject usuario = new JSONObject();
                    JSONObject nuevo_registro = new JSONObject();

                    usuario.put("imei", prefs.getString("imei",""));
                    usuario.put("nombre", prefs.getString("username",""));
                    usuario.put("clave", prefs.getString("pass",""));
                    post.put("usuario", usuario);

                    nuevo_registro.put("latitud", "-3.333");
                    nuevo_registro.put("longitud", "40.00");
                    nuevo_registro.put("tipo", "hs");
                    post.put("nuevo_registro", nuevo_registro);

                    postRequestActualizarRegistro(post);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                salida.setVisibility(View.INVISIBLE);
                entrada.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Me voy, sale el de entrada", Toast.LENGTH_SHORT).show();
            }
        });

        askforCheckGps();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            locManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 1000 , 10 , this);
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 1000 , 10 , this);
        }

    }


    private void askforCheckGps(){
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver() , Settings.Secure.LOCATION_MODE);
            if(gpsSignal == 0){
                showInfoAlert();
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showInfoAlert(){
        new AlertDialog.Builder(getContext())
                .setTitle("GPS Signal")
                .setMessage("No tienes activado el GPS. ¿Quieres activarlo ahora?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentt = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intentt);
                    }
                })
                .setNegativeButton("Ahora no" , null)
                .show();

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitud = location.getLongitude();

        LatLng currentPosition = new LatLng(latitude , longitud);
        CameraPosition camera = new CameraPosition.Builder()
                .target(currentPosition)
                .zoom(18)
                .bearing(90)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

        //Toast.makeText(getContext(), "Latitud" + latitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void postRequestActualizarRegistro(JSONObject data) {
        fRequestQueue = volley.getRequestQueue();
        String url = "https://informehoras.es/recibir-token.php";

        JsonObjectRequest jsonRequestActualizarRegistro=new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(mContext, response.getString("Msg"), Toast.LENGTH_LONG).show();
                            if (Boolean.valueOf(response.getString("Autenticacion")) && Boolean.valueOf(response.getString("actualizado")) ){
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
        fRequestQueue.add(jsonRequestActualizarRegistro);
    }

    private void mostrarRegistro(JSONObject response) throws JSONException {
        //Log.v("Ultimo registro",response.getJSONArray("ultReg")[0].getString("fecha"));
        JSONArray r = response.getJSONArray("registros");
        for (int i=0;i<r.length();i++){
            JSONObject datos = r.getJSONObject(i);
            Log.v("Registros","\n\tfecha: " + datos.getString("fecha") + "\n\thora: " + datos.getString("hora") + "\n\ttipo: " + datos.getString("tipo"));
        }
    }
}
