package cl.inndev.utem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class PrefManager {

    //public static final String NTF_TIMESTAMP_KEY = "ntf_timestamp_key";
    private static final String PREF_GENERAL = "general_preferences";
    private static final String PREF_CREDENCIALES = "credentials_preferences";
    private static final String PREF_USUARIO = "user_preferences";
    private static final String TIMESTAMP = "_timestamp";
    //private static final long TEN_MINUTES = 1000 * 60 * 10;


    private static Boolean set(Context context, String name, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(name,
                MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    private static Boolean set(Context context, String name, String key, Boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(name,
                MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    private static Boolean set(Context context, String name, String key, Integer value) {
        SharedPreferences prefs = context.getSharedPreferences(name,
                MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    private static Boolean set(Context context, String name, String key, Long value) {
        SharedPreferences prefs = context.getSharedPreferences(name,
                MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    private static Boolean set(Context context, String name, String key, Float value) {
        SharedPreferences prefs = context.getSharedPreferences(name,
                MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }


    private static String getString(Context context,
                                   String name,
                                   String key,
                                   @Nullable String failed) {
        SharedPreferences prefs = context.getSharedPreferences(name,
                MODE_PRIVATE);
        return prefs.getString(key, failed);
    }

    private static Boolean getBool(Context context, String name, String key, Boolean failed) {
        SharedPreferences prefs = context.getSharedPreferences(name,
                MODE_PRIVATE);
        return prefs.getBoolean(key, failed);
    }

    private static Long getLong(Context context, String name, String key, Long failed) {
        SharedPreferences prefs = context.getSharedPreferences(name,
                MODE_PRIVATE);
        return prefs.getLong(key, failed);
    }

    private static Integer getInt(Context context, String name, String key) {
        SharedPreferences prefs = context.getSharedPreferences(name, MODE_PRIVATE);
        if (prefs.contains(key) && prefs.getInt(key, -2) != -2) {
            return prefs.getInt(key, -2);
        }
        return null;
    }

    private static Float getFloat(Context context, String name, String key) {
        SharedPreferences prefs = context.getSharedPreferences(name, MODE_PRIVATE);
        if (prefs.contains(key) && prefs.getFloat(key, -2) != -2) {
            return prefs.getFloat(key, -2);
        }
        return null;
    }



    public static Long getTimeDifference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                MODE_PRIVATE);
        Long delta = System.currentTimeMillis()
                - prefs.getLong(key + TIMESTAMP, 0);
        return delta;
    }

    public static Long getPreferenceTimestamp(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_GENERAL,
                MODE_PRIVATE);
        return prefs.getLong(key + TIMESTAMP, 0);
    }

    public static Boolean setUser(Context context, String key, String value) {
        return set(context, PREF_USUARIO, key, value);
    }

    public static Boolean setUser(Context context, String key, Boolean value) {
        return set(context, PREF_USUARIO, key, value);
    }

    public static Boolean setUser(Context context, String key, Long value) {
        return set(context, PREF_USUARIO, key, value);
    }

    public static Boolean setUser(Context context, String key, Integer value) {
        return set(context, PREF_USUARIO, key, value);
    }

    public static Boolean setUser(Context context, String key, Float value) {
        return set(context, PREF_USUARIO, key, value);
    }

    public static Boolean setPreference(Context context, String key, String value) {
        return set(context, PREF_GENERAL, key, value);
    }

    public static Boolean setPreference(Context context, String key, Boolean value) {
        return set(context, PREF_GENERAL, key, value);
    }

    public static Boolean setPreference(Context context, String key, Long value) {
        return set(context, PREF_GENERAL, key, value);
    }

    public static Boolean setPreference(Context context, String key, Integer value) {
        return set(context, PREF_GENERAL, key, value);
    }

    public static Boolean setCredentials(Context context, String key, String value) {
        return set(context, PREF_CREDENCIALES, key, value);
    }

    public static String getCredentials(Context context, String key) {
        return getString(context, PREF_CREDENCIALES, key, null);
    }

    public static Boolean getBoolPreference(Context context, String key, Boolean failed) {
        return getBool(context, PREF_GENERAL, key, failed);
    }

    public static String getStringPreference(Context context, String key, String failed) {
        return getString(context, PREF_GENERAL, key, failed);
    }

    public static Integer getIntPreference(Context context, String key, Integer failed) {
        return getInt(context, PREF_GENERAL, key);
    }

    public static Long getLongPreference(Context context, String key, Long failed) {
        return getLong(context, PREF_GENERAL, key, failed);
    }

    public static String getStringUser(Context context, String key, String failed) {
        return getString(context, PREF_USUARIO, key, failed);
    }

    public static Long getLongUser(Context context, String key, Long failed) {
        return getLong(context, PREF_USUARIO, key, failed);
    }

    public static Float getFloatUser(Context context, String key) {
        return getFloat(context, PREF_USUARIO, key);
    }

    public static Integer getIntUser(Context context, String key) {
        return getInt(context, PREF_USUARIO, key);
    }



    private static Boolean clear(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(name, MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.clear();
        return editor.commit();
    }

    public static Boolean clearAll(Context context) {
        Boolean r;
        r = clear(context, PREF_GENERAL);
        r = r && clear(context, PREF_CREDENCIALES);
        r = r && clear(context, PREF_USUARIO);
        return r;
    }

}