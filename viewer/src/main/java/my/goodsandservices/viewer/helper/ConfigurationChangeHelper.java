package my.goodsandservices.viewer.helper;

import android.os.Bundle;
import android.os.Parcelable;
import my.goodsandservices.viewer.Node;
import my.goodsandservices.viewer.TreeAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A helper class that manages conversion between Goods and Services tree format used in
 * {@link TreeAdapter} and a format needed for the data to be stored in a {@link Bundle}
 * via {@link Bundle#putParcelableArrayList}. The last one format's peculiarity is that
 * objects must implement {@link Parcelable} interface and therefore no links between
 * objects are allowed, so nesting should be provided with any other mechanism.
 * <p/>
 * Using a fragment with <code>setRetainInstance()</code> flag as a data holder
 * isn't a good choice since it will anyway be destroyed when the memory ends up;
 * {@link Parcelable} will not.
 */
public final class ConfigurationChangeHelper {

    private ConfigurationChangeHelper() { /* */ }

    /**
     * Converts multilevel structure to a 'flat' list.
     *
     * @param tree the Goods and Services tree from {@link TreeAdapter}
     * @return the tree unfolded to a single-level list
     */
    public static ArrayList<Node> pack(List<Node> tree) {
        ArrayList<Node> packedTree = new ArrayList<>();

        // Pack only the root nodes since all the others will be packed as children of them
        for (Node node : tree) {
            if (node.level > 0) continue;
            pack(packedTree, node);
        }

        return packedTree;
    }


    private static void pack(ArrayList<Node> packedTree, Node node) {
        packedTree.add(node);

        if (!node.hasSubs()) return;

        for (Node sub : node.subs) {
            pack(packedTree, sub);
        }
    }


    /**
     * Reconstructs the tree structure from a single-level list.
     * <p/>
     * Probably, adding a mandatory unique key like '_id' to {@link Node} structure and
     * using the approach from {@link DBHelper} is the best solution, but for now
     * the code below is the easiest way which completely covers the needs.
     *
     * @param tree the tree given from a {@link Bundle}
     * @return unpacked tree that ready to be used in {@link TreeAdapter}
     */
    public static List<Node> unpack(List<Node> tree) {
        if (tree.isEmpty()) return tree;

        // In case of plain device orientation change event the data is copied via reflection
        // an therefore duplicates exists in a list. Clean up undesired links.
        for (Node node : tree) {
            node.subs = null;
        }

        List<Node> collapsedNodes = new ArrayList<>();
        int scanLevel = 0;
        boolean hasOrphans;

        // Move nodes with level (n) under the nearest node with level (n-1)
        do {
            hasOrphans = false;
            Node potentialParent = null;

            for (Node node : tree) {
                if (node.level < scanLevel) {
                    continue;
                }

                if (node.level == scanLevel) {
                    potentialParent = node;
                    continue;
                }

                if ((node.level == scanLevel + 1) && (potentialParent != null)) {
                    potentialParent.addSub(node);
                    if (!potentialParent.isExpanded()) {
                        collapsedNodes.add(node);
                    }
                    continue;
                }

                hasOrphans = true;
            }

            scanLevel++;

        } while (hasOrphans);

        tree.removeAll(collapsedNodes);

        return tree;
    }
}
