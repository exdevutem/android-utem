package cl.inndev.miutem.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.classes.Asignatura;


public class CarrerasFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView mListMalla;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_carreras, container, false);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set title bar
        mFirebaseAnalytics.setCurrentScreen(getActivity(), CarrerasFragment.class.getSimpleName(),
                CarrerasFragment.class.getSimpleName());
        ((MainActivity) getActivity()).setActionBarTitle("Carreras");

    }
}