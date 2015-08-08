package my.goodsandservices.viewer;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class DataLoader extends AsyncTask<String, Void, String> {
    private static final String TAG = "DataLoader";
    public static final int CONNECT_TIMEOUT = 15 * 1000;
    public static final int READ_TIMEOUT = 10 * 1000;

    private TextView resultView;

    public DataLoader(TextView resultView) {
        this.resultView = resultView;
    }


    @Override
    protected String doInBackground(String... urls) {
        try {
            return loadData(urls[0]);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load data", e);
            Helper.showToUser(R.string.failed_to_load_data);
            return null;
        }
    }


    @Override
    protected void onPostExecute(String jsonString) {
        String result = null;
        try {
            result = JSONParser.parse(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse data", e);
            Helper.showToUser(R.string.failed_to_parse_data);
        }
        resultView.setText(result);
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
}
