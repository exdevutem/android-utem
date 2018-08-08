package cl.inndev.miutem.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.CarreraActivity;
import cl.inndev.miutem.adapters.CamposAdapter;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.deserializers.CarrerasDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class CarreraFragment extends Fragment {
    private TextView mTextCodigo;
    private TextView mTextPlan;
    private TextView mTextEstado;
    private TextView mTextIngreso;
    private TextView mTextNombre;
    private TextView mTextInicio;
    private TextView mTextTermino;
    private ProgressBar mProgressRendimiento;
    private LineChart mChartRendimiento;
    private ProgressBar mProgressAsignaturas;
    private BarChart mChartAsignaturas;
    private SwipeRefreshLayout mSwipeContainer;
    private Integer mId;
    private Call<List<Carrera.Semestre>> mCallBoletin;
    private Call<List<Carrera>> mCallCarreras;

    public CarreraFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mId = bundle.getInt("id");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_carrera_resumen,
                container, false);

        mTextCodigo = rootView.findViewById(R.id.text_codigo);
        mTextPlan = rootView.findViewById(R.id.text_plan);
        mTextEstado = rootView.findViewById(R.id.text_estado);
        mTextIngreso = rootView.findViewById(R.id.text_ingreso);
        mTextNombre = rootView.findViewById(R.id.text_nombre);
        mTextInicio = rootView.findViewById(R.id.text_inicio);
        mTextTermino = rootView.findViewById(R.id.text_termino);
        mProgressRendimiento = rootView.findViewById(R.id.progress_rendimiento);
        mChartRendimiento = rootView.findViewById(R.id.chart_rendimiento);
        mProgressAsignaturas = rootView.findViewById(R.id.progress_asignaturas);
        mChartAsignaturas = rootView.findViewById(R.id.chart_asignaturas);
        mSwipeContainer = rootView.findViewById(R.id.swipe_container);

        Type carrerasType = new TypeToken<List<Carrera>>() {}.getType();
        Reservoir.getAsync("carreras", carrerasType, new ReservoirGetCallback<List<Carrera>>() {
            @Override
            public void onSuccess(List<Carrera> carreras) {
                for (Carrera carrera : carreras) {
                    if (carrera.getmId().equals(mId)) {
                        setCarrera(carrera);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                getCarreras();
            }
        });

        Type boletinType = new TypeToken<List<Carrera.Semestre>>() {}.getType();
        Reservoir.getAsync("carrera" + mId +"boletin", boletinType, new ReservoirGetCallback<List<Carrera.Semestre>>() {
            @Override
            public void onSuccess(List<Carrera.Semestre> boletin) {
                setRendimiento(boletin);
                setAsignaturas(boletin);
            }

            @Override
            public void onFailure(Exception e) {
                getBoletin(mId);
            }
        });

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCarreras();
                getBoletin(mId);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCallCarreras != null) {
            mCallCarreras.cancel();
        }
        if (mCallBoletin != null) {
            mCallBoletin.cancel();
        }
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

        mCallCarreras = client.getCarreras(
                Prefs.getLong("rut", 0),
                Prefs.getString("token", null));

        mCallCarreras.enqueue(new Callback<List<Carrera>>() {
            @Override
            public void onResponse(Call<List<Carrera>> call, Response<List<Carrera>> response) {
                switch (response.code()) {
                    case 200:
                        try {
                            Reservoir.put("carreras", response.body());
                        } catch (IOException e) {
                            Log.d("CARRERA", e.toString());
                            e.printStackTrace();
                        }
                        for (Carrera carrera : response.body()) {
                            if (carrera.getmId().equals(mId)) {
                                setCarrera(carrera);
                            }
                        }
                        break;
                    default:
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Carrera>> call, Throwable t) {
                t.printStackTrace();
            }
        });
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
                        try {
                            Reservoir.put("carrera" + id +"boletin", response.body());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setRendimiento(response.body());
                        setAsignaturas(response.body());
                        break;
                    default:
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Carrera.Semestre>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setRendimiento(List<Carrera.Semestre> boletin) {
        List<Entry> entries = new ArrayList<>();
        double acumulado = 0;

        for (int i = 0; i < boletin.size(); i++) {
            Carrera.Semestre semestre = boletin.get(i);
            if (semestre.getPromedioFinal() != null) {
                if (semestre.getNombre().contains("Verano")) {
                    acumulado += 0.5;
                }
                entries.add(new Entry((i - (float) acumulado), semestre.getPromedioFinal().floatValue()));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, null);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(2);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getContext().getResources().getColor(R.color.red));
        dataSet.setValueTextSize(12);
        dataSet.setValueFormatter(new MyValueFormatter());
        dataSet.setCircleColor(getResources().getColor(R.color.red));
        dataSet.setCircleColorHole(Color.WHITE);
        dataSet.setDrawCircleHole(true);
        dataSet.setColor(getResources().getColor(R.color.red));
        dataSet.setCircleRadius(4);
        dataSet.setCircleHoleRadius(1);
        LineData lineData = new LineData(dataSet);

        XAxis x = mChartRendimiento.getXAxis();
        x.setAxisMinimum(1);
        x.setGranularity(1);
        x.setGranularityEnabled(true);
        x.setDrawLabels(false);
        x.setDrawGridLines(false);

        YAxis y = mChartRendimiento.getAxisLeft();
        y.setAxisMinimum(0);
        y.setAxisMaximum(7);
        y.setGranularity((float) 0.1);
        y.setGranularityEnabled(true);
        y.setMinWidth(8);

        mChartRendimiento.setData(lineData);
        mChartRendimiento.getXAxis().setEnabled(false);
        mChartRendimiento.getDescription().setEnabled(false);
        mChartRendimiento.setTouchEnabled(false);
        mChartRendimiento.getAxisRight().setEnabled(false);
        mChartRendimiento.animateY(1000);
        mChartRendimiento.getLegend().setEnabled(false);
        mChartRendimiento.setExtraOffsets(8f, 8f, 8f, 8f);
        mProgressRendimiento.setVisibility(View.GONE);
        mChartRendimiento.setVisibility(View.VISIBLE);
        mChartRendimiento.invalidate();
    }

    private void setAsignaturas(List<Carrera.Semestre> boletin) {
        List<BarEntry> entries = new ArrayList<>();
        double acumulado = 0;
        for (int i = 0; i < boletin.size(); i++) {
            Carrera.Semestre semestre = boletin.get(i);
            float[] datos = new float[3];
            datos[0] = semestre.getAsignaturasAprobadas() == null ? 0 : semestre.getAsignaturasAprobadas();
            datos[1] = semestre.getAsignaturasConvalidadas() == null ? 0 : semestre.getAsignaturasConvalidadas();
            datos[2] = semestre.getAsignaturasReprobadas() == null ? 0 : semestre.getAsignaturasReprobadas();
            if (semestre.getNombre().contains("Verano")) {
                acumulado += 0.5;
            }
            BarEntry stackedEntry = new BarEntry(i, datos);
            entries.add(stackedEntry);
        }
        BarDataSet dataSet = new BarDataSet(entries, null);
        int[] colores = new int[3];
        colores[0] = getResources().getColor(R.color.ramo_aprobado);
        colores[1] = getResources().getColor(R.color.yellow);
        colores[2] = getResources().getColor(R.color.ramo_reprobado);
        dataSet.setColors(colores);
        dataSet.setStackLabels(new String[]{"Aprobadas", "Convalidadas", "Reprobadas"});
        BarData barData = new BarData(dataSet);
        mChartAsignaturas.setData(barData);
        mChartAsignaturas.setExtraOffsets(8f, 8f, 8f, 8f);
        mProgressAsignaturas.setVisibility(View.GONE);
        mChartAsignaturas.setVisibility(View.VISIBLE);
        mChartAsignaturas.invalidate();
    }

    private void setCarrera(Carrera carrera) {
        mTextCodigo.setText(carrera.getCodigo());
        mTextPlan.setText(carrera.getPlan());
        mTextEstado.setText(carrera.getEstado());
        mTextIngreso.setText(carrera.getCodigo());
        mTextNombre.setText(carrera.getNombre());
        mTextInicio.setText(carrera.getAnioInicio().toString());
        mTextTermino.setText(carrera.getAnioTermino() == null ? null : carrera.getAnioTermino().toString());
    }

    private class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value); // e.g. append a dollar-sign
        }
    }
}
