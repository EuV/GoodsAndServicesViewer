package my.goodsandservices.viewer.helper;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public final class NotificationHelper {
    private static final String TAG = NotificationHelper.class.getSimpleName();
    private static Activity activity;

    private NotificationHelper() { /* */ }

    public static void init(Activity a) {
        activity = a;
    }

    public static void showToUser(final int resId) {
        if (activity == null) {
            Log.e(TAG, "To show messages NotificationHelper should first be initialized with Activity instance");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, activity.getString(resId), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
