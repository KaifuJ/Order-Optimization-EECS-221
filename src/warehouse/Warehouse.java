package warehouse;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Warehouse {
    private double shelf_x;
    private double shelf_y;
    private double road_x;
    private double road_y;
    private int max_x;
    private int max_y;
    private Node[][] grid;

    class Node{
        protected int x,y;
        protected boolean pass;
        protected double distance;
        protected Node parent;

        public Node(){}
    }

    public Warehouse(double shelf_x, double shelf_y, double road_x, double road_y,
                     int max_x, int max_y){
        this.shelf_x = shelf_x;
        this.shelf_y = shelf_y;
        this.road_x = road_x;
        this.road_y = road_y;
        this.max_x = max_x;
        this.max_y = max_y;
        this.grid = new Node[max_x + 1][max_y + 1];

        this.refreshGrid();
    }

    private void refreshGrid(){
        for(int i = 0; i <= max_x; i++){
            for(int j = 0; j <= max_y; j++){
                grid[i][j] = new Node();
                grid[i][j].x = i;
                grid[i][j].y = j;
                grid[i][j].distance = Double.POSITIVE_INFINITY;
                grid[i][j].parent = null;
                if (i % 2 == 0 && j % 2 == 0) {
                    grid[i][j].pass = false;
                } else {
                    grid[i][j].pass = true;
                }
            }
        }
    }

    public double shortestPath(int[] k_start, int[] k_end){
        int[] start = k_start.clone();
        int[] end = k_end.clone();

        if (start[0] == -1 || end[0] == -1) {
            start[0] += 2;
            end[0] += 2;
        }

        grid[start[0]][start[1]].distance = 0;
        Comparator<Node> comparator = new NodeDistanceComparator();
        int capicity = (this.max_x + 1) * (this.max_y + 1);
        PriorityQueue<Node> pq = new PriorityQueue<Node>(capicity, comparator);
        pq.add(grid[start[0]][start[1]]);

        for (int i = 0; i < capicity; i++) {
            Node current = pq.poll();
            if (current == null) {
                return -1;
            }
            if (current.x == end[0] && current.y == end[1]) {
                break;
            }
            if (current.x + 1 <= max_x
                    && grid[current.x + 1][current.y].pass
                    && grid[current.x + 1][current.y].distance > current.distance + road_x) {
                grid[current.x + 1][current.y].distance = current.distance + road_x;
                grid[current.x + 1][current.y].parent = current;
                pq.remove(grid[current.x + 1][current.y]);
                pq.add(grid[current.x + 1][current.y]);
            }
            if (current.x - 1 >= 0
                    && grid[current.x - 1][current.y].pass
                    && grid[current.x - 1][current.y].distance > current.distance + road_x) {
                grid[current.x - 1][current.y].distance = current.distance + road_x;
                grid[current.x - 1][current.y].parent = current;
                pq.remove(grid[current.x - 1][current.y]);
                pq.add(grid[current.x - 1][current.y]);
            }
            if (current.y + 1 <= max_y
                    && grid[current.x][current.y + 1].pass
                    && grid[current.x][current.y + 1].distance > current.distance + road_y) {
                grid[current.x][current.y + 1].distance = current.distance + road_y;
                grid[current.x][current.y + 1].parent = current;
                pq.remove(grid[current.x][current.y + 1]);
                pq.add(grid[current.x][current.y + 1]);
            }
            if (current.y - 1 >= 0
                    && grid[current.x][current.y - 1].pass
                    && grid[current.x][current.y - 1].distance > current.distance + road_y) {
                grid[current.x][current.y - 1].distance = current.distance + road_y;
                grid[current.x][current.y - 1].parent = current;
                pq.remove(grid[current.x][current.y - 1]);
                pq.add(grid[current.x][current.y - 1]);
            }
        }
        double result = grid[end[0]][end[1]].distance;
        this.refreshGrid();
        return result;
    }

    private void printPath(int[] start, int[] end) {
        ArrayList<Node> list = new ArrayList<Node>();
        Node current = grid[end[0]][end[1]];
        try {
            while (!(current.x == start[0] && current.y == start[1])) {
                list.add(current);
                current = current.parent;
            }
        } catch (NullPointerException e) {
            System.out.println("There is no way to the destination.");
            return;
        }
        list.add(current);
        for (int i = list.size() - 1; i >= 0; i--) {
            if (i != 0) {
                System.out.print("(" + list.get(i).x + "," + list.get(i).y + ")" + "-->");
            } else {
                System.out.println("(" + list.get(i).x + "," + list.get(i).y + ")");
            }
        }
    }

    public double orderShortestPath(double[][] distances, ArrayList<Integer> items, ArrayList<int[]> locations, Map<Integer, Double> weightInfo, Rectangle[][] cells, Path file) {
        // 0 - start point, 1 - end point
        // distances: [start, end, item1-l, item1-r, item2-l, item2-r ...] * [start, end, item1-l, item1-r, item2-l, item2-r ...]
        // items: item1, item2, item3 ...
        // locations: start, end, item1-l, item1-r, item2-l, item2-r ...

        ArrayList<String> output = new ArrayList<>();

        String orOrder = "Original order: ";
        System.out.println("The original order is :");
        for (int i = 0; i < items.size(); i++) {
            System.out.print(items.get(i) + ",");
            orOrder += items.get(i) + ",";
        }
        output.add(orOrder);

//        double defaultDis = 0.0;
//        for (int i = 2; i < distances.length - 1; i++) {
//            defaultDis += distances[i][i + 1];
//        }
//        defaultDis += distances[0][2];
//        defaultDis += distances[distances.length - 1][1];
//        System.out.println("\nThe distance of original order is " + defaultDis);


        if (items.size() <= 10) {
//            System.out.println("\nThe following path is the optimal shortest solution.");
//
//            int numExS = distances.length - 1;
//            Set<Set<Integer>> subsets = new HashSet<Set<Integer>>(1 << (numExS));
//
//            for (long i = 0; i < (1 << (numExS)); i++) { //generate all subsets
//                Set<Integer> subset = new HashSet<>();
//                for (int j = 0; j < numExS; j++) {
//                    if ((i & ((long) 1 << j)) != 0) {
//                        subset.add(j + 1);
//                    }
//                }
//                subsets.add(subset);
//            }
//
//            class Tuple {
//                int dest;
//                Set<Integer> vias;
//
//                @Override
//                public int hashCode() {
//                    return this.dest;
//                }
//
//                @Override
//                public boolean equals(Object tuple) {
//                    Tuple t = (Tuple) tuple;
//                    if (this.dest == t.dest && this.vias.equals(t.vias)) {
//                        return true;
//                    }
//                    return false;
//                }
//            }
//
//            class Result {
//                double dis;
//                int parent;
//            }
//
//            Map<Tuple, Result> maps = new HashMap<>();
//            Iterator<Set<Integer>> allSubsets = subsets.iterator();
//
//            while (allSubsets.hasNext()) { // Iterate through all subsets
//                Set<Integer> currentSet = allSubsets.next();
//                for (int i = 1; i <= numExS; i++) { // for each subset, iterate through all nodes
//                    if (!currentSet.contains(i)) {
//                        double min = Double.POSITIVE_INFINITY;
//                        int parent = -1;
//                        Iterator<Integer> allNodesInSubset = currentSet.iterator();
//                        if (currentSet.size() == 0) {
//                            min = distances[0][i];
//                            parent = 0;
//                        }
//                        while (allNodesInSubset.hasNext()) { //check which node is right before the dest
//                            int currentNode = allNodesInSubset.next();
//                            Tuple t = new Tuple();
//                            t.dest = currentNode;
//                            Set<Integer> newSet = new HashSet<>(currentSet);
//                            newSet.remove(currentNode);
//                            t.vias = newSet;
//                            if (distances[currentNode][i] + maps.get(t).dis < min) {
//                                min = distances[currentNode][i] + maps.get(t).dis;
//                                parent = currentNode;
//                            }
//                        }
//                        Tuple t = new Tuple();
//                        Result r = new Result();
//                        t.dest = i;
//                        t.vias = currentSet;
//                        r.dis = min;
//                        r.parent = parent;
//                        maps.put(t, r);
//                    }
//                }
//            }
//            Tuple t = new Tuple();
//            t.dest = 1;
//            Set<Integer> allVias = new HashSet<>();
//            for (int i = 2; i <= numExS; i++) {
//                allVias.add(i);
//            }
//            t.vias = allVias;
//
//            Set<Tuple> test = maps.keySet();
//
//            int cnode = 1;
//            Set<Integer> cset = new HashSet<>();
//            for (int i = 2; i <= numExS; i++) {
//                cset.add(i);
//            }
//            Stack<Integer> stack = new Stack<>();
//            while (cnode != 0) {
//                stack.push(cnode);
//                Tuple tt = new Tuple();
//                tt.dest = cnode;
//                tt.vias = cset;
//                cnode = maps.get(tt).parent;
//                cset.remove(cnode);
//            }
//            stack.push(0);
//
//            Stack<Integer> copy0 = (Stack<Integer>) stack.clone();
//            Stack<Integer> copy1 = (Stack<Integer>) stack.clone();
//
//            System.out.println("\nOptimized orders:");
//            while (stack.size() > 1) {
//                int c = stack.pop();
//                if (c == 0 || c == 1) {
//                    continue;
//                } else if (stack.size() == 1) {
//                    System.out.println("" + items.get(c - 2) + ",");
//                } else {
//                    System.out.print("" + items.get(c - 2) + ",");
//                }
//            }
//
//            System.out.println("\nOptimized path:");
//            while (copy0.size() > 1) {
//                int c = copy0.pop();
//                System.out.print("(" + locations.get(c)[0] + "," + locations.get(c)[1] + ")" + " --> ");
//            }
//            int _c = copy0.pop();
//            System.out.println("(" + locations.get(_c)[0] + "," + locations.get(_c)[1] + ")\n");
//
//            System.out.println("Optimized Distance: " + maps.get(t).dis);
//
//            double currentWeight = 0;
//            double currentEffort = 0;
//            int before = -1;
//            boolean missWeight = false;
//
//            while (!copy1.empty()) {
//                int c = copy1.pop();
//                if (c == 0) {
//                    before = 0;
//                    continue;
//                }
//                currentEffort += currentWeight * distances[before][c];
//                if (c != 1) {
//                    if (weightInfo.containsKey(items.get(c - 2))) {
//                        currentWeight += weightInfo.get(items.get(c - 2));
//                    } else {
//                        missWeight = true;
//                    }
//                }
//                before = c;
//            }
//
//            System.out.print("Total Effort: " + currentEffort);
//            if (missWeight) {
//                System.out.println(" (missing some weight. It's a lowerbound)");
//            } else {
//                System.out.println();
//            }
//
//            return maps.get(t).dis;

            /***************************************************************/

            double[][] originalMatrix = new double[distances.length - 1][distances.length - 1];
            for (int r = 0; r < distances.length; r++) {
                if (r == 1) {
                    continue;
                }
                for (int c = 0; c < distances.length; c++) {
                    if (c == 1) {
                        continue;
                    }
                    int x = r;
                    int y = c;
                    if (x > 1) {
                        x -= 1;
                    }
                    if (y > 1) {
                        y -= 1;
                    }
                    if (x == y) {
                        originalMatrix[x][y] = Double.POSITIVE_INFINITY;
                    } else {
                        originalMatrix[x][y] = distances[r][c];
                    }
                }
            }

            BnBNode init = new BnBNode(-1, originalMatrix, null, null, null);
            double initC = init.reduceMatrix();

            ArrayList<Integer> path = new ArrayList<>();
            path.add(0);

            HashSet<Integer> deletedNodes = new HashSet<>();

            BnBNode start = new BnBNode(0, init.matrix, path, deletedNodes, null);
            start.cost = initC;

            ArrayList<Double> upperBound = new ArrayList<>(1);
            upperBound.add(Double.POSITIVE_INFINITY);

            Comparator<BnBNode> comparator = new BnBCostComparator();

            PriorityQueue<BnBNode> allNodes = new PriorityQueue<>(comparator);
            allNodes.add(start);
            ArrayList<Integer>[] finalpath = new ArrayList[1];

            try {
                while (allNodes.peek().cost < upperBound.get(0)) {
                    BnBNode current = allNodes.poll();
                    PriorityQueue<BnBNode> allNodes_new = new PriorityQueue<>(2000,comparator);
                    try {
                        current.explore(allNodes, distances.length - 1, upperBound, finalpath);
                    } catch (OutOfMemoryError error) {
                        System.out.println(allNodes.size());
                        for (int i = 0; i < 2000; i++) {
                            allNodes_new.add(allNodes.poll());
                        }
                        allNodes.clear();
                        allNodes = allNodes_new;
                    }
                }
                // finalpath[0]: start, item, item, item ...
            } catch (NullPointerException e) {

            }

            String opOrder = "Optimized order: ";
            System.out.println("\nOptimized order:");
            for (int i = 1; i < finalpath[0].size(); i++) {
                int current = (finalpath[0].get(i) + 1) / 2;
                System.out.print(items.get(current - 1) + ",");
                opOrder += items.get(current - 1) + ",";
            }
            output.add(opOrder);

            String opPath = "Optimized Path: ";
            System.out.println("\nOptimized Path:");
            System.out.print("(" + locations.get(0)[0] + "," + locations.get(0)[1] + ")-->");
            opPath += "(" + locations.get(0)[0] + "," + locations.get(0)[1] + ")-->";
            for (int i = 1; i < finalpath[0].size(); i++) {
                int current = finalpath[0].get(i);
                System.out.print("(" + locations.get(current + 1)[0] + "," + locations.get(current + 1)[1] + ")-->");
                opPath += "(" + locations.get(current + 1)[0] + "," + locations.get(current + 1)[1] + ")-->";
            }
            System.out.println("(" + locations.get(1)[0] + "," + locations.get(1)[1] + ")");
            opPath += "(" + locations.get(1)[0] + "," + locations.get(1)[1] + ")";
            output.add(opPath);

            for (int i = 1; i < finalpath[0].size() - 1; i++) {
                int current = finalpath[0].get(i);
                int next = finalpath[0].get(i + 1);
                this.drawPath(cells, locations.get(current + 1), locations.get(next + 1));
            }
            this.drawPath(cells, locations.get(0), locations.get(finalpath[0].get(1) + 1));
            this.drawPath(cells, locations.get(finalpath[0].get(finalpath[0].size() - 1) + 1), locations.get(1));

            String opDis = "Optimized Distance: ";
            System.out.println("\nOptimized Distance:");
            double dis = 0;
            for (int i = 1; i < finalpath[0].size() - 1; i++) {
                int current = finalpath[0].get(i);
                int next = finalpath[0].get(i + 1);
                current++;
                next++;
                dis += distances[current][next];
            }
            dis += distances[0][finalpath[0].get(1) + 1];
            dis += distances[finalpath[0].get(finalpath[0].size() - 1) + 1][1];
            System.out.println(dis);
            opDis += dis;
            output.add(opDis);

            for (int i = 1; i < finalpath[0].size() - 1; i++) {
                int current = finalpath[0].get(i);
                int next = finalpath[0].get(i + 1);
//                this.drawPath(cells, locations.get(current + 1), locations.get(next + 1));

                output.add(locations.get(current + 1)[0] + "," + locations.get(current + 1)[1]);
                output.add(locations.get(next + 1)[0] + "," + locations.get(next + 1)[1]);
            }
//            this.drawPath(cells, locations.get(0), locations.get(finalpath[0].get(1) + 1));
//            this.drawPath(cells, locations.get(finalpath[0].get(finalpath[0].size() - 1) + 1), locations.get(1));
            output.add(locations.get(0) + "," + locations.get(0)[1]);
            output.add(locations.get(finalpath[0].get(1) + 1)[0] + "," + locations.get(finalpath[0].get(1) + 1)[1]);
            output.add(locations.get(finalpath[0].get(finalpath[0].size() - 1) + 1)[0] + "," + locations.get(finalpath[0].get(finalpath[0].size() - 1) + 1)[1]);
            output.add(locations.get(1)[0] + "," + locations.get(1)[1]);

            output.add("---");
            try {
                Files.write(file, output, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return dis;

        } else {

            int numOfNodes = distances.length;
            ArrayList<Integer> mstSet = new ArrayList<>();
            ArrayList<Integer> deletedNodes = new ArrayList<>();
            double[] value = new double[numOfNodes];
            int[] parent = new int[numOfNodes];

            for (int i = 0; i < numOfNodes; i++) {
                value[i] = Double.POSITIVE_INFINITY;
            }
            value[0] = 0.0;
            while (mstSet.size() != numOfNodes/2) {
                double min = Double.POSITIVE_INFINITY;
                int min_index = -1;
                for (int i = 0; i < numOfNodes; i++) {
                    if (i == 1) {
                        continue;
                    }
                    if (!mstSet.contains(i) && !deletedNodes.contains(i) && value[i] < min) {
                        min = value[i];
                        min_index = i;
                    }
                }
                mstSet.add(min_index);
                if (min_index % 2 == 0) {
                    deletedNodes.add(min_index + 1);
                } else {
                    deletedNodes.add(min_index - 1);
                }

                for (int i = 0; i < numOfNodes; i++) {
                    if (i == 1) {
                        continue;
                    }
                    if (!mstSet.contains(i) && !deletedNodes.contains(i) && distances[min_index][i] < value[i]) {
                        value[i] = distances[min_index][i];
                        parent[i] = min_index;
                    }
                }
            }
            // got MST

//            double lowerbound = 0;
//            for (int i = 0; i < numOfNodes; i++) {
//                if (i == 1) {
//                    continue;
//                }
//                lowerbound += distances[i][parent[i]];
//            }
//            double minToEnd = Double.POSITIVE_INFINITY;
//            for (int i = 0; i < numOfNodes; i++) {
//                if (distances[i][1] > 0 && distances[i][1] < minToEnd) {
//                    minToEnd = distances[i][1];
//                }
//            }
//            lowerbound += minToEnd;
//            System.out.println("\nThe following path is an approximate optimal solution.");
//            System.out.println("The lower bound of distance (MST) is " + lowerbound);

            Tree mst = new Tree();
            for (int i = 0; i < mstSet.size(); i++) {
                Tree.Node node = mst.new Node();
                node.num = mstSet.get(i);
                mst.allNodes.add(node);
            }

            for (int i = 0; i < mstSet.size(); i++) {
                Tree.Node current = mst.allNodes.get(i);
                for (int j = 0; j < mstSet.size(); j++) {
                    if (mst.allNodes.get(j).num == parent[current.num]) {
                        current.parent = mst.allNodes.get(j);
                        mst.allNodes.get(j).children.add(current);
                    }
                }
            }
            mst.root = mst.allNodes.get(0);
            mst.root.parent = null;
            mst.root.children.remove(mst.root);

            ArrayList<Tree.Node> order = new ArrayList<>();
            mst.preOrder(mst.allNodes.get(0), order);
            // order is the result order to pick up items (without end point)
            Tree.Node end = mst.new Node();
            end.num = 1;
            order.add(end);


            String opOrder = "Optimized order: ";
            System.out.println("\nOptimized order:");
            for (int i = 0; i < order.size(); i++) {
                Tree.Node current = order.get(i);
                if (current.num == 0 || current.num == 1) {
                    continue;
                }
                System.out.print(items.get((current.num / 2) - 1) + ",");
                opOrder += items.get((current.num / 2) - 1) + ",";
            }
            System.out.println();
            output.add(opOrder);

            String opPath = "Optimized path: ";
            System.out.println("\nOptimized path:");
            for (int i = 0; i < order.size() - 1; i++) {
                Tree.Node current = order.get(i);
                System.out.print("(" + locations.get(current.num)[0] + "," + locations.get(current.num)[1] + ")-->");
                opPath += "(" + locations.get(current.num)[0] + "," + locations.get(current.num)[1] + ")-->";
            }
            System.out.println("(" + locations.get(order.get(order.size() - 1).num)[0] + "," + locations.get(order.get(order.size() - 1).num)[1] + ")");
            opPath += "(" + locations.get(order.get(order.size() - 1).num)[0] + "," + locations.get(order.get(order.size() - 1).num)[1] + ")";
            output.add(opPath);

            for (int i = 0; i < order.size() - 1; i++) {
                int current = order.get(i).num;
                int next = order.get(i + 1).num;

                this.drawPath(cells, locations.get(current), locations.get(next));
            }

            String opDis = "Optimized Distance: ";
            double dis = 0;
            for (int i = 0; i < order.size() - 1; i++) {
                Tree.Node current = order.get(i);
                Tree.Node next = order.get(i + 1);
                dis += distances[current.num][next.num];
            }
            System.out.println("\nDistance: " + dis);
            opDis += dis;
            output.add(opDis);

            for (int i = 0; i < order.size() - 1; i++) {
                int current = order.get(i).num;
                int next = order.get(i + 1).num;

//                this.drawPath(cells, locations.get(current), locations.get(next));
                output.add(locations.get(current)[0] + "," + locations.get(current)[1]);
                output.add(locations.get(next)[0] + "," + locations.get(next)[1]);
            }

            output.add("---");

            try {
                Files.write(file, output, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }

            double currentWeight = 0;
            double currentEffort = 0;
            boolean missWeight = false;
            for (int i = 0; i < order.size() - 1; i++) {
                int current = order.get(i).num;
                int next = order.get(i + 1).num;
                currentEffort += currentWeight * distances[current][next];
                if (next != 1) {
                    if (weightInfo.containsKey(items.get((next / 2) - 1))) {
                        currentWeight += weightInfo.get(items.get((next / 2) - 1));
                    } else {
                        missWeight = true;
                    }
                }
            }

            System.out.print("Total Effort: " + currentEffort);
            if (missWeight) {
                System.out.println(" (missing some weight. It's a lower bound.)");
            } else {
                System.out.println();
            }
            return dis;

            /*********************************************************************/

//            double[][] originalMatrix = new double[distances.length - 1][distances.length - 1];
//            for (int r = 0; r < distances.length; r++) {
//                if (r == 1) {
//                    continue;
//                }
//                for (int c = 0; c < distances.length; c++) {
//                    if (c == 1) {
//                        continue;
//                    }
//                    int x = r;
//                    int y = c;
//                    if (x > 1) {
//                        x -= 1;
//                    }
//                    if (y > 1) {
//                        y -= 1;
//                    }
//                    if (x == y) {
//                        originalMatrix[x][y] = Double.POSITIVE_INFINITY;
//                    } else {
//                        originalMatrix[x][y] = distances[r][c];
//                    }
//                }
//            }
//
//            BnBNode init = new BnBNode(-1, originalMatrix, null, null);
//            double initC = init.reduceMatrix();
//
//            ArrayList<Integer> path = new ArrayList<>();
//            path.add(0);
//
//            BnBNode start = new BnBNode(0, init.matrix, path, null);
//            start.cost = initC;
//
//            ArrayList<Double> upperBound = new ArrayList<>(1);
//            upperBound.add(Double.POSITIVE_INFINITY);
//
//            Comparator<BnBNode> comparator = new BnBCostComparator();
//
//            PriorityQueue<BnBNode> allNodes = new PriorityQueue<>(comparator);
//            allNodes.add(start);
//            ArrayList<Integer>[] finalpath = new ArrayList[1];
//
//            try {
//                while (allNodes.peek().cost < upperBound.get(0)) {
//                    BnBNode current = allNodes.poll();
//                    try {
//                        current.explore(allNodes, distances.length - 1, upperBound, finalpath);
//                    } catch (OutOfMemoryError error) {
//                        System.out.println(allNodes.size());
//                        System.out.println(error.fillInStackTrace());
//                    }
//
//
//                    if (allNodes.size() > 100000) {
//                        System.out.println(allNodes.size());
//                        PriorityQueue<BnBNode> allNodes_new = new PriorityQueue<>(comparator);
//                        for (int i = 0; i < 2000; i++) {
//                            allNodes_new.add(allNodes.poll());
//                        }
//                        allNodes.clear();
//                        allNodes = allNodes_new;
//                    }
//                }
//                // finalpath[0]: start, item, item, item ...
//            } catch (NullPointerException e) {
//
//            }
//
//            System.out.println("\nOptimized order:");
//            for (int i = 1; i < finalpath[0].size(); i++) {
//                int current = finalpath[0].get(i);
//                System.out.print(items.get(current - 1) + ",");
//            }
//
//            System.out.println("\nOptimized Path:");
//            System.out.print("(" + locations.get(0)[0] + "," + locations.get(0)[1] + ")-->");
//            for (int i = 1; i < finalpath[0].size(); i++) {
//                int current = finalpath[0].get(i);
//                System.out.print("(" + locations.get(current + 1)[0] + "," + locations.get(current + 1)[1] + ")-->");
//            }
//            System.out.println("(" + locations.get(1)[0] + "," + locations.get(1)[1] + ")");
//
//            System.out.println("\nOptimized Distance:");
//            double dis = 0;
//            for (int i = 1; i < finalpath[0].size() - 1; i++) {
//                int current = finalpath[0].get(i);
//                int next = finalpath[0].get(i + 1);
//                current++;
//                next++;
//                dis += distances[current][next];
//            }
//            dis += distances[0][finalpath[0].get(1) + 1];
//            dis += distances[finalpath[0].get(finalpath[0].size() - 1) + 1][1];
//            System.out.println(dis);
//
//            return dis;

        }
    }

    public void drawPath(Rectangle[][] cells, int[] k_start, int[] k_end) {
        int[] start = k_start.clone();
        int[] end = k_end.clone();

        if (start[0] == -1 || end[0] == -1) {
            start[0] += 2;
            end[0] += 2;
        }

        grid[start[0]][start[1]].distance = 0;
        Comparator<Node> comparator = new NodeDistanceComparator();
        int capicity = (this.max_x + 1) * (this.max_y + 1);
        PriorityQueue<Node> pq = new PriorityQueue<Node>(capicity, comparator);
        pq.add(grid[start[0]][start[1]]);

        for (int i = 0; i < capicity; i++) {
            Node current = pq.poll();
            if (current == null) {
                return;
            }
            if (current.x == end[0] && current.y == end[1]) {
                break;
            }
            if (current.x + 1 <= max_x
                    && grid[current.x + 1][current.y].pass
                    && grid[current.x + 1][current.y].distance > current.distance + road_x) {
                grid[current.x + 1][current.y].distance = current.distance + road_x;
                grid[current.x + 1][current.y].parent = current;
                pq.remove(grid[current.x + 1][current.y]);
                pq.add(grid[current.x + 1][current.y]);
            }
            if (current.x - 1 >= 0
                    && grid[current.x - 1][current.y].pass
                    && grid[current.x - 1][current.y].distance > current.distance + road_x) {
                grid[current.x - 1][current.y].distance = current.distance + road_x;
                grid[current.x - 1][current.y].parent = current;
                pq.remove(grid[current.x - 1][current.y]);
                pq.add(grid[current.x - 1][current.y]);
            }
            if (current.y + 1 <= max_y
                    && grid[current.x][current.y + 1].pass
                    && grid[current.x][current.y + 1].distance > current.distance + road_y) {
                grid[current.x][current.y + 1].distance = current.distance + road_y;
                grid[current.x][current.y + 1].parent = current;
                pq.remove(grid[current.x][current.y + 1]);
                pq.add(grid[current.x][current.y + 1]);
            }
            if (current.y - 1 >= 0
                    && grid[current.x][current.y - 1].pass
                    && grid[current.x][current.y - 1].distance > current.distance + road_y) {
                grid[current.x][current.y - 1].distance = current.distance + road_y;
                grid[current.x][current.y - 1].parent = current;
                pq.remove(grid[current.x][current.y - 1]);
                pq.add(grid[current.x][current.y - 1]);
            }
        }

        Node current = grid[end[0]][end[1]];
        ArrayList<int[]> path = new ArrayList<>();
        int[] loca = {20 - current.y,  current.x + 1};
        path.add(loca);
        while (current.parent != null) {
            current = current.parent;
            int[] loca1 = {20 - current.y, current.x + 1};
            path.add(loca1);
        }

        if (k_start[0] == -1 || k_end[0] == -1) {
            for (int[] i : path) {
                if (cells[i[0]][i[1] - 2].getFill() == Color.GREEN) {

                } else {
                    cells[i[0]][i[1] - 2].setFill(Color.YELLOW);
                }
            }
        }else{
            for (int[] i : path) {
                if (cells[i[0]][i[1]].getFill() == Color.GREEN) {

                } else {
                    cells[i[0]][i[1]].setFill(Color.YELLOW);
                }
            }
        }

        if (k_start[0] == -1 || k_end[0] == -1) {
            cells[path.get(0)[0]][path.get(0)[1] - 2].setFill(Color.GREEN);
            cells[path.get(path.size() - 1)[0]][path.get(path.size() - 1)[1] - 2].setFill(Color.GREEN);
        } else {
            cells[path.get(0)[0]][path.get(0)[1]].setFill(Color.GREEN);
            cells[path.get(path.size() - 1)[0]][path.get(path.size() - 1)[1]].setFill(Color.GREEN);
        }

        this.refreshGrid();
    }

    public ArrayList<ArrayList<Integer>> ordersReorganize(ArrayList<ArrayList<Integer>> orders, Map<Integer, Double> weightInfo, double weightBound) {
        // -1 split
        // null single order
        // 1 combine

        int orderNo = 1;
        double[] orderWeight = new double[orders.size()];
        ArrayList<ArrayList<Integer>> orderStatus = new ArrayList<>(orders.size());

        for (ArrayList<Integer> order : orders) {
            double totalWeight = 0;
            for (int item : order) {
                if (weightInfo.containsKey(item)) {
                    totalWeight += weightInfo.get(item);
                }
            }
            orderWeight[orderNo - 1] = totalWeight;
            orderNo++;
        }

        for (int i = 0; i < orders.size(); i++) {
            orderStatus.add(new ArrayList<>());
        }

        for (int i = 0; i < orders.size(); i++) {
            if(orderStatus.get(i).size() == 0) {
                if (orderWeight[i] > weightBound) {
                    orderStatus.get(i).add(-1);
                    int splitPoint = 0;
                    double tempWeight = 0;
                    for (int item : orders.get(i)) {
                        if (weightInfo.containsKey(item)) {
                            tempWeight += weightInfo.get(item);
                        }
                        splitPoint++;
                        if (tempWeight > weightBound) {
                            orderStatus.get(i).add(splitPoint - 1);
                            tempWeight = weightInfo.get(item);
                        }
                    }
                }

                if (orderWeight[i] == weightBound) {
                    orderStatus.set(i, null);
                }

                if (orderWeight[i] < weightBound) {
                    boolean combine = false;

                    double tempWeight = orderWeight[i];
                    for (int j = i + 1; j < orders.size(); j++) {
                        if (tempWeight + orderWeight[j] < weightBound) {
                            if (combine == false) {
                                orderStatus.get(i).add(1);
                                orderStatus.get(i).add(i);
                                combine = true;
                            }
                            tempWeight += orderWeight[j];
                            orderStatus.get(i).add(j);
                        }
                    }
                    if (combine == true) {
                        for (int x = 2; x < orderStatus.get(i).size(); x++) {
                            orderStatus.set(orderStatus.get(i).get(x), orderStatus.get(i));
                        }
                    } else {
                        orderStatus.set(i, null);
                    }
                }
            }
        }
        return orderStatus;
    }
}














