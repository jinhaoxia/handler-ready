package jinhaoxia.support.pattern.cmclasp;

import jinhaoxia.support.pattern.cmclasp.clasp_AGP.AlgoCM_ClaSP;
import jinhaoxia.support.pattern.cmclasp.clasp_AGP.dataStructures.abstracciones.ItemAbstractionPair;
import jinhaoxia.support.pattern.cmclasp.clasp_AGP.dataStructures.creators.AbstractionCreator;
import jinhaoxia.support.pattern.cmclasp.clasp_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import jinhaoxia.support.pattern.cmclasp.clasp_AGP.dataStructures.database.SequenceDatabase;
import jinhaoxia.support.pattern.cmclasp.clasp_AGP.dataStructures.patterns.Pattern;
import jinhaoxia.support.pattern.cmclasp.clasp_AGP.idlists.creators.IdListCreator;
import jinhaoxia.support.pattern.cmclasp.clasp_AGP.idlists.creators.IdListCreatorStandard_Map;
import jinhaoxia.support.pattern.cmclasp.clasp_AGP.savers.Saver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MyCMClaSP {

    public List<int[]> cmclasp(List<int[]> data, double minSupport) {

        // Load a sequence database
        double support = minSupport;

        boolean keepPatterns = true;
        boolean verbose = false;
        boolean findClosedPatterns = true;
        boolean executePruningMethods = true;

        boolean outputSequenceIdentifiers = false;

        AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
        IdListCreator idListCreator = IdListCreatorStandard_Map.getInstance();

        SequenceDatabase sequenceDatabase = new SequenceDatabase(abstractionCreator, idListCreator);

        double relativeSupport = sequenceDatabase.loadDatabase(data, minSupport);

        AlgoCM_ClaSP algorithm = new AlgoCM_ClaSP(relativeSupport, abstractionCreator, findClosedPatterns, executePruningMethods);

        try {

            List<Pattern> patterns = new ArrayList<>();

            Saver saver = new Saver() {

                @Override
                public void savePattern(Pattern p) {
                    patterns.add(p);
                }

                @Override
                public void finish() {
                }

                @Override
                public void clear() {
                    patterns.clear();
                }

                @Override
                public String print() {
                    return patterns.toString();
                }
            };

            algorithm.runAlgorithm(sequenceDatabase, keepPatterns, verbose, saver);

            return patterns.stream().map(pattern -> convertPattern(pattern)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private int[] convertPattern(Pattern pattern) {
        int[] tmp = new int[pattern.getElements().size()];
        int i = 0;
        for (ItemAbstractionPair elem : pattern.getElements()) {
            tmp[i++] = (Integer) elem.getItem().getId();
        }

        return tmp;
    }

}
