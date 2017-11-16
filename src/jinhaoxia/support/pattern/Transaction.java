package jinhaoxia.support.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transaction extends ArrayList<Group> {

    public Transaction() {
        super();
    }

    public Transaction(List<Group> groups) {
        super(groups);
    }

    public Transaction tailTransaction() {
        if (this.size() <= 1) return new Transaction();
        return new Transaction(this.subList(1, this.size()));
    }

    public Group headGroup() {
        if (this.size() == 0) return null;
        return this.get(0);
    }

    List<Integer> getGroupTypes() {
        if (this.size() == 0) return Collections.emptyList();
        ArrayList<Integer> types = new ArrayList<>();
        for (Group grp : this) {
            types.add(grp.type);
        }
        return types;
    }

}
