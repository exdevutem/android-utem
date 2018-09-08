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
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.AsignaturaActivity;
import cl.inndev.miutem.activities.CarreraActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.adapters.AsignaturasAdapter;
import cl.inndev.miutem.models.Asignatura;
import cl.inndev.miutem.interfaces.ApiUtem;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class AsignaturasFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ListView mListAsignaturas;
    private AdapterView.OnItemClickListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_asignaturas, container, false);
        mListAsignaturas = view.findViewById(R.id.list_asignaturas);

        mListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Asignatura asignatura = (Asignatura) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), AsignaturaActivity.class);
                intent.putExtra("ASIGNATURA_SECCION_ID", asignatura.getSeccion().getId());
                intent.putExtra("ASIGNATURA_INDEX", position);
                startActivity(intent);
            }
        };

        Type resultType = new TypeToken<List<Asignatura>>() {}.getType();
        /*
        Reservoir.getAsync("asignaturas", resultType, new ReservoirGetCallback<List<Asignatura>>() {
            @Override
            public void onSuccess(List<Asignatura> asignaturas) {
                setLista(asignaturas);
            }

            @Override
            public void onFailure(Exception e) {
                getAsignaturas();
            }
        });
        */

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
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        Call<List<Asignatura>> call = restClient.getAsignaturas(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null)
        );

        call.enqueue(new Callback<List<Asignatura>>() {
            @Override
            public void onResponse(Call<List<Asignatura>> call, Response<List<Asignatura>> response) {
                switch (response.code()) {
                    case 200:
                        /*
                        try {
                            Reservoir.put("asignaturas", response.body());
                            setLista(response.body());
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        */
                        break;
                    default:
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Asignatura>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLista(List<Asignatura> datos) {
        if (datos.size() == 1) {
            Intent intent = new Intent(getActivity(), CarreraActivity.class);
            intent.putExtra("ASIGNATURA_ID", datos.get(0).getId());
            intent.putExtra("ASIGNATURA_INDEX", 0);
            startActivity(intent);
        } else {
            AsignaturasAdapter adapter = new AsignaturasAdapter(getContext(), datos);
            mListAsignaturas.setAdapter(adapter);
            mListAsignaturas.setOnItemClickListener(mListener);
        }
    }
}
