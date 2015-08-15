package my.goodsandservices.viewer;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class ViewerActivity extends AppCompatActivity {
    private static final String TAG = ViewerActivity.class.getSimpleName();

    private TreeAdapter treeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationHelper.init(this);
        setContentView(R.layout.activity_viewer);

        RecyclerView treeView = (RecyclerView) findViewById(R.id.tree_view);
        treeView.setLayoutManager(new LinearLayoutManager(this));
        treeAdapter = new TreeAdapter();
        treeView.setAdapter(treeAdapter);
    }


    public void refreshButtonClickHandler(View view) {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DataController(treeAdapter).load();
        } else {
            Log.d(TAG, "No network connection");
            NotificationHelper.showToUser(R.string.no_network_connection);
        }
    }
}
