package cl.inndev.miutem.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;

import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Estudiante;

public class HorarioAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> {
    private final int[] COLORS = new int[]{
            0xffe62a10, 0xffe91e63, 0xff9c27b0, 0xff673ab7, 0xff3f51b5,
            0xff5677fc, 0xff03a9f4, 0xff00bcd4, 0xff009688, 0xff259b24,
            0xff8bc34a, 0xffcddc39, 0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722};

    private final LayoutInflater mLayoutInflater;

    private final List<List<Asignatura>> mTableData;
    private final List<String> mRowHeaderData;
    private final List<String> mColumnHeaderData;
    private final int mColumnWidth;
    private final int mRowHeight;
    private final int mColumnHeaderHeight;
    private final int mRowHeaderWidth;

    public HorarioAdapter(Context context, List<String> rh, List<String> ch, Estudiante.Horario c) {
        mLayoutInflater = LayoutInflater.from(context);
        mTableData = c.getDatos();
        mRowHeaderData = rh;
        mColumnHeaderData = ch;
        Resources res = context.getResources();
        mColumnWidth = res.getDimensionPixelSize(R.dimen.column_width);
        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);
        mColumnHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);
        mRowHeaderWidth = res.getDimensionPixelSize(R.dimen.row_header_width);
    }

    @Override
    public int getRowCount() {
        return mRowHeaderData.size() + 1;
    }

    @Override
    public int getColumnCount() { return mColumnHeaderData.size() + 1; }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull ViewGroup parent) {
        return new TestViewHolder(mLayoutInflater.inflate(R.layout.item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.item_header, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.item_header, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.item_header, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        final TestViewHolder vh = (TestViewHolder) viewHolder;
        Asignatura itemData = mTableData.get(column - 1).get(row - 1);
        int color = COLORS[column % COLORS.length];

        if (itemData != null) {
            vh.container.setBackgroundColor(color);
            vh.textNombreAsignatura.setText(itemData.getNombre());
            vh.textSalaAsignatura.setText(itemData.getSala());
            vh.textSalaAsignatura.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount =  vh.textSalaAsignatura.getLineCount();
                    vh.textTipoAsignatura.setText("" + lineCount);
                }
            });
            // vh.textTipoAsignatura.setText(itemData.getTipo());
        } else {
            vh.container.setBackgroundColor(Color.WHITE);
            vh.textNombreAsignatura.setText(null);
            vh.textSalaAsignatura.setText(null);
            vh.textTipoAsignatura.setText(null);
        }
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
        vh.tvText.setText(mColumnHeaderData.get(column - 1));
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
        vh.tvText.setText(mRowHeaderData.get(row - 1));
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
        vh.tvText.setText("");
    }

    @Override
    public int getColumnWidth(int column) {
        return mColumnWidth;
    }

    @Override
    public int getHeaderColumnHeight() {
        return mColumnHeaderHeight;
    }

    @Override
    public int getRowHeight(int row) {
        return mRowHeight;
    }

    @Override
    public int getHeaderRowWidth() {
        return mRowHeaderWidth;
    }

    //------------------------------------- view holders ------------------------------------------

    private class TestViewHolder extends ViewHolderImpl {
        TextView textNombreAsignatura;
        TextView textTipoAsignatura;
        TextView textSalaAsignatura;
        LinearLayout container;

        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombreAsignatura = itemView.findViewById(R.id.text_asignatura_nombre);
            textTipoAsignatura = itemView.findViewById(R.id.text_asignatura_tipo);
            textSalaAsignatura = itemView.findViewById(R.id.text_asignatura_sala);
            container = itemView.findViewById(R.id.container);
        }
    }

    private class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
        }
    }

    private class TestHeaderRowViewHolder extends ViewHolderImpl {
        TextView tvText;

        TestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
        }
    }

    private class TestHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
        }
    }
}