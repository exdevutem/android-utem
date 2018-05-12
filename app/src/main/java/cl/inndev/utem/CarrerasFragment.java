package cl.inndev.utem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class CarrerasFragment extends Fragment {

    private ListView listAsignaturas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Carreras");
        View view = inflater.inflate(R.layout.fragment_carreras, container, false);

        listAsignaturas = view.findViewById(R.id.asignaturasList);

        ArrayList<Asignatura> list = new ArrayList<>();
        list.add(new Asignatura("Taller De Matemática", "MATC8010", "Aprobado", "Obligatorio", 1, 1, 5.2));

        listAsignaturas.setAdapter(new CarrerasFragment.MallaAdapter(getActivity(), list));

        listAsignaturas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long s) {
                Asignatura item = (Asignatura) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), AsignaturaActivity.class);
                intent.putExtra("codigo",  item.getCodigo());
                startActivity(intent);
            }
        });

        return view;
    }

    private class MallaAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Asignatura> listItems;

        public MallaAdapter(Context context, ArrayList<Asignatura> listItems) {
            this.context = context;
            this.listItems = listItems;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Asignatura getItem(int i) {
            return listItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Asignatura item = getItem(i);

            view = LayoutInflater.from(context).inflate(R.layout.item, null);
            TextView textNota = view.findViewById(R.id.text_nota);
            TextView textEstado = view.findViewById(R.id.text_estado);
            TextView textTipo = view.findViewById(R.id.text_tipo);
            TextView textNombre = view.findViewById(R.id.text_nombre);
            TextView textCodigo = view.findViewById(R.id.text_codigo);

            textNota.setText(item.getNota().toString());
            textEstado.setText(item.getEstado());
            textTipo.setText(item.getTipo());
            textNombre.setText(item.getNombre());
            textCodigo.setText(item.getCodigo());

            return view;
        }
    }
}
