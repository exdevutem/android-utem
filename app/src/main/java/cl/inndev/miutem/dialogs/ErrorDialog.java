package cl.inndev.miutem.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import cl.inndev.miutem.R;

public class ErrorDialog extends AlertDialog {

    private boolean mIsErrorSetted;
    private Context mContext;

    public ErrorDialog(Context context) {
        super(context);
        this.mIsErrorSetted = false;
        this.mContext = context;
        //final Dialog dialog = this;
    }

    @Override
    public void show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_error, null);
        setView(view);
        super.show();
    }

    public void setError() {
        mIsErrorSetted = true;
    }
}
