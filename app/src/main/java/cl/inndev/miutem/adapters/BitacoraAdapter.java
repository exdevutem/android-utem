package cl.inndev.miutem.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.models.Asignatura;

public class BitacoraAdapter extends RecyclerView.Adapter<BitacoraAdapter.ViewHolder> {

    private List<Asignatura.Registro> mBitacora;
    private Context mContext;

    public BitacoraAdapter(Context context, List<Asignatura.Registro> notas) {
        this.mBitacora = notas;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.view_asignatura_bitacora, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Asignatura.Registro actual = mBitacora.get(position);
        holder.mTextFecha.setText(actual.getFecha());
        holder.mTextBloque.setText("" + actual.getPeriodo().getNumero());
        holder.mTextNumero.setText(actual.getNumero().toString());
        if (actual.getRegistrado()) {
            if (actual.getSuspendido()) {
                holder.mImageIcono.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_block));
                holder.mImageIcono.setColorFilter(mContext.getResources().getColor(R.color.red));
            } else if (actual.getAsistencia()) {
                holder.mImageIcono.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_done));
                holder.mImageIcono.setColorFilter(mContext.getResources().getColor(R.color.green));
            } else {
                holder.mImageIcono.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_close));
                holder.mImageIcono.setColorFilter(mContext.getResources().getColor(R.color.red));
            }
        } else {
            holder.mImageIcono.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_help));
            holder.mImageIcono.setColorFilter(mContext.getResources().getColor(R.color.blue));
        }
    }

    @Override
    public int getItemCount() {
        return mBitacora.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextFecha;
        private TextView mTextBloque;
        private TextView mTextNumero;
        private ImageView mImageIcono;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mTextFecha = itemView.findViewById(R.id.text_fecha);
            this.mTextBloque = itemView.findViewById(R.id.text_bloque);
            this.mTextNumero = itemView.findViewById(R.id.text_numero);
            this.mImageIcono = itemView.findViewById(R.id.image_icono);
        }
    }
}