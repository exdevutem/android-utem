package cl.inndev.miutem.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.MainActivity;


public class SesaesFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_sesaes, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set title bar
        mFirebaseAnalytics.setCurrentScreen(getActivity(), SesaesFragment.class.getSimpleName(),
                SesaesFragment.class.getSimpleName());
        ((MainActivity) getActivity()).setActionBarTitle("SESAES");
    }
}
