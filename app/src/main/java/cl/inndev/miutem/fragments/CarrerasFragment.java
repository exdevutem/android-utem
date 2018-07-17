package cl.inndev.miutem.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.CarreraActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.adapters.CarrerasAdapter;
import cl.inndev.miutem.adapters.MallaAdapter;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;


public class CarrerasFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ListView mListCarreras;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_carreras, container, false);
        mListCarreras = view.findViewById(R.id.list_carreras);

        final AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Carrera carrera = (Carrera) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), CarreraActivity.class);
                intent.putExtra("CARRERA_ID", carrera.getmId());
                startActivity(intent);
            }
        };

        Type resultType = new TypeToken<List<Carrera>>() {}.getType();
        Reservoir.getAsync("carreras", resultType, new ReservoirGetCallback<List<Carrera>>() {
            @Override
            public void onSuccess(List<Carrera> carreras) {
                CarrerasAdapter adapter = new CarrerasAdapter(getContext(), carreras);
                mListCarreras.setAdapter(adapter);
                mListCarreras.setOnItemClickListener(listener);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
            }
        });

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