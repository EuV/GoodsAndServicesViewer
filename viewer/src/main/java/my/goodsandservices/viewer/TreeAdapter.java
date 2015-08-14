package my.goodsandservices.viewer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


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
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NodeHolder(v);
    }


    @Override
    public void onBindViewHolder(NodeHolder holder, int position) {
        Node node = treeDataProvider.get(position);
        holder.titleView.setText(node.title);
        holder.nodeOnClickListener.setNode(node);
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
        final TextView titleView;
        final NodeOnClickListener nodeOnClickListener = new NodeOnClickListener();

        public NodeHolder(TextView textView) {
            super(textView);
            titleView = textView;
            textView.setOnClickListener(nodeOnClickListener);
        }
    }


    private class NodeOnClickListener implements View.OnClickListener {
        private Node node;

        public void setNode(Node node) {
            this.node = node;
        }

        @Override
        public void onClick(View v) {
            if (!node.hasSubs()) return;

            // Clicking may occur on the element that's actually has already been removed
            // (during animation of its disappearing), which leads to inconsistency of data
            int nodeIndex = treeDataProvider.indexOf(node);
            if (nodeIndex == -1) return;

            if (node.isExpanded()) {
                collapseSubtree(nodeIndex + 1);
            } else {
                expandSubtree(nodeIndex + 1);
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
}
