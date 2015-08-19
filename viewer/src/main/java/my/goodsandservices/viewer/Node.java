package my.goodsandservices.viewer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Node implements Parcelable {
    public final int level;
    public final Integer id;
    public final String title;
    public List<Node> subs;
    private boolean expanded = false;

    public Node(int level, Integer id, String title, List<Node> subs) {
        this.level = level;
        this.id = id;
        this.title = title;
        this.subs = subs;
    }


    private Node(Parcel src) {
        level = src.readInt();
        Integer i = src.readInt();
        id = (i == -1) ? null : i;
        title = src.readString();
        subs = null;
        expanded = (boolean) src.readValue(null);
    }


    public boolean hasSubs() {
        return subs != null && !subs.isEmpty();
    }


    public void addSub(Node node) {
        if (subs == null) {
            subs = new ArrayList<>();
        }
        subs.add(node);
    }


    public boolean isExpanded() {
        return expanded;
    }


    public List<Node> expandSubs() {
        expanded = true;
        return subs;
    }


    public List<Node> collapseSubs() {
        List<Node> collapsedSubs = new ArrayList<>();
        collapseSubs(this, collapsedSubs);
        return collapsedSubs;
    }


    private void collapseSubs(Node node, List<Node> collapsedSubs) {
        if (!node.expanded) return;

        node.expanded = false;

        if (!node.hasSubs()) return;

        collapsedSubs.addAll(node.subs);

        for (Node sub : node.subs) {
            collapseSubs(sub, collapsedSubs);
        }
    }


    @Override
    public String toString() {
        return "{" + (expanded ? "+" : "-") + "[" + level + "] " + (title == null ? "" : title) + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(level);
        dest.writeInt((id == null) ? -1 : id);
        dest.writeString((title == null) ? "" : title);
        dest.writeValue(expanded);
    }

    public static final Parcelable.Creator<Node> CREATOR = new Parcelable.Creator<Node>() {
        @Override
        public Node createFromParcel(Parcel src) {
            return new Node(src);
        }

        @Override
        public Node[] newArray(int size) {
            return new Node[size];
        }
    };
}
