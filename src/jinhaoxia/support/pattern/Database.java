package jinhaoxia.support.pattern;

import java.util.*;

public class Database extends LinkedList<Transaction> {

    List<Integer> getGroupTypes() {
        if (this.size() == 0) return Collections.emptyList();
        return this.get(0).getGroupTypes();
    }

    Integer headGroupType() {
        if (this.size() == 0) return null;
        if (this.get(0).size() == 0) return null;
        return this.get(0).get(0).type;
    }

    List<Group> headGroups() {
        if (this.headGroupType() == null) return Collections.emptyList();

        ArrayList<Group> groups = new ArrayList<>(this.size());

        for (Transaction tran : this) {
            groups.add(tran.headGroup());
        }

        return groups;
    }

    public Database() {
        super();
    }

    public Database(List<Transaction> database) {
        super(database);
    }

    public Database projectDatabase(Group pattern) {
        Database database = new Database();

        for (Transaction tran : this) {
            tran.headGroup();
            if (tran.headGroup().meet(pattern)) {
                database.add(tran.tailTransaction());
            }
        }

        return database;
    }

}
