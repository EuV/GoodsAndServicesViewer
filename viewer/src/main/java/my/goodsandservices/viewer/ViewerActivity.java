package my.goodsandservices.viewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class ViewerActivity extends AppCompatActivity {
    private static final String TAG = ViewerActivity.class.getSimpleName();

    private DataController dataController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        NotificationHelper.init(this);
        PreferencesHelper.init(this);
        HTTPSHelper.init(this);
        DBHelper.init(this);

        RecyclerView treeView = (RecyclerView) findViewById(R.id.tree_view);
        treeView.setLayoutManager(new LinearLayoutManager(this));
        TreeAdapter treeAdapter = new TreeAdapter();
        treeView.setAdapter(treeAdapter);

        dataController = new DataController(treeAdapter);

        if (PreferencesHelper.hasDataLocalCopy()) {
            Log.d(TAG, "Restoring Goods and Services tree from database...");
            dataController.restore();
        } else {
            Log.d(TAG, "Downloading Goods and Services tree...");
            dataController.download();
        }
    }


    public void refresh(View v) {
        dataController.download();
    }
}
