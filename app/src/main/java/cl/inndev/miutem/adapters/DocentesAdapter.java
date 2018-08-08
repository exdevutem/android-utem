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

import com.squareup.picasso.Picasso;

import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Docente;
import de.hdodenhof.circleimageview.CircleImageView;

public class DocentesAdapter extends RecyclerView.Adapter<DocentesAdapter.ViewHolder> {

    private List<Docente> mDocentes;

    public DocentesAdapter(List<Docente> docentes) {
        this.mDocentes = docentes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.view_asignatura_docente, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Docente actual = mDocentes.get(position);
        Picasso.get().load("https://sgu.utem.cl/pgai/perfil_foto.php?rut=" + actual.getRut() + "&sexo=1&t_usu=2").into(holder.mImageDocente);
        holder.mTextNombre.setText(actual.getNombre().getCompleto());
        holder.mTextCorreo.setText(actual.getCorreo());
    }

    @Override
    public int getItemCount() {
        return mDocentes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextNombre;
        private TextView mTextCorreo;
        private CircleImageView mImageDocente;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mTextNombre = itemView.findViewById(R.id.text_nombre);
            this.mTextCorreo = itemView.findViewById(R.id.text_correo);
            this.mImageDocente = itemView.findViewById(R.id.image_docente);
        }
    }
}