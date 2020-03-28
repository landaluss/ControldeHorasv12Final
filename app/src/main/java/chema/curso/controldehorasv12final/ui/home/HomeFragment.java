package chema.curso.controldehorasv12final.ui.home;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

                entrada.setVisibility(View.INVISIBLE);
                salida.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Me voy, sale el de salida", Toast.LENGTH_SHORT).show();
            }
        });

        salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}
