package warehouse;

import java.util.ArrayList;

public class Tree {
    class Node {
        int num;
        Node parent;
        ArrayList<Node> children;

        public Node() {
            this.num = -1;
            this.parent = null;
            this.children = new ArrayList<>();
        }
    }

    Node root;
    ArrayList<Node> allNodes;

    public Tree() {
        this.root = new Node();
        this.allNodes = new ArrayList<>();
    }

    public void preOrder(Node root, ArrayList<Node> order) {
        order.add(root);
        for (int i = 0; i < root.children.size(); i++) {
            preOrder(root.children.get(i), order);
        }
    }
}
