package chema.curso.controldehorasv12final.ui.consultas;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import chema.curso.controldehorasv12final.Clases.SinglentonVolley;
import chema.curso.controldehorasv12final.R;

public class ConsultasFragment extends Fragment implements OnMapReadyCallback {

    private View rootView;
    private GoogleMap mMap;
    private MapView mapView;

    private Context mContext;
    private RequestQueue fRequestQueue;
    private SinglentonVolley volley;
    private SharedPreferences prefs;
    private ArrayList<Marker> registrosGPS;
    private Marker MarkerRegistro;

    private Calendar calendario = Calendar.getInstance();
    private EditText FechaInicio;
    private EditText FechaFin;
    private int day, month, year;

    private ImageButton showOpstionsFilter;
    private ImageButton hideOpstionsFilter;
    private Button btnFiltrar;
    private LinearLayout llView;
    private int shortAnimationDuration;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_consultas, container, false);
        return rootView;

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = this.getContext();
        volley = SinglentonVolley.getInstance(mContext);
        fRequestQueue = volley.getRequestQueue();
        prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        registrosGPS = new ArrayList<Marker>();


        mapView = (MapView) rootView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        day = calendario.get(Calendar.DAY_OF_MONTH);
        year = calendario.get(Calendar.YEAR);
        month = calendario.get(Calendar.MONTH);

        //accion para mostrar las opciones de filtrado
        showOpstionsFilter = (ImageButton) getView().findViewById(R.id.showOptionsFilter);
        hideOpstionsFilter = (ImageButton) getView().findViewById(R.id.hideOptionsFilter);
        llView = (LinearLayout) getView().findViewById(R.id.llfiltros);

        //boton accion filtrar
        btnFiltrar = (Button) getView().findViewById(R.id.btnFiltrar);

        //boton mostrar
        showOpstionsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (llView.getVisibility() == View.GONE)
                {
                    animar(true);
                    llView.setVisibility(View.VISIBLE);
                    showOpstionsFilter.setImageResource(R.drawable.ic_hidefilters);
                    //Toast.makeText(mContext, "escondido", Toast.LENGTH_SHORT).show();
                } else {
                    animar(false);
                    llView.setVisibility(View.GONE);
                    showOpstionsFilter.setImageResource(R.drawable.ic_showfilters);
                    //Toast.makeText(mContext, "mostrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*hideOpstionsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animar(false);
                llView.setVisibility(View.GONE);
                showOpstionsFilter.setVisibility(View.VISIBLE);
                hideOpstionsFilter.setVisibility(View.INVISIBLE);
            }
        });*/

        FechaInicio = (EditText) getView().findViewById(R.id.FechaInicio);
        FechaFin = (EditText) getView().findViewById(R.id.FechaFin);

        //mostrar calendario y colocar fecha FechaInicio
        FechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog();
            }

            public void DateDialog() {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int mes = monthOfYear + 1;
                        FechaInicio.setText(dayOfMonth + "/" + mes + "/" + year);
                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(getActivity(), listener, year, month, day);
                dpDialog.show();
            }
        });

        //mostrar calendario y colocar fecha FechaFin
        FechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog();
            }

            public void DateDialog() {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int mes = monthOfYear + 1;
                        FechaFin.setText(dayOfMonth + "/" + mes + "/" + year);
                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(getActivity(), listener, year, month, day);
                dpDialog.show();
            }
        });

        btnFiltrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String fInicio = FechaInicio.getText().toString();
                String fFin = FechaFin.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


                try {


                        Date dtInicio = (Date) sdf.parse(fInicio);
                        Date dtFin = sdf.parse(fFin);

                    if (dtInicio.getTime() <= dtFin.getTime()) {

                        hideOpstionsFilter.callOnClick();
                        String[] fDesde = fInicio.split("/");
                        String[] fHasta = fFin.split("/");

                        JSONObject post = new JSONObject();
                        JSONObject usuario = new JSONObject();
                        JSONObject consulta_registro = new JSONObject();

                        usuario.put("imei", prefs.getString("imei", ""));
                        usuario.put("nombre_login", prefs.getString("nombre_login", ""));
                        usuario.put("clave", prefs.getString("clave", ""));
                        post.put("usuario", usuario);

                        consulta_registro.put("desde", fDesde[2] + "-" + fDesde[1] + "-" + fDesde[0]);
                        consulta_registro.put("hasta", fHasta[2] + "-" + fHasta[1] + "-" + fHasta[0]);
                        post.put("consulta_registro", consulta_registro);

                        postRequestConsultaRegistro(post);
                    }
                    else{
                        //Error rango fechas
                        Toast.makeText(mContext, "El rango de fechas no aporta ningún resultado.", Toast.LENGTH_LONG).show();


                    }

                } catch (JSONException | ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });


        //Toast.makeText(mContext, "Año" + ano, Toast.LENGTH_SHORT).show();

    }


    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            JSONObject jsonRegistros = new JSONObject();
            //JSONArray registrosHoy = new JSONArray();
            jsonRegistros.put("registros", prefs.getString("registrosHoy", ""));
            mostrarRegistro(jsonRegistros);
            moverCamara(registrosGPS.get(registrosGPS.size() - 1));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void animar(boolean mostrar) {
        AnimationSet set = new AnimationSet(true);
        Animation animation = null;
        if (mostrar) {
            //desde la esquina inferior derecha a la superior izquierda
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        } else {    //desde la esquina superior izquierda a la esquina inferior derecha
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        }
        //duración en milisegundos
        animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);

        llView.setLayoutAnimation(controller);
        llView.startAnimation(animation);
    }

    private void mostrarRegistro(JSONObject response) throws JSONException {
        //Log.v("Ultimo registro",response.getJSONArray("ultReg")[0].getString("fecha"));
        String fecha = "";
        String hora = "";
        String tipo = "";
        String rLong = "";
        String rLat = "";
        float btTipo = 0;

        if (response.getJSONArray("registros").length()>0){
            JSONArray r = response.getJSONArray("registros");

            for (int i = 0; i < r.length(); i++) {
                JSONObject datos = r.getJSONObject(i);
                fecha = datos.getString("fecha");
                hora = datos.getString("hora");
                rLong = datos.getString("long");
                rLat = datos.getString("lat");


                switch (datos.getString("tipo")) {
                    case "he":
                        tipo = "ENTRADA";
                        btTipo = BitmapDescriptorFactory.HUE_GREEN;
                        break;
                    case "hs":
                        tipo = "SALIDA";
                        btTipo = BitmapDescriptorFactory.HUE_RED;
                        break;
                }

                Log.v("Registros",
                        "\n\tfecha: " + fecha +
                                "\n\thora: " + hora +
                                "\n\ttipo: " + tipo +
                                "\n\tlongitud: " + String.valueOf(rLong) +
                                "\n\tlatitud: " + String.valueOf(rLat));


                MarkerRegistro = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(rLat), Double.parseDouble(rLong)))
                        .title(tipo)
                        .snippet(hora)
                        .icon(BitmapDescriptorFactory.defaultMarker(btTipo))
                );

                registrosGPS.add(MarkerRegistro);
            }
        }

    }

    private void moverCamara(Marker m) {

        CameraPosition camera = new CameraPosition.Builder()
                .target(m.getPosition())
                .zoom(18)
                .bearing(90)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    private void postRequestConsultaRegistro(JSONObject data) {
        fRequestQueue = volley.getRequestQueue();
        String url = "https://informehoras.es/appMovil/consultaRegistros.php";

        JsonObjectRequest jsonRequestConsultaRegistro = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (Boolean.valueOf(response.getString("Autenticacion"))) {
                                limpiarMapa();
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
                } else if (error instanceof NoConnectionError) {
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
        fRequestQueue.add(jsonRequestConsultaRegistro);
    }

    private void limpiarMapa(){
        if (registrosGPS.size() > 0) {
            for (int i=0; i<registrosGPS.size();i++) {
                registrosGPS.get(i).remove();
            }
            registrosGPS.clear();
        }
    }

    /*private void mostrarRegistro(JSONObject response) throws JSONException {
        //Log.v("Ultimo registro",response.getJSONArray("ultReg")[0].getString("fecha"));
        JSONArray r = response.getJSONArray("registros");
        for (int i = 0; i < r.length(); i++) {
            JSONObject datos = r.getJSONObject(i);
            Log.v("Registros", "\n\tfecha: " + datos.getString("fecha") + "\n\thora: " + datos.getString("hora") + "\n\ttipo: " + datos.getString("tipo"));
        }
    }*/
}
