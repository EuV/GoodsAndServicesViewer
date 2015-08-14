package my.goodsandservices.viewer;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public final int level;
    public final Integer id;
    public final String title;
    private final List<Node> subs;
    private boolean expanded = false;

    public Node(int level, Integer id, String title, List<Node> subs) {
        this.level = level;
        this.id = id;
        this.title = new String(new char[level]).replace("\0", "-") + title;
        this.subs = subs;
    }


    public boolean hasSubs() {
        return subs != null && !subs.isEmpty();
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
        return "{" + (expanded ? "+" : "-") + "[" + level + "] " + title + "}";
    }
}
