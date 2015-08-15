package my.goodsandservices.viewer;

import android.util.Log;
import org.json.JSONException;

public class DataController implements HTTPSLoader.OnDataLoadedListener {
    private static final String TAG = DataController.class.getSimpleName();
    private static final String URL = "https://money.yandex.ru/api/categories-list";

    private TreeAdapter treeAdapter;

    public DataController(TreeAdapter treeAdapter) {
        this.treeAdapter = treeAdapter;
    }


    public void load() {
        new HTTPSLoader(this).execute(URL);
    }


    @Override
    public void onDataLoaded(String rawData) {
        try {
            treeAdapter.setTree(JSONParser.parse(rawData));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse data", e);
            NotificationHelper.showToUser(R.string.failed_to_parse_data);
        }
    }
}
