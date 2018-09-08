package cl.inndev.miutem.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.hbb20.CountryCodePicker;

import cl.inndev.miutem.R;
import cl.inndev.miutem.models.Estudiante;

public class EditarDialog extends Dialog {
    private Toolbar mToolbar;
    private Button mButtonCancelar;
    private Button mButtonGuardar;
    private EditText mEditInfo;
    private CountryCodePicker mCcpCodigo;
    private SaveListener saveListener;
    private Dialog mDialog;

    public EditarDialog(@NonNull final Context context, final Integer index, String valor, SaveListener listener) {
        super(context, R.style.AppTheme);
        this.setContentView(R.layout.dialog_perfil_editar);
        this.saveListener = listener;
        mDialog = this;

        mToolbar = mDialog.getWindow().getDecorView().findViewById(R.id.toolbar);
        mButtonGuardar = mDialog.getWindow().getDecorView().findViewById(R.id.button_guardar);
        mButtonCancelar = mDialog.getWindow().getDecorView().findViewById(R.id.button_cancelar);
        mEditInfo = mDialog.getWindow().getDecorView().findViewById(R.id.edit_info);
        mCcpCodigo = mDialog.getWindow().getDecorView().findViewById(R.id.ccp_codigo);

        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mEditInfo.setText(valor);
        mEditInfo.requestFocus();

        if (index == 6 || index == 7) {
            mEditInfo.setInputType(InputType.TYPE_CLASS_PHONE);
            mEditInfo.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            mCcpCodigo.registerCarrierNumberEditText(mEditInfo);
            mCcpCodigo.setNumberAutoFormattingEnabled(true);

            if (valor != null) {
                Estudiante.Telefono telefono = new Estudiante.Telefono(context, Long.parseLong(valor));
                mEditInfo.setText(telefono.getNumero().toString());
                mCcpCodigo.setCountryForPhoneCode(telefono.getCodigo());
            }

            mButtonGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (saveListener != null)
                        saveListener.onSaveListener(index, mCcpCodigo.getFullNumber());
                    mDialog.dismiss();
                }
            });
        } else {
            mCcpCodigo.setVisibility(View.GONE);

            if (index == 2) {
                mEditInfo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            } else if (index == 12) {
                mEditInfo.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
            }

            mButtonGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (saveListener != null)
                        saveListener.onSaveListener(index, mEditInfo.getText().toString());
                    mDialog.dismiss();
                }
            });
        }

        mButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

    }

    public void setOnSaveListener(SaveListener listener) {
        this.saveListener = listener;
    }

    public interface SaveListener {
        public void onSaveListener(int index, String newValue);
        // public void onCancelListener(String title);
    }

}
