package cl.inndev.miutem.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.MainActivity;


public class AsignaturasFragment extends Fragment {

    private ListView listAsignaturas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Asignaturas");
        View view = inflater.inflate(R.layout.fragment_asignaturas, container, false);
        return view;
    }
}

