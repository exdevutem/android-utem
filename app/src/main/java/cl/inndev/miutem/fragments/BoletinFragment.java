package cl.inndev.miutem.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.inndev.miutem.R;


public class BoletinFragment extends Fragment {
    //private static final String ARG_SECTION_NUMBER = "section_number";

    public BoletinFragment() {}

    /*public static MallaFragment newInstance(int sectionNumber) {
        MallaFragment fragment = new MallaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_carrera_boletin, container, false);

        return rootView;
    }
}
