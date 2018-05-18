package cl.inndev.miutem.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.classes.Asignatura;
import cn.zhouchaoyuan.excelpanel.BaseExcelPanelAdapter;
import cn.zhouchaoyuan.excelpanel.ExcelPanel;


public class HorarioFragment extends Fragment {

    private ExcelPanel mPanelHorario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horario, container, false);
        mPanelHorario = view.findViewById(R.id.content_container);

        //HorarioAdapter adapter = new HorarioAdapter(getContext());
        //mPanelHorario.setAdapter(adapter);
        //adapter.setAllData(colTitles, rowTitles, cells);

        return view;
    }

    public void onResume(){
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Horario");

    }

    /*
    public class HorarioAdapter extends BaseExcelPanelAdapter<String, String, Asignatura> {

        private Context context;

        public HorarioAdapter(Context context) {
            super(context);
            this.context = context;
        }

        //=========================================content's cell===========================================
        @Override
        public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_status_normal_cell, parent, false);
            CellHolder cellHolder = new CellHolder(layout);
            return cellHolder;
        }

        @Override
        public void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition) {
            Cell cell = getMajorItem(verticalPosition, horizontalPosition);
            if (null == holder || !(holder instanceof CellHolder) || cell == null) {
                return;
            }
            CellHolder viewHolder = (CellHolder) holder;
            viewHolder.cellContainer.setTag(cell);
            viewHolder.cellContainer.setOnClickListener(blockListener);
            if (cell.getStatus() == 0) {
                viewHolder.bookingName.setText("");
                viewHolder.channelName.setText("");
                viewHolder.cellContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            } else {
                viewHolder.bookingName.setText(cell.getBookingName());
                viewHolder.channelName.setText(cell.getChannelName());
                if (cell.getStatus() == 1) {
                    viewHolder.cellContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.left));
                } else if (cell.getStatus() == 2) {
                    viewHolder.cellContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.staying));
                } else {
                    viewHolder.cellContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.booking));
                }
            }
        }

        static class CellHolder extends RecyclerView.ViewHolder {

            public final TextView bookingName;
            public final TextView channelName;
            public final LinearLayout cellContainer;

            public CellHolder(View itemView) {
                super(itemView);
                bookingName = (TextView) itemView.findViewById(R.id.booking_name);
                channelName = (TextView) itemView.findViewById(R.id.channel_name);
                cellContainer = (LinearLayout) itemView.findViewById(R.id.pms_cell_container);
            }
        }


        //=========================================top cell===========================================
        @Override
        public RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType) {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_status_top_header_item, parent, false);
            TopHolder topHolder = new TopHolder(layout);
            return topHolder;
        }

        @Override
        public void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position) {
            RowTitle rowTitle = getTopItem(position);
            if (null == holder || !(holder instanceof TopHolder) || rowTitle == null) {
                return;
            }
            TopHolder viewHolder = (TopHolder) holder;
            viewHolder.roomWeek.setText(rowTitle.getWeekString());
            viewHolder.roomDate.setText(rowTitle.getDateString());
            viewHolder.availableRoomCount.setText("剩余" + rowTitle.getAvailableRoomCount() + "间");
        }

        static class TopHolder extends RecyclerView.ViewHolder {

            public final TextView roomDate;
            public final TextView roomWeek;
            public final TextView availableRoomCount;

            public TopHolder(View itemView) {
                super(itemView);
                roomDate = (TextView) itemView.findViewById(R.id.data_label);
                roomWeek = (TextView) itemView.findViewById(R.id.week_label);
                availableRoomCount = (TextView) itemView.findViewById(R.id.available_room_count);
            }
        }

        //=========================================left cell===========================================
        @Override
        public RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType) {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_status_left_header_item, parent, false);
            LeftHolder leftHolder = new LeftHolder(layout);
            return leftHolder;
        }

        @Override
        public void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position) {
            ColTitle colTitle = getLeftItem(position);
            if (null == holder || !(holder instanceof LeftHolder) || colTitle == null) {
                return;
            }
            LeftHolder viewHolder = (LeftHolder) holder;
            viewHolder.roomNumberLabel.setText(colTitle.getRoomNumber());
            viewHolder.roomTypeLabel.setText(colTitle.getRoomTypeName());
            ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
            viewHolder.root.setLayoutParams(lp);
        }

        static class LeftHolder extends RecyclerView.ViewHolder {

            public final TextView roomNumberLabel;
            public final TextView roomTypeLabel;
            public final View root;

            public LeftHolder(View itemView) {
                super(itemView);
                root = itemView.findViewById(R.id.root);
                roomNumberLabel = (TextView) itemView.findViewById(R.id.room_number_label);
                roomTypeLabel = (TextView) itemView.findViewById(R.id.room_type_label);
            }
        }

        //=========================================left-top cell===========================================
        @Override
        public View onCreateTopLeftView() {
            return LayoutInflater.from(context).inflate(R.layout.room_status_normal_cell, null);
        }
    } */
}
