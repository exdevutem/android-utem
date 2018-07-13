package cl.inndev.miutem.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.MainActivity;


public class AsignaturaFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView mListMalla;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.tab_asignatura_horario, container, false);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set title bar
        mFirebaseAnalytics.setCurrentScreen(getActivity(), AsignaturaFragment.class.getSimpleName(),
                AsignaturaFragment.class.getSimpleName());
        ((MainActivity) getActivity()).setActionBarTitle("Carreras");

    }
}