package jinhaoxia.support.pattern;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class Group {

    public static final int TYPE_ITEM_SET = 1;
    public static final int TYPE_SEQUENCE = 2;

    public Group() {
    }

    public Group(int type, int[] array) {
        this.type = type;
        this.array = array;
    }

    public int type;

    public int[] array;

    public boolean meet(Group o) {
        if (this.type == TYPE_ITEM_SET) {
            if (o.array == null || o.array.length == 0) return true;
            for (int x : o.array) if (Arrays.binarySearch(this.array, x) == -1) return false;
            return true;
        } else if (this.type == TYPE_SEQUENCE) {
            int pos = 0;
            if (o.array == null || o.array.length == 0) return true;
            for (int x : o.array) {
                pos = ArrayUtils.indexOf(this.array, x, pos);
                if (pos == -1) return false;
            }
            return true;
        }
        return false;
    }

}
