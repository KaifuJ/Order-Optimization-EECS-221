package warehouse;

import java.util.Comparator;

public class BnBCostComparator implements Comparator<BnBNode> {
    public int compare(BnBNode n1, BnBNode n2){
        if (n1.cost < n2.cost) {
            return -1;
        }
        if (n1.cost > n2.cost) {
            return 1;
        }
        if (n1.cost == n2.cost) {
            if (n1.path.size() > n2.path.size()) {
                return -1;
            }
            if (n1.path.size() < n2.path.size()) {
                return 1;
            }
        }
        return 0;
    }
}