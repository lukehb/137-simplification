package algorithm;

import onethreeseven.collections.Range;
import onethreeseven.common.util.Maths;
import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.STPt;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import java.time.temporal.ChronoUnit;

/**
 * A simplification algorithm for spatio-temporal trajectories that uses a divide and conquer strategy O(n^2).
 * Entry significance is computed using Synchronised Euclidean Distance (SED).
 * @author Luke Bermingham
 */
public class STSPLSED extends AbstractSPLTrajectorySimplifier {

    @Override
    public <T extends CompositePt> SpatioCompositeTrajectory<T> simplify(SpatioCompositeTrajectory<T> trajectory, float simplificationStrength) {
        if(!(trajectory instanceof STTrajectory)){
            throw new IllegalArgumentException("Trajectory must be of type STTrajectory to use this simplifier.");
        }
        return super.simplify(trajectory, simplificationStrength);
    }

    @Override
    protected <T extends CompositePt> EntrySignificance findMostSignificantEntry(SpatioCompositeTrajectory<T> trajectory,
                                                                                 Range r) {

        final STTrajectory stTraj = (STTrajectory) trajectory;
        final STPt a = stTraj.get(r.lowerBound);
        final STPt c = stTraj.get(r.upperBound);
        final double[] ac = Maths.sub(c.getCoords(), a.getCoords());
        final long totalSegmentDuration = ChronoUnit.MILLIS.between(a.getTime(), c.getTime());

        int bestIdx = r.lowerBound;
        double bestScore = -1;

        for (int i = r.lowerBound + 1; i < r.upperBound; i++) {
            STPt b = stTraj.get(i);
            long millisAtoB = ChronoUnit.MILLIS.between(a.getTime(), b.getTime());
            double percentageAlong = millisAtoB / totalSegmentDuration;
            double[] movedAlongAC = Maths.scale(ac, percentageAlong);
            double[] projected = Maths.add(a.getCoords(), movedAlongAC);
            double sedDistance = Maths.dist(b.getCoords(), projected);
            if(sedDistance > bestScore){
                bestScore = sedDistance;
                bestIdx = i;
            }
        }

        return new EntrySignificance(bestIdx, (float) bestScore);
    }

}
