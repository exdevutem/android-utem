package cl.inndev.miutem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import java.util.Map;

import cl.inndev.miutem.R;

public class CamposAdapter extends BaseAdapter {

    private Context mContext;
    private Map<String, String> mDatos;

    public CamposAdapter(Context context, Map<String, String> listItems) {
        this.mContext = context;
        this.mDatos = listItems;
    }

    @Override
    public int getCount() {
        return mDatos.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int i) {
        int c = 0;
        for (Map.Entry<String, String> entry : mDatos.entrySet()) {
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
        final Map.Entry<String, String> item = this.getItem(position);
        final String[] claves = mContext.getResources().getStringArray(R.array.etiquetas_perfil);
        String extra = "";

        convertView = LayoutInflater.from(mContext).inflate(R.layout.view_campo_perfil, null);

        TextView textEtiqueta = convertView.findViewById(R.id.text_etiqueta);
        TextView textValor = convertView.findViewById(R.id.text_valor);
        final CountryCodePicker ccpPais = convertView.findViewById(R.id.ccp_pais);
        ImageButton buttonEditar = convertView.findViewById(R.id.button_editar);

        if (item.getKey().trim().equals(claves[0]) // RUT
                || item.getKey().trim().equals(claves[1]) // Correo UTEM
                || item.getKey().trim().equals(claves[3])) { // Correo personal
            buttonEditar.setVisibility(View.GONE);
            convertView.setEnabled(false);
            convertView.setOnClickListener(null);
        } else {
            // Números de teléfono
            if (item.getKey().trim().equals(claves[6]) || item.getKey().trim().equals(claves[7])) {
                ccpPais.setVisibility(View.VISIBLE);
                extra = "+";
            }

            if (item.getKey().trim().equals(claves[8])) { // Nacionalidad
                textValor.setVisibility(View.GONE);
                ccpPais.setVisibility(View.VISIBLE);
                ccpPais.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                ccpPais.showFullName(true);

                buttonEditar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ccpPais.launchCountrySelectionDialog();
                    }
                });
            }


            /*buttonEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText editInfo = mDialogEditar.getWindow().getDecorView().findViewById(R.id.edit_info);
                    final Button buttonGuardar = mDialogEditar.getWindow().getDecorView().findViewById(R.id.button_guardar);

                    if (item.getKey().trim().equals(claves[2]) ||
                            item.getKey().trim().equals(claves[6]) ||
                            item.getKey().trim().equals(claves[7]) ||
                            item.getKey().trim().equals(claves[12])) {
                        editInfo.setText(item.getValue().trim());
                        editInfo.setSelectAllOnFocus(true);
                    }
                    if (item.getKey().trim().equals(claves[2])) {
                        editInfo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        buttonGuardar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Estudiante nuevo = new Estudiante();
                                nuevo.setCorreoPersonal(editInfo.getText().toString());
                                actualizarPerfil(nuevo);
                                mDialogEditar.dismiss();
                            }
                        });
                        mDialogEditar.show();
                    } else if (item.getKey().trim().equals(claves[4])) {
                        mDialogSexo.show();
                    } else if (item.getKey().trim().equals(claves[5])) {
                        if (item.getValue() == null) {
                            mDialogFecha = new DatePickerDialog(PerfilActivity.this, dateListener,
                                    mFechaActual.get(Calendar.YEAR) - 17, 0, 1);
                        } else {
                            mDialogFecha = new DatePickerDialog(PerfilActivity.this, dateListener,
                                    mFechaActual.get(Calendar.YEAR) - Integer.valueOf(item.getValue()), 0, 1);
                        }
                        mDialogFecha.setTitle("Selecciona tu fecha de nacimiento");

                        mDialogFecha.show();
                    } else if (item.getKey().trim().equals(claves[6])) {
                        editInfo.setInputType(InputType.TYPE_CLASS_PHONE);
                        buttonGuardar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Estudiante nuevo = new Estudiante();
                                nuevo.setTelefonoMovil(Long.parseLong(editInfo.getText().toString()));
                                actualizarPerfil(nuevo);
                                mDialogEditar.dismiss();
                            }
                        });
                        mDialogEditar.show();
                    } else if (item.getKey().trim().equals(claves[7])) {
                        editInfo.setInputType(InputType.TYPE_CLASS_PHONE);
                        buttonGuardar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Estudiante nuevo = new Estudiante();
                                nuevo.setTelefonoFijo(Long.parseLong(editInfo.getText().toString()));
                                actualizarPerfil(nuevo);
                                mDialogEditar.dismiss();
                            }
                        });
                        mDialogEditar.show();
                    } else if (item.getKey().trim().equals(claves[12])) {
                        editInfo.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                        buttonGuardar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Estudiante nuevo = new Estudiante();
                                nuevo.setDireccion(editInfo.getText().toString());
                                actualizarPerfil(nuevo);
                                mDialogEditar.dismiss();
                            }
                        });
                        mDialogEditar.show();
                    }

                    Button buttonCancelar = mDialogEditar.getWindow().getDecorView().findViewById(R.id.button_cancelar);
                    buttonCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialogEditar.dismiss();
                        }
                    });
                }
            });*/
        }

        textEtiqueta.setText(item.getKey());
        textValor.setText(extra + item.getValue());
        return convertView;
    }
}