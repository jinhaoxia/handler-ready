package jinhaoxia.handler.ready.model.request.comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class MapComparator implements Comparator<Map<String, String>> {

    public static final MapComparator MAP_COMPARATOR = new MapComparator();

    @Override
    public int compare(Map<String, String> o1, Map<String, String> o2) {
        SortedSet<String> K = new TreeSet<>(o1.keySet());
        K.addAll(o2.keySet());

        for (String k : K) {
            String v1 = o1.get(k);
            String v2 = o2.get(k);

            if (v1 == null && v2 == null) return 0;
            if (v1 == null) return -1;
            if (v2 == null) return 1;

            int cmp = v1.compareTo(v2);
            if (cmp != 0) return cmp;
        }

        return 0;
    }
}