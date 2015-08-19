package my.goodsandservices.viewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import my.goodsandservices.viewer.helper.DBHelper;
import my.goodsandservices.viewer.helper.HTTPSHelper;
import my.goodsandservices.viewer.helper.NotificationHelper;
import my.goodsandservices.viewer.helper.PreferencesHelper;

import java.util.List;

public class ViewerActivity extends AppCompatActivity {
    private static final String TAG = ViewerActivity.class.getSimpleName();
    private static final String PACKED_TREE = "packed_tree";

    private DataController dataController;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_viewer);

        initHelpers();

        RecyclerView treeView = (RecyclerView) findViewById(R.id.tree_view);
        treeView.setLayoutManager(new LinearLayoutManager(this));
        TreeAdapter treeAdapter = new TreeAdapter();
        treeView.setAdapter(treeAdapter);

        dataController = new DataController(treeAdapter);

        if (state != null && state.containsKey(PACKED_TREE)) {
            List<Node> packedTree = state.getParcelableArrayList(PACKED_TREE);
            dataController.unpack(packedTree);
            return;
        }


        if (PreferencesHelper.hasDataLocalCopy()) {
            dataController.restore();
        } else {
            dataController.download();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putParcelableArrayList(PACKED_TREE, dataController.pack());
        super.onSaveInstanceState(state);
    }


    public void refresh(View v) {
        dataController.download();
    }


    private void initHelpers() {
        NotificationHelper.init(this);
        PreferencesHelper.init(this);
        HTTPSHelper.init(this);
        DBHelper.init(this);
    }
}
