package cl.inndev.miutem.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.classes.Rut;
import cl.inndev.miutem.dialogs.EditarDialog;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class CamposAdapter extends BaseAdapter {

    private Context mContext;
    private EditarDialog mDialogEditar;
    private AlertDialog mDialogSexo;
    private Map<Integer, String> mDatos;
    private CamposListener mListener;
    private DatePickerDialog mDatePicker;

    public CamposAdapter(Context context, Map<Integer, String> listItems) {
        this.mContext = context;
        this.mDatos = listItems;
        this.mListener = null;
    }

    @Override
    public int getCount() {
        return mDatos.size();
    }

    @Override
    public Map.Entry<Integer, String> getItem(int i) {
        int c = 0;
        for (Map.Entry<Integer, String> entry : mDatos.entrySet()) {
            if (c == i) {
                return entry;
            }
            c++;
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Integer index = this.getItem(position).getKey();
        final String valor = this.getItem(position).getValue();
        String[] etiquetas = mContext.getResources().getStringArray(R.array.etiquetas_perfil);

        convertView = LayoutInflater.from(mContext).inflate(R.layout.view_campo_perfil, null);

        final TextView textValor = convertView.findViewById(R.id.text_valor);
        final CountryCodePicker ccpPais = convertView.findViewById(R.id.ccp_pais);
        TextView textEtiqueta = convertView.findViewById(R.id.text_etiqueta);
        ImageButton buttonEditar = convertView.findViewById(R.id.button_editar);

        DatePickerDialog.OnDateSetListener nacimientoListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Estudiante actualizado = new Estudiante();
                actualizado.setNacimiento(dayOfMonth + "-" + (month + 1) + "-" + year);
                mListener.onEditListener(actualizado);
            }
        };

        CountryCodePicker.OnCountryChangeListener paisListener = new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Estudiante actualizado = new Estudiante();
                Integer codigo = Estudiante.Nacionalidad.codigoAUtem(ccpPais.getSelectedCountryName());
                actualizado.setNacionalidad(new Estudiante.Nacionalidad(codigo));
                mListener.onEditListener(actualizado);
            }
        };

        textEtiqueta.setText(etiquetas[index]);

        if (valor == null) {
            textValor.setText(mContext.getString(R.string.sin_especificar));
        } else {
            textValor.setText(valor);
        }

        final EditarDialog.SaveListener listener = new EditarDialog.SaveListener() {
            @Override
            public void onSaveListener(int index, String newValue) {
                Estudiante actualizado = new Estudiante();
                switch (index) {
                    case 2:
                        actualizado.setCorreoPersonal(newValue);
                        break;
                    case 6:
                        actualizado.setTelefonoMovil(Long.parseLong(newValue));
                        break;
                    case 7:
                        actualizado.setTelefonoMovil(Long.parseLong(newValue));
                        break;
                    case 12:
                        actualizado.setDireccion(newValue);
                        break;
                }
                mListener.onEditListener(actualizado);
            }
        };

        if (index == 0 || index == 1 || index == 3) {
            buttonEditar.setVisibility(View.GONE);
            convertView.setEnabled(false);
            convertView.setOnClickListener(null);
            if (index == 0) {
                textValor.setText(new Rut(Long.parseLong(valor)).getRut(false));
            }
        } else if (index == 2) {
            buttonEditar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EditarDialog(mContext, index, valor, listener).show();
                }
            });
        } else if (index == 4) {
            textValor.setText(new Estudiante.Sexo(Integer.parseInt(valor)).getDescripcion());
            buttonEditar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setSingleChoiceItems(R.array.sexos, Integer.parseInt(valor), null)
                            .setTitle("Selecciona tu sexo")
                            .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int selected = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    Estudiante actualizado = new Estudiante();
                                    actualizado.setSexo(selected);
                                    mListener.onEditListener(actualizado);
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }
            });
        } else if (index == 5) {
            Calendar ahora = Calendar.getInstance();

            mDatePicker = new DatePickerDialog(
                    mContext,
                    nacimientoListener,
                    ahora.get(Calendar.YEAR) - 18,
                    ahora.get(Calendar.MONTH),
                    ahora.get(Calendar.DAY_OF_MONTH));

            if (valor != null) {
                SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
                String[] meses = mContext.getResources().getStringArray(R.array.meses);
                try {
                    Date fecha = parser.parse(valor);
                    Calendar nacimiento = Calendar.getInstance();
                    nacimiento.setTime(fecha);
                    String fechaEnTexto = nacimiento.get(Calendar.DAY_OF_MONTH) + " de " +
                            meses[nacimiento.get(Calendar.MONTH)].toLowerCase() + " de " +
                            nacimiento.get(Calendar.YEAR);

                    textValor.setText(fechaEnTexto);
                    mDatePicker.updateDate(nacimiento.get(Calendar.YEAR),
                            nacimiento.get(Calendar.MONTH),
                            nacimiento.get(Calendar.DAY_OF_MONTH));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            buttonEditar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatePicker.show();
                }
            });
        } else if (index == 6 || index == 7) {

            if (valor != null) {
                ccpPais.setVisibility(View.VISIBLE);
                Estudiante.Telefono telefono = new Estudiante.Telefono(mContext, Long.parseLong(valor));
                textValor.setText(telefono.getNumero().toString());
                ccpPais.setCountryForPhoneCode(telefono.getCodigo());
            }

            buttonEditar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EditarDialog(mContext, index, valor, listener).show();

                }
            });
        } else if (index == 8) {
            textValor.setVisibility(View.GONE);
            ccpPais.setVisibility(View.VISIBLE);
            ccpPais.setShowPhoneCode(false);
            ccpPais.showFullName(true);
            ccpPais.setCcpDialogShowPhoneCode(false);
            ccpPais.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            if (valor != null) {
                ccpPais.setCountryForNameCode(Estudiante.Nacionalidad.utemACodigo(Integer.parseInt(valor)));
            }

            buttonEditar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ccpPais.launchCountrySelectionDialog();
                }
            });

            ccpPais.setOnCountryChangeListener(paisListener);
        } else if (index == 12) {
            buttonEditar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EditarDialog(mContext, index, valor, listener).show();
                }
            });
        }
        return convertView;
    }

    public void setOnEditListener(CamposListener listener) {
        this.mListener = listener;
    }

    public interface CamposListener {
        public void onEditListener(Estudiante nuevo);
    }
}