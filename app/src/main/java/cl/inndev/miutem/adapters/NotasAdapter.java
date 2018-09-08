package cl.inndev.miutem.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.models.Asignatura;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder> {

    private List<Asignatura.Nota> mNotas;
    private OnNotaKeyListener mListener;

    public NotasAdapter(List<Asignatura.Nota> notas) {
        this.mNotas = notas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.view_asignatura_nota, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Asignatura.Nota actual = mNotas.get(position);
        holder.mTextDescripcion.setText(actual.getTipo());
        if (actual.getPonderador() != null) {
            Double porcentaje = actual.getPonderador() * 100;
            holder.mTextPonderacion.setText(porcentaje.intValue() + "%");
        }

        if (actual.getNota() != null) {
            holder.mEditNota.setText(actual.getNota().toString());
            holder.mEditNota.setEnabled(false);
            holder.mEditNota.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.mEditNota.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                char unicode = (char) event.getUnicodeChar();
                String valor = holder.mEditNota.getText().toString() + unicode;
                Log.d("WATCHER", "Valor: " + valor);
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return false;
                }
                if (valor.length() > 3) {
                    return true;
                }
                if (valor != null && !valor.isEmpty() && (Double.parseDouble(valor) > 7 ) ||
                        Double.parseDouble(valor.toString()) < 0) {
                    return true;
                }
                return false;
            }
        });

        holder.mEditNota.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mListener.onKey(position, charSequence, actual.getPonderador());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


    }

    @Override
    public int getItemCount() {
        return mNotas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextDescripcion;
        private TextView mTextPonderacion;
        private EditText mEditNota;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mTextDescripcion = itemView.findViewById(R.id.text_descripcion);
            this.mTextPonderacion = itemView.findViewById(R.id.text_ponderacion);
            this.mEditNota = itemView.findViewById(R.id.edit_nota);
        }
    }

    public void setNotaKeyListener(OnNotaKeyListener listener) {
        this.mListener = listener;
    }

    public interface OnNotaKeyListener {
        void onKey(int i, CharSequence charSequence, Double ponderador);
    }
}