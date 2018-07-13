package cl.inndev.miutem.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.cleveroad.adaptivetablelayout.OnItemLongClickListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.AsignaturaActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.adapters.HorarioAdapter;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.interfaces.ApiUtem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;

public class HorarioFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private AdaptiveTableLayout mTableHorario;
    private AlertDialog mDialogAsignatura;
    private ProgressBar mProgressCargando;
    private List<String[]> mRowHeaderList = new ArrayList<>();
    private List<String> mColumnHeaderList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_horario, container, false);
        mTableHorario = view.findViewById(R.id.table_horario);
        mProgressCargando = view.findViewById(R.id.progress_cargando);

        String[] bloque = {"8:00", "8:45","9:30"};
        mRowHeaderList.add(bloque);
        String[] bloque1 = {"9:40", "10:25","11:10"};
        mRowHeaderList.add(bloque1);
        String[] bloque2 = {"11:20", "12:05","12:50"};
        mRowHeaderList.add(bloque2);
        String[] bloque3 = {"13:00", "13:45","14:30"};
        mRowHeaderList.add(bloque3);
        String[] bloque4 = {"14:40", "15:25","16:10"};
        mRowHeaderList.add(bloque4);
        String[] bloque5 = {"16:20", "17:05","17:50"};
        mRowHeaderList.add(bloque5);
        String[] bloque6 = {"18:00", "18:45","19:30"};
        mRowHeaderList.add(bloque6);
        String[] bloque7 = {"19:40", "20:25","21:10"};
        mRowHeaderList.add(bloque7);
        String[] bloque8 = {"21:20", "22:05","22:50"};
        mRowHeaderList.add(bloque8);

        mColumnHeaderList.add("Lunes");
        mColumnHeaderList.add("Martes");
        mColumnHeaderList.add("Miércoles");
        mColumnHeaderList.add("Jueves");
        mColumnHeaderList.add("Viernes");
        mColumnHeaderList.add("Sábado");

        getHorario();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set title bar
        mFirebaseAnalytics.setCurrentScreen(getActivity(), HorarioFragment.class.getSimpleName(),
                HorarioFragment.class.getSimpleName());
        ((MainActivity) getActivity()).setActionBarTitle("Horario");
    }

    private void setDialog(Asignatura asignatura) {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_horario_asignatura, null);
        TextView textCodigoAsignatura = view.findViewById(R.id.text_asignatura_codigo);
        TextView textNombreAsignatura = view.findViewById(R.id.text_asignatura_nombre);
        TextView textProfesorAsignatura = view.findViewById(R.id.text_asignatura_profesor);
        TextView textTipoAsignatura = view.findViewById(R.id.text_asignatura_tipo);
        TextView textSeccionAsignatura = view.findViewById(R.id.text_asignatura_seccion);

        textCodigoAsignatura.setText(asignatura.getCodigo());
        textNombreAsignatura.setText(asignatura.getNombre());
        textProfesorAsignatura.setText(asignatura.getProfesor());
        textTipoAsignatura.setText(asignatura.getTipo());
        textSeccionAsignatura.setText(asignatura.getSeccion().toString());
        mDialogAsignatura = new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    private HorarioAdapter setHorarioClickListener(HorarioAdapter adapter, final Estudiante.Horario horario) {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int row, int column) {
                Asignatura clickeada = horario.getDatos().get(column - 1).get(row - 1);
                if (clickeada != null) {
                    //Intent intent = new Intent(getContext(), AsignaturaActivity.class);
                    //intent.putExtra("codigo", clickeada.getCodigo() + "/" + clickeada.getSeccion());
                    //startActivity(intent);
                    Toast.makeText(getActivity(), R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onRowHeaderClick(int row) { }

            @Override
            public void onColumnHeaderClick(int column) { }

            @Override
            public void onLeftTopHeaderClick() { }
        });

        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int row, int column) {
                Asignatura clickeada = horario.getDatos().get(column).get(row);
                if (clickeada != null) {
                    setDialog(clickeada);
                    mDialogAsignatura.show();
                }
            }

            @Override
            public void onLeftTopHeaderLongClick() { }
        });
        return adapter;
    }

    private void getHorario() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Estudiante.Horario.class, new HorarioDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem client = retrofit.create(ApiUtem.class);

        Map<String, String> credenciales = Estudiante.getCredenciales(getActivity());

        Call<Estudiante.Horario> call = client.getHorarios(credenciales.get("rut"), credenciales.get("token"));

        call.enqueue(new Callback<Estudiante.Horario>() {
            @Override
            public void onResponse(Call<Estudiante.Horario> call, Response<Estudiante.Horario> response) {
                switch (response.code()) {
                    case 200:
                        HorarioAdapter adapter = new HorarioAdapter(getContext(), mRowHeaderList, mColumnHeaderList, response.body());
                        adapter = setHorarioClickListener(adapter, response.body());
                        mTableHorario.setAdapter(adapter);
                        mTableHorario.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Toast.makeText(getContext(), "Ocurrió un error inesperado al cargar el horario", Toast.LENGTH_SHORT).show();
                        break;
                }
                mProgressCargando.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Estudiante.Horario> call, Throwable t) {
                Toast.makeText(getActivity(), "No se pudo cargar el horario", Toast.LENGTH_SHORT).show();
                mProgressCargando.setVisibility(View.GONE);
            }
        });
    }

    private class HorarioDeserializer implements JsonDeserializer<Estudiante.Horario> {

        @Override
        public Estudiante.Horario deserialize(JsonElement json,
                                              Type type,
                                              JsonDeserializationContext context) throws JsonParseException {
            Map<String, Asignatura> cursadas = new HashMap<>();
            JsonArray carreras = (JsonArray) json;
            // TODO: Agregar la posibilidad de multiples horarios
            JsonObject carrera = (JsonObject) carreras.get(0);
            JsonArray asignaturas = carrera.getAsJsonArray("asignaturas");
            for (int j = 0; j < asignaturas.size(); j++) {
                JsonObject asignatura = (JsonObject) asignaturas.get(j);
                Asignatura nueva = new Asignatura(asignatura.get("nombre").getAsString(),
                        asignatura.get("tipo").getAsString(),
                        asignatura.get("profesor").getAsString(),
                        asignatura.get("seccion").getAsInt());
                cursadas.put(asignatura.get("codigo").getAsString() + "/" + nueva.getSeccion(),
                        nueva);
            }


            JsonObject semana = carrera.getAsJsonObject("horario");
            List<List<Asignatura>> horario = new ArrayList<>();
            for (Map.Entry<String, JsonElement> dia : semana.entrySet()) {
                JsonArray dias = (JsonArray) dia.getValue();
                List<Asignatura> diaHorario = new ArrayList<>();
                for (int j = 0; j < dias.size(); j++) {
                    JsonObject periodo = (JsonObject) dias.get(j);
                    JsonArray bloques = periodo.getAsJsonArray("bloques");
                    if (!bloques.get(0).isJsonNull()) {
                        JsonObject bloque = (JsonObject) bloques.get(0);
                        Asignatura asignatura = cursadas.get(
                                bloque.get("codigoAsignatura").getAsString() + "/" +
                                        bloque.get("seccionAsignatura"));
                        asignatura.setCodigo(bloque.get("codigoAsignatura").getAsString());
                        asignatura.setSala(bloque.get("sala").getAsString());
                        diaHorario.add(asignatura);
                    } else {
                        diaHorario.add(null);
                    }
                }
                horario.add(diaHorario);
            }

            return new Estudiante.Horario(horario);
        }
    }
}
