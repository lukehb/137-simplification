package algorithm;

import onethreeseven.common.util.Maths;
import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;

/**
 * A spatial-only linear trajectory simplification method that score individual entry
 * significance using the perpendicular distance between the current entry
 * and the line formed by connecting its neighbours.
 * @author Luke Bermingham
 */
public class DRDP extends AbstractDRTrajectorySimplifier {

    @Override
    protected <T extends CompositePt> float scoreSingleEntry(SpatioCompositeTrajectory<T> trajectory,
                                                             int index) {
        //we have three points: prev, cur, and next which we label a, b, and c.
        double[] a = trajectory.getCoords(index-1);
        double[] b = trajectory.getCoords(index);
        double[] c = trajectory.getCoords(index+1);
        return (float) Maths.perpendicularDistance(a,c, b);
    }

}
