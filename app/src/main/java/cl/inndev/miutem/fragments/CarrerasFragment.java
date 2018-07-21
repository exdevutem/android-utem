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

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeoutException;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.CarreraActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.adapters.CarrerasAdapter;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.deserializers.CarrerasDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class CarrerasFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ListView mListCarreras;
    private AdapterView.OnItemClickListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_carreras, container, false);
        mListCarreras = view.findViewById(R.id.list_carreras);

        mListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Carrera carrera = (Carrera) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), CarreraActivity.class);
                intent.putExtra("CARRERA_ID", carrera.getmId());
                intent.putExtra("CARRERA_INDEX", position);
                startActivity(intent);
            }
        };

        Type resultType = new TypeToken<List<Carrera>>() {}.getType();
        Reservoir.getAsync("carreras", resultType, new ReservoirGetCallback<List<Carrera>>() {
            @Override
            public void onSuccess(List<Carrera> carreras) {
                setLista(carreras);
            }

            @Override
            public void onFailure(Exception e) {
                getCarreras();
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

    private void getCarreras() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new CarrerasDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Call<List<Carrera>> call = client.getCarreras(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null));

        call.enqueue(new Callback<List<Carrera>>() {
            @Override
            public void onResponse(Call<List<Carrera>> call, Response<List<Carrera>> response) {
                switch (response.code()) {
                    case 200:
                        try {
                            Reservoir.put("carreras", response.body());
                            setLista(response.body());
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Carrera>> call, Throwable t) {
                if (t instanceof TimeoutException) {
                    Toast.makeText(getContext(), "¡Ups! Parece que la conexión está algo lenta. Por favor inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setLista(List<Carrera> datos) {
        if (datos.size() == 1) {
            Intent intent = new Intent(getActivity(), CarreraActivity.class);
            intent.putExtra("CARRERA_ID", datos.get(0).getmId());
            intent.putExtra("CARRERA_INDEX", 0);
            startActivity(intent);
        } else {
            CarrerasAdapter adapter = new CarrerasAdapter(getContext(), datos);
            mListCarreras.setAdapter(adapter);
            mListCarreras.setOnItemClickListener(mListener);
        }
    }
}