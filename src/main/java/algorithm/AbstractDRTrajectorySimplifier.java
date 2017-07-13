package algorithm;

import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for trajectory simplification algorithms that process each entry incrementally.
 * @author Luke Bermingham
 */
public abstract class AbstractDRTrajectorySimplifier extends AbstractTrajectorySimplifier {

    @Override
    protected <T extends CompositePt> List<EntrySignificance> scoreEntries(SpatioCompositeTrajectory<T> trajectory) {

        final int nEntries = trajectory.size();
        final ArrayList<EntrySignificance> scores = new ArrayList<>(nEntries);

        for (int i = 0; i < nEntries; i++) {
            //always keep first entry
            if(i == 0){
                scores.add(new EntrySignificance(0, Float.MAX_VALUE));
            }
            //always keep last entry
            else if(i == nEntries-1){
                scores.add(new EntrySignificance(nEntries-1, Float.MAX_VALUE));
            }
            //score the rest using whatever heuristic
            else{
                scores.add(new EntrySignificance(i, scoreSingleEntry(trajectory, i)));
            }
        }

        return scores;
    }

    /**
     * Determine the significance of the current entry.
     * Note: Implementing this method you can assume that you will not have
     * process the first and last entries of the trajectory.
     * @param trajectory The trajectory to sample from (if needed).
     * @param index The index of the entry from the trajectory that is to be scored.
     * @param <T> The type of the entry.
     * @return A significance score for the entry at the index. A higher score is more significant.
     */
    protected abstract <T extends CompositePt> float scoreSingleEntry(SpatioCompositeTrajectory<T> trajectory, int index);

}
