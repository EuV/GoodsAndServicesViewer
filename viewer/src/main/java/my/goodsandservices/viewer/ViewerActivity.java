package my.goodsandservices.viewer;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class ViewerActivity extends AppCompatActivity {
    private static final String TAG = "ViewerActivity";
    private static final String URL = "https://money.yandex.ru/api/categories-list";

    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.init(this);
        setContentView(R.layout.activity_viewer);
        resultView = (TextView) findViewById(R.id.text_result);
    }


    public void refreshButtonClickHandler(View view) {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DataLoader(resultView).execute(URL);
        } else {
            Log.d(TAG, "No network  connection");
            Helper.showToUser(R.string.no_network_connection);
        }
    }


    public void resetButtonClickHandler(View view) {
        resultView.setText("");
    }
}
