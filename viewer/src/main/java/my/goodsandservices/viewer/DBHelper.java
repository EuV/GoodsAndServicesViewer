package my.goodsandservices.viewer;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import my.goodsandservices.viewer.CategoriesContract.Categories;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    private static Activity activity;

    public static void init(Activity a) {
        activity = a;
    }


    /**
     * Saves Goods and Services tree in a local database.
     * When success, also writes hash code of a raw data string into
     * SharedPreferences to indicate that there's saved data and let
     * quick compare received from the server data with a stored one.
     *
     * @param tree    the categories tree represented as a list of root nodes.
     * @param rawData the {@code string} with raw data received from the server.
     */
    @SuppressWarnings("unchecked")
    public static void save(List<Node> tree, String rawData) {
        if (tree.isEmpty()) return;

        // The only node's field that changes while interaction with a user is
        // expanded-flag, which isn't stored into database. So it is enough only
        // to copy root nodes list to avoid ConcurrentModificationException
        // due to changes of the list while user taps nodes.
        new SaveTask(rawData.hashCode()).execute(new ArrayList<>(tree));
    }


    public static void load(OnLocalDataLoadedListener listener) {
        new LoadTask(listener).execute();
    }


    private static class SaveTask extends AsyncTask<List<Node>, Void, Void> {
        private final int dataHashCode;

        public SaveTask(int dataHashCode) {
            this.dataHashCode = dataHashCode;
        }

        @Override
        @SafeVarargs
        final protected Void doInBackground(List<Node>... trees) {
            SQLiteDatabase db = new CategoriesDBHelper(activity).getWritableDatabase();

            // Due to table truncation the numeration always starts from 1,
            // which allows using array while tree reconstruction
            db.delete(Categories.TABLE_NAME, null, null);
            db.execSQL(CategoriesContract.SQL_RESET_NUMERATION);

            for (Node node : trees[0]) {
                saveCategory(db, node, null);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.d(TAG, "Data has been stored in the database");
            PreferencesHelper.storeHash(dataHashCode);
        }

        private void saveCategory(SQLiteDatabase db, Node node, Long parentId) {
            ContentValues category = new ContentValues();
            category.put(Categories.COLUMN_NAME_LEVEL, node.level);
            category.put(Categories.COLUMN_NAME_ID, node.id);
            category.put(Categories.COLUMN_NAME_TITLE, node.title);
            if (parentId != null) category.put(Categories.COLUMN_NAME_PARENT_ID, parentId);

            long categoryId = db.insert(Categories.TABLE_NAME, null, category);

            if (!node.hasSubs()) return;

            for (Node sub : node.subs) {
                saveCategory(db, sub, categoryId);
            }
        }
    }


    private static class LoadTask extends AsyncTask<Void, Void, List<Node>> {
        private final OnLocalDataLoadedListener listener;

        public LoadTask(OnLocalDataLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<Node> doInBackground(Void... params) {
            SQLiteDatabase db = new CategoriesDBHelper(activity).getReadableDatabase();
            List<Node> tree = new ArrayList<>();
            Cursor cursor = db.query(Categories.TABLE_NAME, Categories.PROJECTION, null, null, null, null, Categories._ID);
            try {
                restoreTree(cursor, tree);
            } finally {
                cursor.close();
            }
            return tree;
        }

        @Override
        protected void onPostExecute(List<Node> tree) {
            Log.d(TAG, "Data has been loaded from the database");
            listener.onLocalDataLoaded(tree);
        }

        private void restoreTree(Cursor cursor, List<Node> tree) {
            if (!cursor.moveToLast()) return;

            // Because of reset numeration while saving data and using sorting in query above,
            // the last _ID also represents the number of categories in the tree
            int total = getInt(cursor, Categories._ID) + 1;
            Node[] nodes = new Node[total];

            cursor.moveToFirst();

            // Due to proper order of saving nodes, 3 steps of tree restoration might be done in a single pass
            do {
                int _id = getInt(cursor, Categories._ID);
                int level = getInt(cursor, Categories.COLUMN_NAME_LEVEL);
                Integer id = getInt(cursor, Categories.COLUMN_NAME_ID);
                String title = getString(cursor, Categories.COLUMN_NAME_TITLE);
                Integer parent_id = getInt(cursor, Categories.COLUMN_NAME_PARENT_ID);

                // A childless node for further use
                nodes[_id] = new Node(level, id, title, null);

                // parent_id may link only to _id of node has already been created
                if (parent_id != null) {
                    nodes[parent_id].addSub(nodes[_id]);
                }

                // Collect root nodes
                if (level == 0) {
                    tree.add(nodes[_id]);
                }

            } while (cursor.moveToNext());
        }

        private Integer getInt(Cursor cursor, String column) {
            int index = cursor.getColumnIndexOrThrow(column);
            return cursor.isNull(index) ? null : cursor.getInt(index);
        }

        private String getString(Cursor cursor, String column) {
            return cursor.getString(cursor.getColumnIndexOrThrow(column));
        }
    }


    public interface OnLocalDataLoadedListener {
        void onLocalDataLoaded(List<Node> tree);
    }
}
