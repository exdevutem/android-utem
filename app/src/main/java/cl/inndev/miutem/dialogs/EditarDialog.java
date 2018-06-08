package cl.inndev.miutem.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import cl.inndev.miutem.R;

public class EditarDialog extends Dialog {

    private FirebaseAnalytics mFirebaseAnalytics;
    Button mButtonCancelar;
    Button mButtonGuardar;

    public EditarDialog(@NonNull final Context context) {
        super(context, R.style.AppTheme);
        this.setContentView(R.layout.dialog_perfil_editar);
        final Dialog dialog = this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mButtonGuardar = dialog.getWindow().getDecorView().findViewById(R.id.button_guardar);
        mButtonCancelar = dialog.getWindow().getDecorView().findViewById(R.id.button_cancelar);

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mButtonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

}
