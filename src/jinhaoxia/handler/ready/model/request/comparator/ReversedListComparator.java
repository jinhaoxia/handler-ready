package jinhaoxia.handler.ready.model.request.comparator;

import java.util.Comparator;
import java.util.List;

public class ReversedListComparator implements Comparator<List<String>> {

    public static final ReversedListComparator REVERSED_LIST_COMPARATOR = new ReversedListComparator();

    @Override
    public int compare(List<String> o1, List<String> o2) {

        if (o1.size() < o2.size()) {
            return -1;
        } else if (o2.size() < o1.size()) {
            return 1;
        }

        int n = o1.size();

        for (int i = 0; i < n; i++) {
            String s1 = o1.get(o1.size() - i - 1);
            String s2 = o2.get(o2.size() - i - 1);

            if (s1 == null && s2 == null) continue;
            if (s1 == null) return 1;
            if (s2 == null) return -1;

            int cmp = s1.compareTo(s2);
            if (cmp != 0) return cmp;
        }

        return 0;
    }
}

