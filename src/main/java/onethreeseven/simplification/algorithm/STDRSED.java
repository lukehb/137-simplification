package onethreeseven.simplification.algorithm;

import onethreeseven.common.util.Maths;
import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.STPt;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;

import java.time.temporal.ChronoUnit;

/**
 * A spatio-temporal trajectory simplification algorithm
 * that runs in linear time. It used Synchronised Euclidean Distance (SED)
 * to calculate the significance of each entry.
 * @author Luke Bermingham
 */
public class STDRSED extends AbstractDRTrajectorySimplifier {

    @Override
    public <T extends CompositePt> SpatioCompositeTrajectory<T> simplify(SpatioCompositeTrajectory<T> trajectory, float simplificationStrength) {
        if(!(trajectory instanceof STTrajectory)){
            throw new IllegalArgumentException("Trajectory must be of type STTrajectory to use this simplifier.");
        }
        return super.simplify(trajectory, simplificationStrength);
    }

    @Override
    public String readableName() {
        return "Fast synchronised euclidean distance";
    }

    @Override
    public String simpleName() {
        return "stdrsed";
    }

    @Override
    protected <T extends CompositePt> float scoreSingleEntry(SpatioCompositeTrajectory<T> trajectory, int index) {

        STTrajectory stTraj = (STTrajectory) trajectory;
        STPt a = stTraj.get(index-1);
        STPt b = stTraj.get(index);
        STPt c = stTraj.get(index+1);

        //get percentage along using temporal dimension
        long millisAtoB = ChronoUnit.MILLIS.between(a.getTime(), b.getTime());
        long millisAtoC = ChronoUnit.MILLIS.between(a.getTime(), c.getTime());
        double percentageAlong = millisAtoB / millisAtoC;

        //find the point along vector ac (we call b')
        double[] ac = Maths.sub(c.getCoords(), a.getCoords());
        double[] movedAlongAC = Maths.scale(ac, percentageAlong);
        double[] projected = Maths.add(a.getCoords(), movedAlongAC);

        //measure distance from b to b'
        return (float) Maths.dist(b.getCoords(), projected);
    }
}
