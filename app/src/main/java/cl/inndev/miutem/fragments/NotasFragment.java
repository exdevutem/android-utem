package cl.inndev.miutem.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.AsignaturaActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.adapters.NotasAdapter;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.interfaces.ApiUtem;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class NotasFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView mListNotas;
    private TextView mTextValorPresentacion;
    private TextView mTextValorExamen;
    private TextView mTextNotaFinal;
    private Integer mSeccionId;
    private SwipeRefreshLayout mSwipeContainer;
    private Call<Asignatura> mCallNotas;

    public NotasFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mSeccionId = bundle.getInt("seccionId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_asignatura_notas, container, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mListNotas = view.findViewById(R.id.list_notas);
        mTextValorPresentacion = view.findViewById(R.id.text_valor_presentacion);
        mTextValorExamen = view.findViewById(R.id.text_valor_examen);
        mTextNotaFinal = view.findViewById(R.id.text_nota_final);
        mSwipeContainer = view.findViewById(R.id.swipe_container);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotas(mSeccionId);
            }
        });

        Type resultType = new TypeToken<Asignatura>() {}.getType();
        Reservoir.getAsync("asignatura" + mSeccionId + "notas", resultType, new ReservoirGetCallback<Asignatura>() {
            @Override
            public void onSuccess(Asignatura asignatura) {
                setNotas(asignatura);
            }

            @Override
            public void onFailure(Exception e) {
                getNotas(mSeccionId);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(getActivity(), NotasFragment.class.getSimpleName(),
                NotasFragment.class.getSimpleName());
        ;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCallNotas != null) {
            mCallNotas.cancel();
        }
    }

    private void getNotas(final Integer id) {
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

        mCallNotas = client.getNotas(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null),
                id);

        mCallNotas.enqueue(new Callback<Asignatura>() {
            @Override
            public void onResponse(Call<Asignatura> call, Response<Asignatura> response) {
                switch (response.code()) {
                    case 200:
                        try {
                            Reservoir.put("asignatura" + id + "notas", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setNotas(response.body());
                        break;
                    default:
                        Toast.makeText(getActivity(), "Ocurrió un error inesperado al cargar", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Asignatura> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setNotas(Asignatura asignatura) {
        final Double[] notaPresentacion = {asignatura.getPresentacion() == null ? 0 : asignatura.getPresentacion()};
        mTextValorExamen.setText(asignatura.getNota() == null ? null : asignatura.getNota().toString());
        mTextValorPresentacion.setText(notaPresentacion[0].toString());
        mTextNotaFinal.setText(asignatura.getNota() == null ? null : asignatura.getNota().toString());
        if (asignatura.getPonderadoresRegistrados() == true) {
            NotasAdapter adapter = new NotasAdapter(asignatura.getNotas());
            adapter.setNotaKeyListener(new NotasAdapter.OnNotaKeyListener() {
                @Override
                public void onKey(int i, CharSequence nuevoValor, Double ponderador) {
                    notaPresentacion[0] += Double.parseDouble(nuevoValor.toString()) * ponderador;
                    mTextValorPresentacion.setText("" + round(notaPresentacion[0], 1));
                }
            });
            mListNotas.setLayoutManager(new LinearLayoutManager(getContext()));
            mListNotas.setAdapter(adapter);
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}