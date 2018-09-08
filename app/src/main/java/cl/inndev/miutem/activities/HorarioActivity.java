package cl.inndev.miutem.activities;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.cleveroad.adaptivetablelayout.OnItemLongClickListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.adapters.HorarioAdapter;
import cl.inndev.miutem.models.Asignatura;
import cl.inndev.miutem.models.Carrera;
import cl.inndev.miutem.models.Horario;
import cl.inndev.miutem.deserializers.HorariosDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.utils.StoreUtils;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;

public class HorarioActivity extends AppCompatActivity {

    private AdaptiveTableLayout mTableHorario;
    private AlertDialog mDialogAsignatura;
    private ProgressBar mProgressCargando;
    private Toolbar mToolbar;
    private List<String[]> mRowHeaderList = new ArrayList<>();
    private List<String> mColumnHeaderList = new ArrayList<>();
    private Integer mHorarioIndex;
    private AccountManager mAccountManager;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);
        mTableHorario = findViewById(R.id.table_horario);
        mProgressCargando = findViewById(R.id.progress_cargando);
        mAccountManager = AccountManager.get(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        mHorarioIndex = getIntent().getIntExtra("HORARIO_INDEX", -1);

        if (mHorarioIndex == -1) {
            finish();
        }

        getToken();

        BarCode barCode = new BarCode(Horario.class.getSimpleName() + "s", getRut());
        try {
            List<Horario> horarios = StoreUtils.provideHorariosStore(this, mToken)
                    .get(barCode)
                    .subscribeOn(Schedulers.io())
                    .blockingGet();
            setHorario(horarios.get(mHorarioIndex));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private HorarioAdapter setHorarioClickListener(HorarioAdapter adapter, final Horario horario) {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int row, int column) {
                Asignatura clickeada = horario.getDatos().get(column - 1).get(row - 1);
                if (clickeada != null) {
                    //Intent intent = new Intent(getContext(), AsignaturaActivity.class);
                    //intent.putExtra("codigo", clickeada.getCodigo() + "/" + clickeada.getSeccion());
                    //startActivity(intent);
                    Toast.makeText(HorarioActivity.this, R.string.pronto_disponible, Toast.LENGTH_SHORT).show();
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

    private void setDialog(Asignatura asignatura) {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_horario_asignatura, null);
        TextView textCodigoAsignatura = view.findViewById(R.id.text_asignatura_codigo);
        TextView textNombreAsignatura = view.findViewById(R.id.text_asignatura_nombre);
        TextView textProfesorAsignatura = view.findViewById(R.id.text_asignatura_profesor);
        TextView textTipoAsignatura = view.findViewById(R.id.text_asignatura_tipo);
        TextView textSeccionAsignatura = view.findViewById(R.id.text_asignatura_seccion);

        textCodigoAsignatura.setText(asignatura.getCodigo());
        textNombreAsignatura.setText(asignatura.getNombre());
        textProfesorAsignatura.setText(asignatura.getDocente().getNombre().getCompleto());
        textTipoAsignatura.setText(asignatura.getTipo());
        textSeccionAsignatura.setText(asignatura.getSeccion().toString());
        mDialogAsignatura = new AlertDialog.Builder(HorarioActivity.this).setView(view).create();
    }

    private void setHorario(Horario horario) {
        HorarioAdapter adapter = new HorarioAdapter(HorarioActivity.this, mRowHeaderList, mColumnHeaderList, horario);
        adapter = setHorarioClickListener(adapter, horario);
        mTableHorario.setAdapter(adapter);
        mTableHorario.setVisibility(View.VISIBLE);
    }

    private void getToken() {
        mAccountManager.getAuthToken(mAccountManager.getAccounts()[0],
                "Bearer", null, this,
                future -> {
                    try {
                        mToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }, null);
    }

    private String getRut() {
        return mAccountManager.getUserData(mAccountManager.getAccounts()[0], LoginActivity.PARAM_USER_RUT);
    }
}
