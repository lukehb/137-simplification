package onethreeseven.simplification.algorithm;

import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * The base class for all algorithms that simplify {@link onethreeseven.datastructures.model.SpatioCompositeTrajectory}s.
 * @author Luke Bermingham
 */
public abstract class AbstractTrajectorySimplifier {

    private static final Comparator<EntrySignificance> leastToMostSignificant =
            (o1, o2) -> Float.compare(o1.significance, o2.significance);

    private static final Comparator<EntrySignificance> indexOrdering =
            Comparator.comparingInt(o -> o.entryIdx);

    /**
     * Simplifies this trajectory by removing a certain percentage of entries that are considered less "significant"
     * than the other entries in this trajectory. The definition of significant varies depending on which simplification
     * algorithm is being used.
     * @param trajectory The trajectory to be simplified (though it is not mutated in any way -
     *                   a new simplified version is constructed).
     * @param simplificationStrength How much of the trajectory to remove. Acceptable values are 0-1 (inclusive).
     *                               0 means remove none. 1 means remove all.
     * @param <T> The type of entries in the trajectory.
     * @return A simplified version of the input trajectory.
     */
    public <T extends CompositePt> SpatioCompositeTrajectory<T> simplify(SpatioCompositeTrajectory<T> trajectory,
                                                                         float simplificationStrength){

        //ensure the trajectory is in cartesian coordinates
        trajectory.toCartesian();
        //give each entry a significance score
        List<EntrySignificance> scores = scoreEntries(trajectory);
        //rank them by sorting them in order of least significant to most
        scores.sort(leastToMostSignificant);
        //truncate the appropriate percentage of score from the front of the list
        int nToRemove = Math.round(trajectory.size() * simplificationStrength);
        scores = scores.subList(nToRemove, scores.size());
        //sort the score by the index so we can make the new trajectory in the correct index ordering
        scores.sort(indexOrdering);

        SpatioCompositeTrajectory<T> outputTraj = new SpatioCompositeTrajectory<T>(trajectory.isInCartesianMode(), trajectory.getProjection());

        //iterate the original trajectory and add indices that match
        Iterator<T> origIter = trajectory.iterator();
        int iterIdx = -1;
        for (EntrySignificance score : scores) {
            int scoreIdx = score.entryIdx;
            //iterate through the original trajectory until we get to the right index
            T entry = null;
            while(iterIdx < scoreIdx){
                entry = origIter.next();
                iterIdx++;
            }
            //store that entry as we are keeping it
            outputTraj.add(entry);
        }
        scores.clear();

        return outputTraj;
    }

    /**
     * Score each entry at each index.
     * @param trajectory The trajectory whose entries are going to be scored.
     * @param <T> The type of entries.
     * @return A list of scored entries.
     */
    protected abstract <T extends CompositePt> List<EntrySignificance> scoreEntries(SpatioCompositeTrajectory<T> trajectory);

    public abstract String readableName();
    public abstract String simpleName();

    @Override
    public String toString(){
        return readableName();
    }

    class EntrySignificance{
        final int entryIdx;
        final float significance;

        EntrySignificance(int entryIdx, float significance) {
            this.entryIdx = entryIdx;
            this.significance = significance;
        }
    }

}
