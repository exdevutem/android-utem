package cl.inndev.miutem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.AsignaturaActivity;
import cl.inndev.miutem.activities.CarreraActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.adapters.AsignaturasAdapter;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.deserializers.AsignaturasDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class AsignaturasFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ListView mListAsignaturas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_asignaturas, container, false);
        mListAsignaturas = view.findViewById(R.id.list_asignaturas);

        final AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Asignatura asignatura = (Asignatura) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), AsignaturaActivity.class);
                intent.putExtra("ASIGNATURA_ID", asignatura.getmId());
                startActivity(intent);
            }
        };

        getAsignaturas();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        mFirebaseAnalytics.setCurrentScreen(getActivity(), AsignaturasFragment.class.getSimpleName(),
                AsignaturasFragment.class.getSimpleName());
        ((MainActivity) getActivity()).setActionBarTitle("Asignaturas");

    }

    private void getAsignaturas() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new AsignaturasDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);


        Map<String, String> credenciales = Estudiante.getCredenciales(getContext());

        Call<List<Asignatura>> call = restClient.getAsignaturas(credenciales.get("rut"), credenciales.get("token"));

        call.enqueue(new Callback<List<Asignatura>>() {
            @Override
            public void onResponse(Call<List<Asignatura>> call, Response<List<Asignatura>> response) {
                switch (response.code()) {
                    case 200:
                        mListAsignaturas.setAdapter(new AsignaturasAdapter(getContext(), response.body()));
                        break;
                    default:
                        Toast.makeText(getContext(), "Error desconocido", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Asignatura>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
