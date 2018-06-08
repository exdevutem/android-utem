package cl.inndev.miutem.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.dialogs.EditarDialog;


public class CertificadosFragment extends Fragment {
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditarDialog mEditarDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mEditarDialog = new EditarDialog(getContext());
        View view = inflater.inflate(R.layout.fragment_certificados, container, false);
        Button buttonDescargar = view.findViewById(R.id.button_descargar);
        Button buttonNoDescargar = view.findViewById(R.id.button_no_descargar);

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
                mEditarDialog.show();
            }
        });

        buttonNoDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set title bar
        mFirebaseAnalytics.setCurrentScreen(getActivity(), CertificadosFragment.class.getSimpleName(),
                CertificadosFragment.class.getSimpleName());
        ((MainActivity) getActivity()).setActionBarTitle("Certificados");

    }


}
