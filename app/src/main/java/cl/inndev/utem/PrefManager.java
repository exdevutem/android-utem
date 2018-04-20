package cl.inndev.utem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefManager {

    public static final String NTF_TIMESTAMP_KEY = "ntf_timestamp_key";
    private static final String PREF_GENERAL = "general_preferences";
    private static final String PREF_CREDENCIALES = "credentials_preferences";
    private static final String PREF_USUARIO = "user_preferences";
    private static final String TIMESTAMP = "_timestamp";
    //private static final long TEN_MINUTES = 1000 * 60 * 10;

    private 

    public static Boolean setPreference(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.putLong(key + TIMESTAMP, System.currentTimeMillis());
        return editor.commit();
    }

    public static Boolean setPreference(Context context, String key, Boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.putLong(key + TIMESTAMP, System.currentTimeMillis());
        return editor.commit();
    }

    public static Boolean setPreference(Context context, String key, Integer value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.putLong(key + TIMESTAMP, System.currentTimeMillis());
        return editor.commit();
    }

    public static Boolean setPreference(Context context, String key, Long value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.putLong(key + TIMESTAMP, System.currentTimeMillis());
        return editor.commit();
    }

    public static Long getTimeDifference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        Long delta = System.currentTimeMillis()
                - prefs.getLong(key + TIMESTAMP, 0);
        return delta;
    }

    public static Long getPreferenceTimestamp(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        return prefs.getLong(key + TIMESTAMP, 0);
    }

    public static String getStringPreference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    public static Long getLongPreference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        return prefs.getLong(key, 0);
    }

    public static Boolean getBoolPreference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                Context.MODE_PRIVATE);
        // default the ntf pref to true
        if (context.getString(R.string.pref_ntf_enabled).equals(key)) {
            return prefs.getBoolean(key, true);
        } else {
            return prefs.getBoolean(key, false);
        }

    }

}