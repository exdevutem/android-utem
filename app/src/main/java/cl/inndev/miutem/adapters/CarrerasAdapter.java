package cl.inndev.miutem.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.models.Carrera;

public class CarrerasAdapter extends BaseAdapter {

    private Context mContext;
    private List<Carrera> mDatos;
    private String[] mAmarillo = {
            "Sin Especificar",
            "Causal de Eliminación",
            "Postergado con Matricula",
            "Transitorio",
            "Morosidad Arancelaria",
            "Congelado y Postergado",
            "Moroso Autorizado Toma Ramo",
            "Congelación automatica",
            "Alumno condicionado a inscrip.",
            "Cambio de regimen",
            "Cambio de Rut"};
    private String[] mRojo = {
            "Suspendido",
            "Eliminado",
            "Abandono Voluntario con Matric",
            "Renunciado",
            "Postergado sin matricula",
            "Abandono Voluntario",
            "Expulsado",
            "Solicitud Rechazada",
            "Egresado Abandono Voluntario",
            "Renuncia al Proceso",
            "Carr no Pertenece a Docencia",
            "Alumno Fallecido",
            "Cambio de Carrera",
            "Cancelacion de Matricula"};
    private String[] mAzul = {
            "Regular",
            "Egresado Matriculado",
            "Egresado",
            "Congelado",
            "Cambio de Plan",
            "Postulante",
            "Titulado",
            "Egreso Pendiente",
            "Seleccionado",
            "Egresado Titulo Intermedio"};

    public CarrerasAdapter(Context context, List<Carrera> carreras) {
        this.mContext = context;
        this.mDatos = carreras;
    }

    @Override
    public int getCount() {
        return mDatos.size();
    }

    @Override
    public Carrera getItem(int i) {
        return mDatos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Carrera actual = this.getItem(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.view_carreras_carrera, null);


        TextView textCodigo = convertView.findViewById(R.id.text_codigo);
        TextView textNombre = convertView.findViewById(R.id.text_nombre);
        TextView textInicio = convertView.findViewById(R.id.text_inicio);
        TextView textEstado = convertView.findViewById(R.id.text_estado);

        textCodigo.setText(actual.getCodigo() + "/" + actual.getPlan());
        textNombre.setText(actual.getNombre());
        textInicio.setText("Desde " + actual.getAnioInicio() + "/" + actual.getSemestreInicio());
        textEstado.setText(actual.getEstado());

        int colorEstado;
        Drawable itemBg = textEstado.getBackground();
        itemBg = DrawableCompat.wrap(itemBg);

        if (contains(mRojo, actual.getEstado())) {
            colorEstado = mContext.getResources().getColor(R.color.carrera_negativo);
        } else if (contains(mAmarillo, actual.getEstado())) {
            colorEstado = mContext.getResources().getColor(R.color.carrera_advertencia);
        } else {
            colorEstado = mContext.getResources().getColor(R.color.carrera_normal);
        }

        DrawableCompat.setTint(itemBg, colorEstado);
        textEstado.setBackground(itemBg);

        return convertView;
    }

    private Boolean contains(String[] array, String q) {
        for (String e : array) {
            if (e.equalsIgnoreCase(q)) {
                return true;
            }
        }
        return false;
    }
}