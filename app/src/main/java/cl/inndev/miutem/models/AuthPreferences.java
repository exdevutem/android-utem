package cl.inndev.miutem.models;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.pixplicity.easyprefs.library.Prefs;

public class AuthPreferences {
    public static final String ACCOUNT_TYPE = "cl.inndev.miutem";
    public static final String AUTHTOKEN_TYPE_TEST = "Bearer";

    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";

    public AuthPreferences(Context context) {
        new Prefs.Builder()
                .setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(context.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public void setUser(String user) {
        Prefs.putString(AccountManager.KEY_ACCOUNT_NAME, user);
    }

    public void setToken(String token) {
        Prefs.putString(AccountManager.KEY_AUTHTOKEN, token);
    }

    public String getUser() {
        return Prefs.getString(AccountManager.KEY_ACCOUNT_NAME, null);
    }

    public String getToken() {
        return Prefs.getString(AccountManager.KEY_AUTHTOKEN, null);
    }
}
