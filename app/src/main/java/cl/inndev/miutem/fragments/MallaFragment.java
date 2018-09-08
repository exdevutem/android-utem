package cl.inndev.miutem.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.R;
import cl.inndev.miutem.adapters.MallaAdapter;
import cl.inndev.miutem.models.Asignatura;
import cl.inndev.miutem.models.Carrera;
import cl.inndev.miutem.deserializers.MallaDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.items.AsignaturaItem;
import cl.inndev.miutem.items.NivelItem;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class MallaFragment extends Fragment {

    private RecyclerView mListMalla;
    private SwipeRefreshLayout mSwipeContainer;
    private Integer mId;
    private Call<List<Carrera.Nivel>> mCallMalla;
    //private ShimmerFrameLayout mShimmerViewContainer;

    public MallaFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlexibleAdapter.useTag("MallaAdapter");
        Bundle bundle = getArguments();
        mId = bundle.getInt("id");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_carrera_malla, container, false);

        mListMalla = rootView.findViewById(R.id.list_malla);
        mSwipeContainer = rootView.findViewById(R.id.swipe_container);
        mListMalla.setLayoutManager(new LinearLayoutManager(getContext()));
        mListMalla.addItemDecoration(new FlexibleItemDecoration(getContext())
                .addItemViewType(R.layout.view_carrera_malla_nivel, 8, 0, 8, 0)
                .withRightEdge(true)
                .withLeftEdge(true)
                .withSectionGapOffset(8));

        Type resultType = new TypeToken<List<Carrera.Nivel>>() {}.getType();
        /*
        Reservoir.getAsync("carrera" + mId + "malla", resultType, new ReservoirGetCallback<List<Carrera.Nivel>>() {
            @Override
            public void onSuccess(List<Carrera.Nivel> malla) {
                setMalla(malla);
            }

            @Override
            public void onFailure(Exception e) {
                //mShimmerViewContainer.setVisibility(View.VISIBLE);
                //mShimmerViewContainer.startShimmer();
                getMalla(mId);
            }
        });
        */
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMalla(mId);
            }
        });
        //mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCallMalla != null) {
            mCallMalla.cancel();
        }
    }

    private void getMalla(@NonNull final Integer id) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new MallaDeserializer())
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

        mCallMalla = client.getMalla(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null),
                id);

        mCallMalla.enqueue(new Callback<List<Carrera.Nivel>>() {
            @Override
            public void onResponse(Call<List<Carrera.Nivel>> call, Response<List<Carrera.Nivel>> response) {
                //mShimmerViewContainer.stopShimmer();
                //mShimmerViewContainer.setVisibility(View.GONE);
                switch (response.code()) {
                    case 200:
                        /*
                        try {
                            Reservoir.put("carrera" + id + "malla", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        */
                        setMalla(response.body());
                        break;
                    default:
                        Toast.makeText(getContext(), "Ocurrió un error inesperado al cargar el horario", Toast.LENGTH_SHORT).show();
                        break;
                }
                mSwipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Carrera.Nivel>> call, Throwable t) {
                t.printStackTrace();
                mSwipeContainer.setRefreshing(false);
                //mShimmerViewContainer.setVisibility(View.GONE);
                //mShimmerViewContainer.stopShimmer();
            }
        });
    }

    private void setMalla(List<Carrera.Nivel> malla) {
        List<NivelItem> niveles = new ArrayList<>();
        for (Carrera.Nivel nivel : malla) {
            NivelItem nivelItem = new NivelItem(nivel);
            for (Asignatura asignatura : nivel.getAsignaturas()) {
                nivelItem.addSubItem(new AsignaturaItem(nivelItem, getContext(), asignatura));
            }
            niveles.add(nivelItem);
        }
        MallaAdapter adapter = new MallaAdapter(niveles, getContext());
        adapter.expandItemsAtStartUp()
                .setAutoScrollOnExpand(true)
                .setStickyHeaders(true)
                .setStickyHeaderElevation(8);

        mListMalla.setLayoutManager(new LinearLayoutManager(getContext()));
        mListMalla.setAdapter(adapter);
        mListMalla.setHasFixedSize(true);
    }


}
