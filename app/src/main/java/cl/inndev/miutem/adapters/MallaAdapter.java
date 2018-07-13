package cl.inndev.miutem.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import cl.inndev.miutem.views.NonScrollListView;

public class MallaAdapter extends BaseAdapter {

    private class NivelAdapter extends BaseAdapter {

        private Context mContext;
        private List<Asignatura> mDatos;

        public NivelAdapter(Context context, List<Asignatura> asignaturas) {
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

            TextView textCodigo = convertView.findViewById(R.id.text_codigo);
            TextView textNombre = convertView.findViewById(R.id.text_nombre);
            TextView textTipo = convertView.findViewById(R.id.text_tipo);
            TextView textEstado = convertView.findViewById(R.id.text_estado);
            TextView textNota = convertView.findViewById(R.id.text_nota);
            TextView textOportunidades = convertView.findViewById(R.id.text_oportunidades);
            View iconoEstado = convertView.findViewById(R.id.icono_estado);
            LinearLayout layoutEstado = convertView.findViewById(R.id.layout_estado);

            Drawable itemBorder = convertView.getBackground();
            itemBorder = DrawableCompat.wrap(itemBorder);

            Drawable rellenoIcono = iconoEstado.getBackground();
            rellenoIcono = DrawableCompat.wrap(rellenoIcono);

            textCodigo.setText(actual.getCodigo());
            textNombre.setText(actual.getNombre());
            textTipo.setText(actual.getTipo());
            textEstado.setText(actual.getEstado());
            textNota.setText(actual.getNota() == null ? null : actual.getNota().toString());
            textOportunidades.setText(actual.getOportunidades() == null ? null :
                    (actual.getOportunidades() == 1 ?
                            actual.getOportunidades().toString() + " vez" :
                            actual.getOportunidades().toString() + " veces"));

            int colorEstado;

            switch (actual.getEstado()) {
                case "Aprobado":
                    colorEstado = mContext.getResources().getColor(R.color.ramo_aprobado_carrera_activity);
                    convertView.setEnabled(false);
                    convertView.setOnClickListener(null);
                    break;
                case "Reprobado":
                    colorEstado = mContext.getResources().getColor(R.color.ramo_reprobado_carrera_activity);
                    convertView.setEnabled(false);
                    convertView.setOnClickListener(null);
                    break;
                case "Inscrito":
                    colorEstado = mContext.getResources().getColor(R.color.ramo_inscrito_carrera_activity);
                    break;
                default:
                    colorEstado = 0;
                    convertView.setEnabled(false);
                    convertView.setOnClickListener(null);
                    convertView.setBackground(new ColorDrawable(0x000000));
                    layoutEstado.setVisibility(View.GONE);
            }

            if (colorEstado != 0) {
                DrawableCompat.setTint(itemBorder, colorEstado);
                convertView.setBackground(itemBorder);
                DrawableCompat.setTint(rellenoIcono, colorEstado);
                iconoEstado.setBackground(rellenoIcono);
                textEstado.setTextColor(colorEstado);
            }

            return convertView;
        }
    }

    private Context mContext;
    private List<Carrera.Nivel> mDatos;

    public MallaAdapter(Context context, List<Carrera.Nivel> malla) {
        this.mContext = context;
        this.mDatos = malla;
    }

    @Override
    public int getCount() {
        return mDatos.size();
    }

    @Override
    public Carrera.Nivel getItem(int i) {
        return mDatos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Carrera.Nivel actual = this.getItem(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.view_carrera_malla_nivel, null);

        TextView textTitulo = convertView.findViewById(R.id.text_nivel);
        NonScrollListView listNivel = convertView.findViewById(R.id.list_asignaturas);
        if (actual.getTitulo() == null || actual.getTitulo().isEmpty()) {
            textTitulo.setText("Sin nivel");
        } else {
            textTitulo.setText("Nivel " + actual.getTitulo());
        }
        listNivel.setAdapter(new NivelAdapter(mContext, actual.getAsignaturas()));

        return convertView;
    }
}