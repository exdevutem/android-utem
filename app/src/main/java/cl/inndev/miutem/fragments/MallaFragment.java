package cl.inndev.miutem.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pixplicity.easyprefs.library.Prefs;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.CarreraActivity;
import cl.inndev.miutem.adapters.HorarioAdapter;
import cl.inndev.miutem.adapters.MallaAdapter;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.deserializers.MallaDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class MallaFragment extends Fragment {
    //private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView mListMalla;
    private MallaAdapter mMallaAdapter;
    private ShimmerFrameLayout mShimmerViewContainer;

    public MallaFragment() {}

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
        View rootView = inflater.inflate(R.layout.tab_carrera_malla, container, false);

        mListMalla = rootView.findViewById(R.id.list_malla);
        // mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container);
        // mShimmerViewContainer.startShimmer();

        getMalla();
        return rootView;
    }

    private void getMalla() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new MallaDeserializer())
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        CarreraActivity carreraActivity = (CarreraActivity) getActivity();

        Toast.makeText(carreraActivity, "" + carreraActivity.mId, Toast.LENGTH_SHORT).show();

        Call<List<Carrera.Nivel>> call = client.getMalla(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null),
                carreraActivity.mId);

        call.enqueue(new Callback<List<Carrera.Nivel>>() {
            @Override
            public void onResponse(Call<List<Carrera.Nivel>> call, Response<List<Carrera.Nivel>> response) {
                switch (response.code()) {
                    case 200:
                        MallaAdapter adapter = new MallaAdapter(getContext(), response.body());
                        // mShimmerViewContainer.stopShimmer();
                        mListMalla.setAdapter(adapter);
                        mListMalla.setVisibility(View.VISIBLE);
                        /* adapter = setHorarioClickListener(adapter, response.body());
                        mTableHorario.setAdapter(adapter);
                        mTableHorario.setVisibility(View.VISIBLE); */
                        break;
                    default:
                        Toast.makeText(getContext(), "Ocurrió un error inesperado al cargar el horario", Toast.LENGTH_SHORT).show();
                        break;
                }
                // mProgressCargando.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Carrera.Nivel>> call, Throwable t) {
                Log.e("RETROFIT", t.toString());
                Toast.makeText(getActivity(), "No se pudo obtener la malla curricular", Toast.LENGTH_LONG).show();

                // mProgressCargando.setVisibility(View.GONE);
            }
        });
    }
}
