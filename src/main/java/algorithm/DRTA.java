package algorithm;

import onethreeseven.common.util.Maths;
import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;

/**
 * A spatial-only linear trajectory simplification algorithm that uses triangular area between neighbouring entries
 * to determine significance of entries.
 * @author Luke Bermingham
 */
public class DRTA extends AbstractDRTrajectorySimplifier{
    @Override
    protected <T extends CompositePt> float scoreSingleEntry(SpatioCompositeTrajectory<T> trajectory,
                                                                                 int index) {
        double[] a = trajectory.getCoords(index-1);
        double[] b = trajectory.getCoords(index);
        double[] c = trajectory.getCoords(index+1);
        return (float) Maths.triArea(a[0], a[1], b[0], b[1], c[0], c[1]);
    }
}
