package my.goodsandservices.viewer.helper;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import my.goodsandservices.viewer.R;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static android.content.Context.CONNECTIVITY_SERVICE;

public final class HTTPSHelper extends AsyncTask<String, Void, String> {
    private static final String TAG = HTTPSHelper.class.getSimpleName();
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    private static final int READ_TIMEOUT = 10 * 1000;

    private static Activity activity;

    private final OnWebDataLoadedListener listener;

    private HTTPSHelper(OnWebDataLoadedListener listener) {
        this.listener = listener;
    }

    public static void init(Activity a) {
        activity = a;
    }


    public static void load(OnWebDataLoadedListener listener, String url) {
        NetworkInfo info = ((ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            new HTTPSHelper(listener).execute(url);
        } else {
            Log.d(TAG, "No network connection");
            NotificationHelper.showToUser(R.string.no_network_connection);
        }
    }


    @Override
    protected String doInBackground(String... urls) {
        try {
            return loadData(urls[0]);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load data", e);
            NotificationHelper.showToUser(R.string.failed_to_load_data);
            return null;
        }
    }


    @Override
    protected void onPostExecute(String rawData) {
        listener.onWebDataLoaded(rawData);
    }


    private String loadData(String url) throws IOException {
        InputStream is = null;

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();

            Log.d(TAG, "The server has responded (" + connection.getResponseCode() + ")");

            is = connection.getInputStream();
            return convertStreamToString(is);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    private String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");

        // Since '\A' points to the beginning of input,
        // the next token will be the entire stream
        return scanner.hasNext() ? scanner.next() : "";
    }


    public interface OnWebDataLoadedListener {
        void onWebDataLoaded(String rawData);
    }
}
