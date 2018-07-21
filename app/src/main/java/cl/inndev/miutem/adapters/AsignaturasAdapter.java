package cl.inndev.miutem.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;

public class AsignaturasAdapter extends BaseAdapter {

    private Context mContext;
    private List<Asignatura> mDatos;

    public AsignaturasAdapter(Context context, List<Asignatura> asignaturas) {
        this.mContext = context;
        this.mDatos = asignaturas;
    }

    @Override
    public int getCount() {
        return mDatos.size();
    }

    @Override
    public Asignatura getItem(int i) {
        return mDatos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Asignatura actual = this.getItem(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.view_carrera_malla_asignatura, null);

        LinearLayout layoutDerecha = convertView.findViewById(R.id.layout_derecha);
        TextView textCodigo = convertView.findViewById(R.id.text_codigo);
        TextView textNombre = convertView.findViewById(R.id.text_nombre);
        TextView textTipo = convertView.findViewById(R.id.text_tipo);

        convertView.setBackgroundColor(Color.WHITE);
        layoutDerecha.setVisibility(View.GONE);
        textCodigo.setText(actual.getCodigo());
        textNombre.setText(actual.getNombre());
        textTipo.setText(actual.getTipo());

        return convertView;
    }
}