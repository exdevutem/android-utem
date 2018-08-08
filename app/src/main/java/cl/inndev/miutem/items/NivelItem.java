package cl.inndev.miutem.items;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Carrera;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.ExpandableViewHolder;

public class NivelItem extends AbstractExpandableHeaderItem<NivelItem.ViewHolder, AsignaturaItem> {

    private Carrera.Nivel mNivel;

    public NivelItem(Carrera.Nivel nivel) {
        this.mNivel = nivel;
    }

    @Override
    public boolean equals(Object inObject) {
        return this == inObject;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.view_carrera_malla_nivel;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder, int position, List<Object> payloads) {
        if (mNivel.getTitulo() == null || mNivel.getTitulo().isEmpty()) {
            holder.mTextTitulo.setText("Sin nivel");
        } else {
            holder.mTextTitulo.setText("Nivel " + mNivel.getTitulo());
        }
    }

    public class ViewHolder extends ExpandableViewHolder {

        TextView mTextTitulo;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpansion();
                }
            });
            mTextTitulo = view.findViewById(R.id.text_nivel);
        }
    }
}
