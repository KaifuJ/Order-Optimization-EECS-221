package warehouse;

import java.util.*;

public class BnBNode {
    int num;
    double[][] matrix;
    ArrayList<Integer> path;
    HashSet<Integer> deletedNodes;
    BnBNode parent;
    double cost;

    public BnBNode(int num, double[][] matrix, ArrayList<Integer> path, HashSet<Integer> deletedNodes, BnBNode parent) {
        this.num = num;
        this.matrix = matrix;
        this.path = path;
        this.deletedNodes = deletedNodes;
        this.parent = parent;
    }

    public double reduceMatrix(){
        if(this.num != -1) { // if the BnBNode is not init or start.
            for (int i = 0; i < matrix.length; i++) {
                this.matrix[this.parent.num][i] = Double.POSITIVE_INFINITY;
            }
            for (int i = 0; i < matrix.length; i++) {
                this.matrix[i][this.num] = Double.POSITIVE_INFINITY;
            }
            for (int i = 0; i < this.path.size(); i++) {
                this.matrix[this.num][path.get(i)] = Double.POSITIVE_INFINITY;
            }
            Iterator<Integer> dnodes = this.deletedNodes.iterator();
            while (dnodes.hasNext()) {
                int current = dnodes.next();
                this.matrix[this.num][current] = Double.POSITIVE_INFINITY;
            }
        }
        int size = matrix.length;
        ArrayList<Double> allMin = new ArrayList<>();

        for (int r = 0; r < size; r++) {
            double min = Double.POSITIVE_INFINITY;
            for (int c = 0; c < size; c++) {
                if (this.matrix[r][c] < min) {
                    min = this.matrix[r][c];
                }
            }
            if (min != 0 && min != Double.POSITIVE_INFINITY) {
                allMin.add(min);
                for (int c = 0; c < size; c++) {
                    this.matrix[r][c] -= min;
                }
            }
        }

        for (int c = 0; c < size; c++) {
            double min = Double.POSITIVE_INFINITY;
            for (int r = 0; r < size; r++) {
                if (this.matrix[r][c] < min) {
                    min = this.matrix[r][c];
                }
            }
            if (min != 0 && min != Double.POSITIVE_INFINITY) {
                allMin.add(min);
                for (int r = 0; r < size; r++) {
                    this.matrix[r][c] -= min;
                }
            }
        }

        double sum = 0;
        for (int i = 0; i < allMin.size(); i++) {
            sum += allMin.get(i);
        }
        return sum;
    }

    public double calCost(){
        this.cost = parent.cost + parent.matrix[parent.num][this.num] + this.reduceMatrix();
        return this.cost;
    }

    public void explore(PriorityQueue<BnBNode> allNodes, int numOfV, ArrayList<Double> upper, ArrayList<Integer>[] finalpath) {
        if (this.path.size() == numOfV / 2 + 1) {
            if (this.cost < upper.get(0)) {
                upper.clear();
                upper.add(this.cost);
                finalpath[0] = this.path;
            }
            return;
        }

        for (int n = 0; n < numOfV; n++) {
            if (!path.contains(n) && !deletedNodes.contains(n)) {
                ArrayList<Integer> newpath = (ArrayList<Integer>) this.path.clone();
                HashSet<Integer> newDeletedNodes = (HashSet<Integer>) this.deletedNodes.clone();
                newpath.add(n);
                if (n % 2 == 0) {
                    if (n != 0) {
                        newDeletedNodes.add(n - 1);
                    }
                } else {
                    newDeletedNodes.add(n + 1);
                }

                double[][] newarray = new double[this.matrix.length][this.matrix.length];
                for (int i = 0; i < newarray.length; i++) {
                    for (int j = 0; j < newarray.length; j++) {
                        newarray[i][j] = this.matrix[i][j];
                    }
                }

                BnBNode newnode = new BnBNode(n, newarray, newpath, newDeletedNodes, this);
                newnode.calCost(); // matrix reduced in this step
                allNodes.add(newnode);
            }
        }
    }
}
