package algorithm;

import onethreeseven.collections.Range;
import onethreeseven.common.util.Maths;
import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;

/**
 * A spatial-only simplification algorithm for trajectories that uses a divide and conquer strategy O(n^2).
 * Entry significance is computed using perpendicular distance.
 * This method is equivalent to
 * <a href="https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm">this approach</a>
 * but with a simplification percentage control as opposed to some distance threshold.
 * @author Luke Bermingham
 */
public class SPLDP extends AbstractSPLTrajectorySimplifier {

    @Override
    protected <T extends CompositePt> EntrySignificance findMostSignificantEntry(SpatioCompositeTrajectory<T> trajectory,
                                                                                 Range r) {
        //going to make line between the entries specified at the bounds of the range
        double[] a = trajectory.getCoords(r.lowerBound, true);
        double[] c = trajectory.getCoords(r.upperBound, true);

        int bestIdx = r.lowerBound;
        double bestScore = -1;

        //go through each entry in the range and see the perpendicular distance between that entry and the line
        for (int i = r.lowerBound + 1; i < r.upperBound; i++) {
            double[] b = trajectory.getCoords(i);
            double pDist = Maths.perpendicularDistance(a,c,b);
            if(pDist > bestScore){
                bestScore = pDist;
                bestIdx = i;
            }
        }

        return new EntrySignificance(bestIdx, (float) bestScore);
    }

}
