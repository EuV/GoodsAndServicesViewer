package my.goodsandservices.viewer;

import android.util.Log;
import my.goodsandservices.viewer.helper.*;

import java.util.List;

public class DataController implements DBHelper.OnLocalDataLoadedListener, HTTPSHelper.OnWebDataLoadedListener {
    private static final String TAG = DataController.class.getSimpleName();
    private static final String URL = "https://money.yandex.ru/api/categories-list";

    private TreeAdapter treeAdapter;

    public DataController(TreeAdapter treeAdapter) {
        this.treeAdapter = treeAdapter;
    }


    public void restore() {
        Log.d(TAG, "Restoring Goods and Services tree from database...");
        DBHelper.load(this);
    }


    public void download() {
        Log.d(TAG, "Downloading Goods and Services tree...");
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
            NotificationHelper.showToUser(R.string.no_updates);
            return;
        }

        List<Node> tree = JSONHelper.parse(rawData);

        DBHelper.save(tree, rawData);

        treeAdapter.setTree(tree);

        NotificationHelper.showToUser(R.string.list_updated);
    }
}
