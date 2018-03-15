package onethreeseven.simplification.algorithm;

import onethreeseven.collections.Range;
import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The base class for trajectory simplification methods that divide and conquer
 * portions of the data-set.
 * @author Luke Bermingham
 */
public abstract class AbstractSPLTrajectorySimplifier extends AbstractTrajectorySimplifier {

    @Override
    protected <T extends CompositePt> List<EntrySignificance> scoreEntries(SpatioCompositeTrajectory<T> trajectory) {

        ArrayList<EntrySignificance> scores = new ArrayList<>();
        int size = trajectory.size();
        //add the first and last scores as significant
        scores.add(new EntrySignificance(0, Float.MAX_VALUE));
        scores.add(new EntrySignificance(size-1, Float.MAX_VALUE));

        Stack<Range> toProcess = new Stack<>();
        toProcess.push(new Range(0, size-1));

        //process all indices by splitting on the most significant entry
        while(!toProcess.isEmpty()){
            Range indexRange = toProcess.pop();
            if(indexRange.getRange() <= 1){
                continue;
            }

            EntrySignificance score = findMostSignificantEntry(trajectory, indexRange);
            scores.add(score);

            //best score is one of the range indices, that means the whole range is insignificant
            toProcess.push(new Range(indexRange.lowerBound, score.entryIdx));
            toProcess.push(new Range(score.entryIdx, indexRange.upperBound));
        }

        return scores;
    }

    /**
     * Given an index range find the most significant entry within that range.
     * @param trajectory The trajectory whose entries will be considered.
     * @param r The index range to work within.
     * @param <T> The type of entries in the trajectory.
     * @return The most significant entry.
     */
    protected abstract <T extends CompositePt> EntrySignificance findMostSignificantEntry(SpatioCompositeTrajectory<T> trajectory, Range r);

}
