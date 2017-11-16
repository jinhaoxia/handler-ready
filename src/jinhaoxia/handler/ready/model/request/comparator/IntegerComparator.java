package jinhaoxia.handler.ready.model.request.comparator;

import java.util.Comparator;

public class IntegerComparator implements Comparator<Integer> {
    public  static  final IntegerComparator INTEGER_COMPARATOR = new IntegerComparator();
    @Override
    public int compare(Integer o1, Integer o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        return o1.compareTo(o2);
    }
}
