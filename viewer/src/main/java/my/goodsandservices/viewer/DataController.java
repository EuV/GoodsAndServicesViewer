package my.goodsandservices.viewer;

import android.util.Log;
import org.json.JSONException;

import java.util.List;

public class DataController implements DBHelper.OnLocalDataLoadedListener, HTTPSHelper.OnWebDataLoadedListener {
    private static final String TAG = DataController.class.getSimpleName();
    private static final String URL = "https://money.yandex.ru/api/categories-list";

    private TreeAdapter treeAdapter;

    public DataController(TreeAdapter treeAdapter) {
        this.treeAdapter = treeAdapter;
    }


    public void restore() {
        DBHelper.load(this);
    }


    public void download() {
        HTTPSHelper.load(this, URL);
    }


    @Override
    public void onLocalDataLoaded(List<Node> tree) {
        treeAdapter.setTree(tree);
    }


    @Override
    public void onWebDataLoaded(String rawData) {
        if (rawData == null || rawData.isEmpty()) return;

        if (PreferencesHelper.isAlreadyUpToDate(rawData)) {
            return;
        }

        List<Node> tree;
        try {
            tree = JSONParser.parse(rawData);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse data", e);
            NotificationHelper.showToUser(R.string.failed_to_parse_data);
            return;
        }

        DBHelper.save(tree, rawData);

        treeAdapter.setTree(tree);
    }
}
