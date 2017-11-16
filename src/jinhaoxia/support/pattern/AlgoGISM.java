package jinhaoxia.support.pattern;

import com.google.common.collect.Lists;
import jinhaoxia.support.pattern.cmclasp.MyCMClaSP;
import jinhaoxia.support.pattern.fpclose.MyFPClose;

import java.util.*;
import java.util.stream.Collectors;

public class AlgoGISM {

    private Database patterns;

    public AlgoGISM() {
        this.patterns = new Database();
    }

    public Database getPatterns(){
        return patterns;
    }

    public void runAlgorithm(Database database, double minSupport, LinkNode<Group> previousPatterns) {
        Integer headGroupType = database.headGroupType();
        List<Group> headGroups = database.headGroups();


        if (headGroupType == null) {
            List<Group> completePattern = new ArrayList<>();
            LinkNode<Group> p = previousPatterns;
            while (p != null) {
                completePattern.add(p.data);
                p = p.prev;
            }
            completePattern = Lists.reverse(completePattern);

            Transaction transactionPattern = new Transaction(completePattern);

            patterns.add(transactionPattern);

            return;
        }

        List<Group> headPatterns = null;

        if (headGroupType == Group.TYPE_ITEM_SET) {
            headPatterns = mineFrequentClosedItemSet(headGroups, minSupport);
        } else if (headGroupType == Group.TYPE_SEQUENCE) {
            headPatterns = mineFrequentClosedSequence(headGroups, minSupport);
        }

        for (Group pattern : headPatterns) {
            Database projectedDatabase = database.projectDatabase(pattern);
            double projectedMinSupport = minSupport * database.size() / projectedDatabase.size();
            LinkNode<Group> newPreviousPatterns = new LinkNode<>();
            newPreviousPatterns.data = pattern;
            newPreviousPatterns.prev = previousPatterns;
            runAlgorithm(projectedDatabase, projectedMinSupport, newPreviousPatterns);
        }
    }

    public List<Group> mineFrequentClosedSequence(List<Group> groups, double minSupport) {
        List<int[]> datas = groups.stream().map(group -> group.array).collect(Collectors.toList());
        MyCMClaSP myCMClaSP = new MyCMClaSP();
        List<int[]> frequentSequences = myCMClaSP.cmclasp(datas, minSupport);
        List<Group> frequentGroupPatterns = frequentSequences.stream().map(data -> new Group(Group.TYPE_SEQUENCE, data)).collect(Collectors.toList());
        if (frequentGroupPatterns.size()== 0) frequentGroupPatterns.add(new Group(Group.TYPE_SEQUENCE, new int[]{}));
        return frequentGroupPatterns;
    }

    public List<Group> mineFrequentClosedItemSet(List<Group> groups, double minSupport) {
        List<int[]> datas = groups.stream().map(group -> group.array).collect(Collectors.toList());
        MyFPClose fpClose = new MyFPClose();
        List<int[]> frequentItemSets = fpClose.runAlgorithm(datas, minSupport);
        List<Group> frequentGroupPatterns = frequentItemSets.stream().map(data -> new Group(Group.TYPE_ITEM_SET, data)).collect(Collectors.toList());
        if (frequentGroupPatterns.size()== 0) frequentGroupPatterns.add(new Group(Group.TYPE_ITEM_SET, new int[]{}));
        return frequentGroupPatterns;
    }

}

class LinkNode<T> {
    T data;
    LinkNode<T> prev;
}

