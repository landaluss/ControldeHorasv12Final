package chema.curso.controldehorasv12final.ui.sesion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import chema.curso.controldehorasv12final.LoginActivity;
import chema.curso.controldehorasv12final.PrincipalActivity;
import chema.curso.controldehorasv12final.R;

public class SesionFragment extends Fragment {

    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_sesion, container, false);
        return rootView;


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        prefs.edit().clear().apply();
        Intent intent = new Intent(getContext() , LoginActivity.class);
        startActivity(intent);

    }

}
