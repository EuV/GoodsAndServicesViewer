package my.goodsandservices.viewer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public final class PreferencesHelper {
    private static final String TAG = PreferencesHelper.class.getSimpleName();
    private static final String PREFERENCE_CATEGORIES_KEY = "categories_hash";
    private static Activity activity;

    private PreferencesHelper() { /* */ }

    public static void init(Activity a) {
        activity = a;
    }


    public static boolean isAlreadyUpToDate(String rawData) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        int storedHash = preferences.getInt(PREFERENCE_CATEGORIES_KEY, -1);

        if (rawData.hashCode() == storedHash) {
            Log.d(TAG, "Data hasn't been changed since last update");
            return true;
        }

        Log.d(TAG, "Data has been changed");
        return false;
    }


    public static void storeHash(int hash) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCE_CATEGORIES_KEY, hash);
        editor.apply();
        Log.d(TAG, "Data hash code has been stored");
    }


    public static boolean hasDataLocalCopy() {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        int hash = preferences.getInt(PREFERENCE_CATEGORIES_KEY, -1);
        return (hash != -1);
    }
}
