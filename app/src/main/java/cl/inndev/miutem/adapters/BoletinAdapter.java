package cl.inndev.miutem.adapters;

import java.util.List;

import cl.inndev.miutem.items.NivelItem;
import cl.inndev.miutem.items.SemestreItem;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public class BoletinAdapter extends FlexibleAdapter<SemestreItem> {

    public BoletinAdapter(List<SemestreItem> items, Object listener) {
        super(items, listener);
    }

    @Override
    public void updateDataSet(List<SemestreItem> items, boolean animate) {
        super.updateDataSet(items, animate);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public String onCreateBubbleText(int position) {
        if (position < getScrollableHeaders().size()) {
            return "Top";
        } else if (position >= getItemCount() - getScrollableFooters().size()) {
            return "Bottom";
        } else {
            position -= getScrollableHeaders().size() + 1;
        }
        return super.onCreateBubbleText(position);
    }
}