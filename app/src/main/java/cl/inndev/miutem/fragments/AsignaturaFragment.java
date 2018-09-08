package cl.inndev.miutem.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.analytics.FirebaseAnalytics;
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
import cl.inndev.miutem.adapters.DocentesAdapter;
import cl.inndev.miutem.models.Asignatura;
import cl.inndev.miutem.models.Docente;
import cl.inndev.miutem.interfaces.ApiUtem;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class AsignaturaFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView mTextCodigo;
    private TextView mTextNombre;
    private TextView mTextTipo;
    private TextView mTextDocentes;
    private CardView mCardDocentes;
    private ProgressBar mProgressAsistencia;
    private PieChart mChartAsistencia;
    private RecyclerView mRecyclerDocentes;
    private SwipeRefreshLayout mSwipeContainer;
    private Integer mSeccionId;
    private CardView mCardAsistencia;
    private Call<List<Asignatura>> mCallAsignaturas;
    private Call<Asignatura.Asistencia> mCallBitacora;

    public AsignaturaFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mSeccionId = bundle.getInt("seccionId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.tab_asignatura_resumen, container, false);
        mTextCodigo = view.findViewById(R.id.text_codigo);
        mTextNombre = view.findViewById(R.id.text_nombre);
        mTextTipo = view.findViewById(R.id.text_tipo);
        mTextDocentes = view.findViewById(R.id.text_docentes);
        mCardDocentes = view.findViewById(R.id.card_docentes);
        mCardAsistencia = view.findViewById(R.id.card_asistencia);
        mProgressAsistencia = view.findViewById(R.id.progress_asistencia);
        mChartAsistencia = view.findViewById(R.id.chart_asistencia);
        mSwipeContainer = view.findViewById(R.id.swipe_container);
        mRecyclerDocentes = view.findViewById(R.id.recycler_docentes);

        Type asignaturasType = new TypeToken<List<Asignatura>>() {}.getType();
        /*
        Reservoir.getAsync("asignaturas", asignaturasType, new ReservoirGetCallback<List<Asignatura>>() {
            @Override
            public void onSuccess(List<Asignatura> asignaturas) {
                for (Asignatura asignatura : asignaturas) {
                    if (asignatura.getSeccion().getId().equals(mSeccionId)) {
                        setDocentes(asignatura.getDocentes());
                        setAsignatura(asignatura);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                getAsignaturas();
            }
        });
        */

        Type bitacoraType = new TypeToken<Asignatura.Asistencia>() {}.getType();
        /*
        Reservoir.getAsync("asignatura" + mSeccionId + "asistencia", bitacoraType, new ReservoirGetCallback<Asignatura.Asistencia>() {
            @Override
            public void onSuccess(Asignatura.Asistencia asistencia) {
                setAsistencia(asistencia);
            }

            @Override
            public void onFailure(Exception e) {
                getBitacora(mSeccionId);
            }
        });
        */

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAsignaturas();
                getBitacora(mSeccionId);
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(getActivity(), AsignaturaFragment.class.getSimpleName(),
                AsignaturaFragment.class.getSimpleName());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCallAsignaturas != null) {
            mCallAsignaturas.cancel();
        }
        if (mCallBitacora != null) {
            mCallBitacora.cancel();
        }
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
                .baseUrl(ApiUtem.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        mCallAsignaturas = restClient.getAsignaturas(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null)
        );

        mCallAsignaturas.enqueue(new Callback<List<Asignatura>>() {
            @Override
            public void onResponse(@NonNull Call<List<Asignatura>> call, @NonNull Response<List<Asignatura>> response) {
                mSwipeContainer.setRefreshing(false);
                switch (response.code()) {
                    case 200:
                        /*
                        try {
                            Reservoir.put("asignaturas", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        */
                        for (Asignatura asignatura : response.body()) {
                            if (asignatura.getSeccion().getId().equals(mSeccionId)) {
                                setDocentes(asignatura.getDocentes());
                                setAsignatura(asignatura);
                            }
                        }
                        break;
                    default:
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Asignatura>> call, @NonNull Throwable t) {
                mSwipeContainer.setRefreshing(false);
                t.printStackTrace();
            }
        });
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

        mCallBitacora = client.getBitacora(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null),
                id);

        mCallBitacora.enqueue(new Callback<Asignatura.Asistencia>() {
            @Override
            public void onResponse(Call<Asignatura.Asistencia> call, Response<Asignatura.Asistencia> response) {
                mSwipeContainer.setRefreshing(false);
                switch (response.code()) {
                    case 200:
                        /*
                        try {
                            Reservoir.put("asignatura" + id + "asistencia", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        */
                        setAsistencia(response.body());
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

    private void setAsignatura(Asignatura asignatura) {
        mTextNombre.setText(asignatura.getNombre());
        mTextCodigo.setText(asignatura.getCodigo() + "/" + asignatura.getSeccion().getNumero());
        mTextTipo.setText(asignatura.getTipo());
    }

    private void setAsistencia(Asignatura.Asistencia asistencia) {
        if (asistencia.getRegistrados() == null || asistencia.getRegistrados() == 0) {
            mCardAsistencia.setVisibility(View.GONE);
        } else {
            mCardAsistencia.setVisibility(View.VISIBLE);
            mChartAsistencia.setUsePercentValues(true);
            mChartAsistencia.getDescription().setEnabled(false);
            mChartAsistencia.setDrawHoleEnabled(true);
            mChartAsistencia.setHoleColor(Color.WHITE);
            mChartAsistencia.setRotationEnabled(false);
            mChartAsistencia.setHighlightPerTapEnabled(true);
            mChartAsistencia.setMaxAngle(180f); // HALF CHART
            mChartAsistencia.setRotationAngle(180f);
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(asistencia.getAsistencias(), "Asistencia"));
            entries.add(new PieEntry(asistencia.getInasistencias(), "Inasistencias"));
            PieDataSet dataSet = new PieDataSet(entries, null);
            dataSet.setValueTextSize(11f);
            dataSet.setValueTextColor(Color.WHITE);
            int[] colores = new int[3];
            colores[0] = getResources().getColor(R.color.green);
            colores[1] = getResources().getColor(R.color.red);
            dataSet.setColors(colores);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            mChartAsistencia.setData(data);
            mProgressAsistencia.setVisibility(View.GONE);
            mChartAsistencia.animateY(2000, Easing.EasingOption.EaseInOutQuad);
            mChartAsistencia.setVisibility(View.VISIBLE);

            View parent = (View) mChartAsistencia.getParent();
            int height = parent.getHeight();

            int offset = (int)(height * 0.85);

            FrameLayout.LayoutParams rlParams = (FrameLayout.LayoutParams) mChartAsistencia.getLayoutParams();
            rlParams.setMargins(0, 0, 0, -offset);
            mChartAsistencia.setLayoutParams(rlParams);

            mChartAsistencia.invalidate();
        }
    }

    private void setDocentes(List<Docente> docentes) {
        if (docentes == null || docentes.size() == 0) {
            mCardDocentes.setVisibility(View.GONE);
        } else {
            mCardDocentes.setVisibility(View.VISIBLE);
            mRecyclerDocentes.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerDocentes.setAdapter(new DocentesAdapter(docentes));
            if (docentes.size() == 1) {
                mTextDocentes.setText("Docente");
            } else {
                mTextDocentes.setText("Docentes");
            }
        }

    }
}