package cl.inndev.miutem.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cl.inndev.miutem.models.UtemAuthenticator;

public class UtemAuthenticatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        UtemAuthenticator authenticator = new UtemAuthenticator(this);
        return authenticator.getIBinder();
    }
}
