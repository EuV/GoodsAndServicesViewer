package my.goodsandservices.viewer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static my.goodsandservices.viewer.TreeAdapter.ListMarkerStates.*;


public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.NodeHolder> {

    /**
     * Data provider for {@link TreeAdapter}.
     * <p/>
     * Its direct contents represent expanded {@link Node}s.
     * Used for fast scrolling of multilevel structure in a single-level RecyclerView.
     * <p/>
     * Since the root nodes are never removed from the list,
     * it also allows to get any node of the tree via {@link Node#subs} references.
     * The tree structure corresponds to the structure of data received from the server.
     */
    private List<Node> treeDataProvider = new ArrayList<>();


    @Override
    public NodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NodeHolder(listItem);
    }


    @Override
    public void onBindViewHolder(NodeHolder holder, int position) {
        Node node = treeDataProvider.get(position);

        // Calculate left indent of list item depending on the nesting level.
        // To simplify calculation the standard one-level indent is stored as 'height' parameter.
        LayoutParams indentParams = holder.indent.getLayoutParams();
        indentParams.width = indentParams.height * node.level;
        holder.indent.setLayoutParams(indentParams);

        int markerState = node.hasSubs() ? (node.isExpanded() ? EXPANDED : COLLAPSED) : EMPTY;
        holder.marker.setImageState(new int[]{markerState}, false);

        holder.title.setText(node.title);

        holder.listItemOnClickListener.setNode(node);
    }


    @Override
    public int getItemCount() {
        return treeDataProvider.size();
    }


    public void setTree(List<Node> tree) {
        this.treeDataProvider = tree;
        notifyDataSetChanged();
    }


    class NodeHolder extends RecyclerView.ViewHolder {
        final View indent;
        final ImageView marker;
        final TextView title;
        final ListItemOnClickListener listItemOnClickListener = new ListItemOnClickListener();

        public NodeHolder(View listItem) {
            super(listItem);
            indent = listItem.findViewById(R.id.list_item_indent);
            marker = (ImageView) listItem.findViewById(R.id.list_item_marker);
            title = (TextView) listItem.findViewById(R.id.list_item_title);
            listItem.setOnClickListener(listItemOnClickListener);
        }
    }


    class ListItemOnClickListener implements View.OnClickListener {
        private Node node;

        public void setNode(Node node) {
            this.node = node;
        }

        @Override
        public void onClick(View listItem) {
            if (!node.hasSubs()) return;

            // Clicking may occur on the element that's actually has already been removed
            // (during animation of its disappearing), which leads to inconsistency of data
            int nodeIndex = treeDataProvider.indexOf(node);
            if (nodeIndex == -1) return;

            ImageView marker = (ImageView) listItem.findViewById(R.id.list_item_marker);

            if (node.isExpanded()) {
                collapseSubtree(nodeIndex + 1);
                marker.setImageState(new int[]{COLLAPSED}, false);
            } else {
                expandSubtree(nodeIndex + 1);
                marker.setImageState(new int[]{EXPANDED}, false);
            }

        }

        private void collapseSubtree(int changeIndex) {
            List<Node> removedNodes = node.collapseSubs();
            treeDataProvider.removeAll(removedNodes);
            notifyItemRangeRemoved(changeIndex, removedNodes.size());
        }

        private void expandSubtree(int changeIndex) {
            List<Node> addedNodes = node.expandSubs();
            treeDataProvider.addAll(changeIndex, addedNodes);
            notifyItemRangeInserted(changeIndex, addedNodes.size());
        }
    }


    static final class ListMarkerStates {
        public static final int EXPANDED = android.R.attr.state_expanded;
        public static final int COLLAPSED = android.R.attr.state_checkable;
        public static final int EMPTY = android.R.attr.state_empty;
    }
}
