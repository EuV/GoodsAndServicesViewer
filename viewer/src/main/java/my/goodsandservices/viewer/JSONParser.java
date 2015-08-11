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


    public static List<String> parse(String jsonString) throws JSONException {
        List<String> tree = new ArrayList<>();

        parseNodes(tree, new JSONArray(jsonString), 0);

        return tree;
    }


    private static void parseNodes(List<String> tree, JSONArray array, int level) throws JSONException {
        Log.d(TAG, "Parse nodes at level " + level);

        for (int i = 0; i < array.length(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(new String(new char[level]).replace("\0", "-"));

            JSONObject object = array.getJSONObject(i);

            if (object.has(KEY_TITLE)) {
                sb.append(object.getString(KEY_TITLE));
            }

            if (object.has(KEY_ID)) {
                sb.append(" [");
                sb.append(object.getString(KEY_ID));
                sb.append("]");
            }

            tree.add(sb.toString());

            if (object.has(KEY_SUBS)) {
                parseNodes(tree, object.getJSONArray(KEY_SUBS), level + 1);
            }
        }
    }
}
