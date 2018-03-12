package cl.inndev.utem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class AsignaturasFragment extends Fragment {

    String[] name = {"Electromagnetismo", "Cálculo avanzado", "Ecuaciones diferenciales", "EFD: Acondicionamiento físico", "Bases de datos"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Asignaturas");
        View view = inflater.inflate(R.layout.fragment_asignaturas, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("alumno", Context.MODE_PRIVATE);
        String token = preferences.getString("token", "");
        String rut = preferences.getString("rut", "").substring(0, 8);
        ListView asignaturas = (ListView) view.findViewById(R.id.asignaturasList);

        asignaturas.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, name));

        return view;
    }
}
