package cl.inndev.miutem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.AsignaturaActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.activities.PerfilActivity;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.interfaces.ApiUtem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class AsignaturasFragment extends Fragment {

    private ListView mListAsignaturas;
    private ProgressBar mProgressCargando;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Asignaturas");
        View view = inflater.inflate(R.layout.fragment_asignaturas, container, false);

        mListAsignaturas = view.findViewById(R.id.list_asignaturas);
        mProgressCargando = view.findViewById(R.id.progress_cargando);

        mListAsignaturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long s) {
                Toast.makeText(getContext(), R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
            }
        });

        getAsignaturas();

        return view;
    }

    private void mostrarAsignaturas(ArrayList<Asignatura> asignaturas) {
        ArrayList<String> nombres = new ArrayList<>();
        for (int i = 0; i < asignaturas.size(); i++) {
            nombres.add(asignaturas.get(i).getNombre());
        }
        mProgressCargando.setVisibility(View.GONE);
        mListAsignaturas.setVisibility(View.VISIBLE);
        mListAsignaturas.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, nombres));
    }

    private void getAsignaturas() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        Map<String, String> credenciales = Estudiante.getCredenciales(getContext());

        Call<ArrayList<Asignatura>> call = restClient.getAsignaturas(credenciales.get("rut"), credenciales.get("token"));

        call.enqueue(new Callback<ArrayList<Asignatura>>() {
            @Override
            public void onResponse(Call<ArrayList<Asignatura>> call, Response<ArrayList<Asignatura>> response) {
                switch (response.code()) {
                    case 200:
                        mostrarAsignaturas(response.body());
                        break;
                    default:
                        Toast.makeText(getContext(), "Error desconocido", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Asignatura>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

