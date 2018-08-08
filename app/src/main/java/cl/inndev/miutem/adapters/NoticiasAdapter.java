package cl.inndev.miutem.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.classes.Noticia;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.ViewHolder> {

    private List<Noticia> noticias;
    private NoticiasListener listener;
    private Context mContext;


    public NoticiasAdapter(Context context, List<Noticia> noticias) {
        this.noticias = noticias;
        this.mContext = context;
        this.listener = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.view_inicio_noticias, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(v, position, noticias.get(position));
            }
        });

        holder.mTextFecha.setText(noticias.get(position).getDateGmt());
        holder.mTextTitulo.setText(noticias.get(position).getTitle().getRendered());
        Picasso.get().load(noticias.get(position).getMedia().getGuid().getRendered()).into(holder.mImageCover);

        Calendar fecha = GregorianCalendar.getInstance();
        String[] meses = mContext.getResources().getStringArray(R.array.meses);
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(noticias.get(position).getDateGmt());
            fecha.setTime(date);
            String fechaEnTexto = fecha.get(Calendar.DAY_OF_MONTH) + " de " +
                    meses[fecha.get(Calendar.MONTH)].toLowerCase() + " de " +
                    fecha.get(Calendar.YEAR);
            holder.mTextFecha.setText(fechaEnTexto);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextTitulo;
        private TextView mTextFecha;
        private ImageView mImageCover;
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mImageCover = mView.findViewById(R.id.image_cover);
            this.mTextTitulo = mView.findViewById(R.id.text_titulo);
            this.mTextFecha = mView.findViewById(R.id.text_fecha);
        }
    }

    public void setOnItemClickListener(NoticiasAdapter.NoticiasListener listener) {
        this.listener = listener;
    }

    public interface NoticiasListener {
        public void onItemClickListener(View view, int position, Noticia noticia);
    }
}
