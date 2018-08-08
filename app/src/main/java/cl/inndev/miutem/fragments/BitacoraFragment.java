package cl.inndev.miutem.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
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
import java.util.concurrent.TimeoutException;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.AsignaturaActivity;
import cl.inndev.miutem.activities.CarreraActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.adapters.BitacoraAdapter;
import cl.inndev.miutem.adapters.NotasAdapter;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.deserializers.CarrerasDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class BitacoraFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView mListBitacora;
    private SwipeRefreshLayout mSwipeContainer;
    private Integer mSeccionId;

    public BitacoraFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AsignaturaActivity asignaturaActivity = (AsignaturaActivity) getActivity();
        View view = inflater.inflate(R.layout.tab_asignatura_bitacora, container, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mListBitacora = view.findViewById(R.id.list_bitacora);
        mSwipeContainer = view.findViewById(R.id.swipe_container);
        mSeccionId = asignaturaActivity.mSeccionId;
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBitacora(mSeccionId);
            }
        });


        Type bitacoraType = new TypeToken<Asignatura.Asistencia>() {}.getType();
        Reservoir.getAsync("asignatura" + mSeccionId + "asistencia", bitacoraType, new ReservoirGetCallback<Asignatura.Asistencia>() {
            @Override
            public void onSuccess(Asignatura.Asistencia asistencia) {
                setBitacora(asistencia);
            }

            @Override
            public void onFailure(Exception e) {
                getBitacora(mSeccionId);
            }
        });
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set title bar
        mFirebaseAnalytics.setCurrentScreen(getActivity(), BitacoraFragment.class.getSimpleName(),
                BitacoraFragment.class.getSimpleName());

    }

    private void getBitacora(final Integer id) {
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

        ApiUtem client = retrofit.create(ApiUtem.class);

        Call<Asignatura.Asistencia> call = client.getBitacora(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null),
                id);

        call.enqueue(new Callback<Asignatura.Asistencia>() {
            @Override
            public void onResponse(Call<Asignatura.Asistencia> call, Response<Asignatura.Asistencia> response) {
                mSwipeContainer.setRefreshing(false);
                switch (response.code()) {
                    case 200:
                        try {
                            Reservoir.put("asignatura" + id + "asistencia", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setBitacora(response.body());
                        break;
                    default:
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Asignatura.Asistencia> call, Throwable t) {
                mSwipeContainer.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void setBitacora(Asignatura.Asistencia asistencia) {
        BitacoraAdapter adapter = new BitacoraAdapter(getContext(), asistencia.getBitacora());
        mListBitacora.setLayoutManager(new LinearLayoutManager(getContext()));
        mListBitacora.setAdapter(adapter);
    }
}