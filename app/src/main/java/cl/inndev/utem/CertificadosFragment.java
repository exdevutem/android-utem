package cl.inndev.utem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class CertificadosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setActionBarTitle("Certificados");
        View view = inflater.inflate(R.layout.fragment_certificados, container, false);
        Button buttonDescargar = view.findViewById(R.id.button_descargar);

        final AlertDialog.Builder dialogMotivo = new AlertDialog.Builder(getActivity());

        dialogMotivo.setTitle("Seleccione el motivo");
        dialogMotivo.setSingleChoiceItems(R.array.motivos_certificados, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getActivity(), "Hola", Toast.LENGTH_SHORT).show();
                    }
                });

        buttonDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMotivo.show();
            }
        });

        return view;
    }

    public void onResume(){
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Inicio");

    }
}
