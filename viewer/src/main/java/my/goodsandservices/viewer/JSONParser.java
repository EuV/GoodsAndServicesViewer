package my.goodsandservices.viewer;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JSONParser {
    private static final String TAG = "JSONParser";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBS = "subs";

    private JSONParser() { /* */ }


    public static List<Node> parse(String jsonString) throws JSONException {
        return parseNodes(new ArrayList<Node>(), new JSONArray(jsonString), 0);
    }


    private static List<Node> parseNodes(List<Node> tree, JSONArray rawArray, int level) throws JSONException {
        Log.d(TAG, "Parse nodes at level " + level);

        for (int i = 0; i < rawArray.length(); i++) {
            JSONObject obj = rawArray.getJSONObject(i);

            Integer id = obj.has(KEY_ID) ? obj.getInt(KEY_ID) : null;
            String title = obj.has(KEY_TITLE) ? obj.getString(KEY_TITLE) : null;
            List<Node> subs = obj.has(KEY_SUBS) ? parseNodes(new ArrayList<Node>(), obj.getJSONArray(KEY_SUBS), level + 1) : null;

            tree.add(new Node(level, id, title, subs));
        }

        return tree;
    }
}
