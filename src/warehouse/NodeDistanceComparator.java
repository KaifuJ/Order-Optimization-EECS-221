package warehouse;

import java.util.Comparator;

public class NodeDistanceComparator implements Comparator<Warehouse.Node> {
    public int compare(Warehouse.Node n1, Warehouse.Node n2){
        if (n1.distance < n2.distance) {
            return -1;
        }
        if (n1.distance > n2.distance) {
            return 1;
        }
        return 0;
    }
}
