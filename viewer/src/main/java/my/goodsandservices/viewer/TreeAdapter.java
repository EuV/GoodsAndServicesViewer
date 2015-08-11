package my.goodsandservices.viewer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.NodeHolder> {
    private List<String> tree = new ArrayList<>();

    public static class NodeHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public NodeHolder(TextView v) {
            super(v);
            textView = v;
        }
    }


    @Override
    public NodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NodeHolder(v);
    }


    @Override
    public void onBindViewHolder(NodeHolder holder, int position) {
        holder.textView.setText(tree.get(position));
    }


    @Override
    public int getItemCount() {
        return tree.size();
    }


    public void setTree(List<String> tree) {
        this.tree = tree;
        notifyDataSetChanged();
    }
}
