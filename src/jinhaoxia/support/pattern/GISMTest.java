package jinhaoxia.support.pattern;

public class GISMTest {

    public static void main(String[] args) {
        Group groupSchemeAndPort = new Group(Group.TYPE_ITEM_SET, new int[]{1, 2});
        Group groupAuthority = new Group(
                Group.TYPE_SEQUENCE,
                new int[]{
                        101, 110, 102, 120, 103, 130
                }
        );

        int[][] paths = new int[][]{
                new int[]{1001, 1010, 1002, 1020, 1003, 1030},
                new int[]{1001, 1010, 1002, 1021, 1003, 1030},
                new int[]{1001, 1010, 1002, 1022, 1003, 1030},
                new int[]{1001, 1010, 1002, 1023, 1003, 1030},
                new int[]{1001, 1010, 1002, 1024, 1003, 1030},
                new int[]{1001, 1010, 1002, 1025, 1003, 1030},
                new int[]{1001, 1010, 1002, 1026, 1003, 1030},
                new int[]{1001, 1010, 1002, 1027, 1003, 1030},
                new int[]{1001, 1011, 1002, 1028},
                new int[]{1001, 1011, 1002, 1029},
                new int[]{1001, 1011, 1002, 1035},
                new int[]{1001, 1011, 1002, 1031},
                new int[]{1001, 1011, 1002, 1032},
                new int[]{1001, 1011, 1002, 1033},
                new int[]{1001, 1011, 1002, 1034},
                new int[]{1001, 1011, 1002, 1035},
        };

        Database database = new Database();

        for (int[] path : paths) {
            Transaction tran = new Transaction();
            tran.add(groupSchemeAndPort);
            tran.add(groupAuthority);
            tran.add(new Group(Group.TYPE_SEQUENCE, path));
            database.add(tran);
        }

        AlgoGISM gism = new AlgoGISM();

        gism.runAlgorithm(database, 0.5, null);

        return;

    }
}
