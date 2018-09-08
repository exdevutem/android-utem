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

import com.google.gson.FieldNamingPolicy;
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
import cl.inndev.miutem.adapters.BoletinAdapter;
import cl.inndev.miutem.models.Asignatura;
import cl.inndev.miutem.models.Carrera;
import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.items.AsignaturaBoletinItem;
import cl.inndev.miutem.items.SemestreItem;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class BoletinFragment extends Fragment {

    private RecyclerView mListBoletin;
    private SwipeRefreshLayout mSwipeContainer;
    private Integer mId;
    private Call<List<Carrera.Semestre>> mCallBoletin;

    public BoletinFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mId = bundle.getInt("id");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_carrera_boletin, container, false);
        mListBoletin = rootView.findViewById(R.id.list_boletin);
        mSwipeContainer = rootView.findViewById(R.id.swipe_container);
        mListBoletin.setLayoutManager(new LinearLayoutManager(getContext()));
        mListBoletin.addItemDecoration(new FlexibleItemDecoration(getContext())
                .addItemViewType(R.layout.view_carrera_malla_nivel, 8, 0, 8, 0)
                .withRightEdge(true)
                .withLeftEdge(true)
                .withSectionGapOffset(8));
        Type boletinType = new TypeToken<List<Carrera.Semestre>>() {}.getType();
        /*
        Reservoir.getAsync("carrera" + mId + "boletin", boletinType, new ReservoirGetCallback<List<Carrera.Semestre>>() {
            @Override
            public void onSuccess(List<Carrera.Semestre> boletin) {
                setBoletin(boletin);
            }

            @Override
            public void onFailure(Exception e) {
                //mShimmerViewContainer.setVisibility(View.VISIBLE);
                //mShimmerViewContainer.startShimmer();
                getBoletin(mId);
            }
        });
        */
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBoletin(mId);
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCallBoletin != null) {
            mCallBoletin.cancel();
        }
    }

    private void getBoletin(final Integer id) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        mCallBoletin = client.getBoletin(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null),
                id);

        mCallBoletin.enqueue(new Callback<List<Carrera.Semestre>>() {
            @Override
            public void onResponse(Call<List<Carrera.Semestre>> call, Response<List<Carrera.Semestre>> response) {
                switch (response.code()) {
                    case 200:
                        /*
                        try {
                            Reservoir.put("carrera" + id +"boletin", response.body());
                            setBoletin(response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        */
                        break;
                    default:
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
                mSwipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Carrera.Semestre>> call, Throwable t) {
                mSwipeContainer.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    private void setBoletin(List<Carrera.Semestre> boletin) {
        List<SemestreItem> semestres = new ArrayList<>();
        for (Carrera.Semestre semestre : boletin) {
            SemestreItem semestreItem = new SemestreItem(semestre);
            for (Asignatura asignatura : semestre.getAsignaturas()) {
                semestreItem.addSubItem(new AsignaturaBoletinItem(semestreItem, getContext(), asignatura));
            }
            semestres.add(semestreItem);
        }
        BoletinAdapter adapter = new BoletinAdapter(semestres, getContext());
        adapter.expandItemsAtStartUp()
                .setAutoScrollOnExpand(true)
                .setStickyHeaders(true)
                .setStickyHeaderElevation(8);

        mListBoletin.setLayoutManager(new LinearLayoutManager(getContext()));
        mListBoletin.setAdapter(adapter);
        mListBoletin.setHasFixedSize(true);
    }
}
