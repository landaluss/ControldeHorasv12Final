package chema.curso.controldehorasv12final.ui.ayuda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import chema.curso.controldehorasv12final.R;

public class ayudaFragment extends Fragment {

    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ayuda, container, false);
        return rootView;
    }
}