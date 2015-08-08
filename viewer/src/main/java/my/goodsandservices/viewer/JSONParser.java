package my.goodsandservices.viewer;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JSONParser {
    private static final String TAG = "JSONParser";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBS = "subs";

    private JSONParser() { /* */ }


    public static String parse(String jsonString) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();

        parseNodes(stringBuilder, new JSONArray(jsonString), 0);

        return stringBuilder.toString();
    }


    private static void parseNodes(StringBuilder sb, JSONArray array, int level) throws JSONException {
        Log.d(TAG, "Parse nodes at level " + level);

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            sb.append(new String(new char[level]).replace("\0", "-"));

            if (object.has(KEY_TITLE)) {
                sb.append(object.getString(KEY_TITLE));
            }

            if (object.has(KEY_ID)) {
                sb.append(" [");
                sb.append(object.getString(KEY_ID));
                sb.append("]");
            }

            sb.append("\n");

            if (object.has(KEY_SUBS)) {
                parseNodes(sb, object.getJSONArray(KEY_SUBS), level + 1);
            }
        }
    }
}
