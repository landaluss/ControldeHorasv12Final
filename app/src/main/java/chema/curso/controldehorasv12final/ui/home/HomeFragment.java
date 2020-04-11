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
import android.os.Looper;
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
import com.google.android.gms.internal.maps.zzt;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.location.Location;
import android.location.LocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

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
    private double latitud;
    private double longitud;

    private Button entrada;
    private Button salida;

    private Context mContext;
    private RequestQueue fRequestQueue;
    private SinglentonVolley volley;
    private SharedPreferences prefs;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private int ENTRADA  = 0;
    private int SALIDA  = 1;
    //private boolean entradaClick;
    //private boolean salidaClick;
    private ArrayList <Marker> posicionGPS;
    private ArrayList <Marker> registrosGPS;
    Marker puntoGpsInicio;
    private Marker MarkerUltimoRegistro;


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
        mLocationRequest = new LocationRequest();
        posicionGPS = new ArrayList<Marker>();
        registrosGPS = new ArrayList<Marker>();


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
                pulsarBoton(entrada);
            }
        });

        salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsarBoton(salida);
            }
        });
        startLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            JSONObject jsonRegistros = new JSONObject();
            JSONArray registrosHoy = new JSONArray(prefs.getString("registrosHoy",""));
            jsonRegistros.put("registros",registrosHoy);
            mostrarUltimoRegistro(jsonRegistros);
            if (MarkerUltimoRegistro.getTitle().equals("ENTRADA")){
                entrada.setVisibility(View.INVISIBLE);
                salida.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void startLocationUpdates(){
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //check permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if(askforCheckGps()){

            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
        }
        else{
            habilitarBoton(false);
        }


    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onLocationChanged(locationResult.getLastLocation());
        }
    };

    private void stoplocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private boolean askforCheckGps(){
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver() , Settings.Secure.LOCATION_MODE);
            if(gpsSignal == 0){
                return showInfoAlert();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean showInfoAlert(){
        final boolean[] r = {false};
        new AlertDialog.Builder(getContext())
                .setTitle("GPS Signal")
                .setMessage("No tienes activado el GPS. ¿Quieres activarlo ahora?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentt = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intentt);
                        r[0] = true;
                    }
                })
                .setNegativeButton("Ahora no" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        r[0] = false;
                    }
                })
                .show();
        return r[0];

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        latitud = mLastLocation.getLatitude();
        longitud = mLastLocation.getLongitude();

        LatLng currentPosition = new LatLng(latitud , longitud);
        CameraPosition camera = new CameraPosition.Builder()
                .target(currentPosition)
                .zoom(18)
                .bearing(90)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        stoplocationUpdates();

        if (!entrada.isEnabled()) {
            realizarRegistro(ENTRADA);
        }
        else if (!salida.isEnabled()) {
            realizarRegistro(SALIDA);
        }
        else{

            puntoGpsInicio = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitud, longitud))
                    .title("Posición actual")
                    .snippet("")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            posicionGPS.add(puntoGpsInicio);
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(mContext, provider, Toast.LENGTH_LONG).show();
        Log.v("onProviderEnabled", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void realizarRegistro(int entradaSalida){
        String tipo = "";
        if (entradaSalida == 0){
            tipo = "he";
        }
        else if (entradaSalida == 1){
            tipo = "hs";
        }

        try {
            JSONObject post = new JSONObject();
            JSONObject usuario = new JSONObject();
            JSONObject nuevo_registro = new JSONObject();

            usuario.put("imei", prefs.getString("imei",""));
            usuario.put("nombre_login", prefs.getString("nombre_login",""));
            usuario.put("clave", prefs.getString("clave",""));
            post.put("usuario", usuario);

            nuevo_registro.put("latitud", latitud);
            nuevo_registro.put("longitud", longitud);
            nuevo_registro.put("tipo", tipo);
            post.put("nuevo_registro", nuevo_registro);

            postRequestActualizarRegistro(post);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void postRequestActualizarRegistro(JSONObject data) {
        fRequestQueue = volley.getRequestQueue();
        String url = "https://informehoras.es/recibir-token.php";

        JsonObjectRequest jsonRequestActualizarRegistro=new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (Boolean.valueOf(response.getString("Autenticacion")) && Boolean.valueOf(response.getString("actualizado")) ){

                                habilitarBoton(true);

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("registrosHoy", response.getString("registros"));
                                editor.putString("horariosHoy", response.getString("horarios"));
                                //editor.commit(); //sincrono
                                editor.apply();     //asincrono


                                mostrarUltimoRegistro(response);
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
                habilitarBoton(false);
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

    private void mostrarUltimoRegistro(JSONObject response) throws JSONException {
        JSONArray r = response.getJSONArray("registros");
        String fecha = "";
        String hora = "";
        String tipo = "";
        String rLong = "";
        String rLat = "";
        float btTipo = 0;


        JSONObject datos = r.getJSONObject(r.length()-1);
        fecha = datos.getString("fecha");
        hora = datos.getString("hora");
        rLong = datos.getString("long");
        rLat = datos.getString("lat");


        switch (datos.getString("tipo")) {
            case "he":
                tipo = "ENTRADA";
                btTipo =BitmapDescriptorFactory.HUE_GREEN;
                break;
            case "hs":
                tipo = "SALIDA";
                btTipo =BitmapDescriptorFactory.HUE_RED;
                break;
        }

        Log.v("Registros",
                "\n\tfecha: " + fecha +
                        "\n\thora: " + hora +
                        "\n\ttipo: " + tipo +
                        "\n\tlongitud: " + String.valueOf(rLong) +
                        "\n\tlatitud: " + String.valueOf(rLat));

        if(registrosGPS.contains(MarkerUltimoRegistro)){
            registrosGPS.remove(MarkerUltimoRegistro);
            MarkerUltimoRegistro.remove();
        }

        MarkerUltimoRegistro = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(rLat), Double.parseDouble(rLong)))
                .title(tipo)
                .snippet(hora)
                .icon(BitmapDescriptorFactory.defaultMarker(btTipo))
        );

        registrosGPS.add(MarkerUltimoRegistro);

        if(posicionGPS.size()>0){
            posicionGPS.clear();
            puntoGpsInicio.remove();
        }
    }

    private void mostrarRegistro(JSONObject response) throws JSONException {
        //Log.v("Ultimo registro",response.getJSONArray("ultReg")[0].getString("fecha"));
        JSONArray r = response.getJSONArray("registros");
        String fecha = "";
        String hora = "";
        String tipo = "";
        String rLong = "";
        String rLat = "";
        float btTipo = 0;

        for (int i=0;i<r.length();i++){
            JSONObject datos = r.getJSONObject(i);
            fecha = datos.getString("fecha");
            hora = datos.getString("hora");
            rLong = datos.getString("long");
            rLat = datos.getString("lat");


            switch (datos.getString("tipo")) {
                case "he":
                    tipo = "ENTRADA";
                    btTipo =BitmapDescriptorFactory.HUE_GREEN;
                    break;
                case "hs":
                    tipo = "SALIDA";
                    btTipo =BitmapDescriptorFactory.HUE_RED;
                    break;
            }

            Log.v("Registros",
                  "\n\tfecha: " + fecha +
                       "\n\thora: " + hora +
                       "\n\ttipo: " + tipo +
                       "\n\tlongitud: " + String.valueOf(rLong) +
                       "\n\tlatitud: " + String.valueOf(rLat));



            MarkerUltimoRegistro = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(rLat), Double.parseDouble(rLong)))
                    .title(tipo)
                    .snippet(hora)
                    .icon(BitmapDescriptorFactory.defaultMarker(btTipo))
            );
        }
        if(posicionGPS.size()>0){
            posicionGPS.clear();
            puntoGpsInicio.remove();
        }
    }

    private void pulsarBoton(Button bt) {
        bt.setEnabled(false);
        bt.setTextColor(getActivity().getResources().getColor(R.color.common_google_signin_btn_text_dark_disabled));
        startLocationUpdates();
    }
    private void habilitarBoton(boolean exito) {
        if (exito) {
            if (!entrada.isEnabled()) {
                entrada.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));
                entrada.setVisibility(View.INVISIBLE);
                salida.setVisibility(View.VISIBLE);
                entrada.setEnabled(true);
            } else if (!salida.isEnabled()) {
                salida.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));
                salida.setVisibility(View.INVISIBLE);
                entrada.setVisibility(View.VISIBLE);
                salida.setEnabled(true);
            }
        }
        else{
            if (!entrada.isEnabled()) {
                entrada.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));
                entrada.setEnabled(true);
            }
            else if (!salida.isEnabled()) {
                salida.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));
                salida.setEnabled(true);
            }
        }
    }
}
