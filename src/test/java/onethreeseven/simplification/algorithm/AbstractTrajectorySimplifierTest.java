package onethreeseven.simplification.algorithm;

import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import onethreeseven.datastructures.util.DataGeneratorUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for all the simplification algorithms.
 * @author Luke Bermingham
 */
public class AbstractTrajectorySimplifierTest {

    private static final STTrajectory traj = DataGeneratorUtil.generateSpatiotemporalTrajectories(1).values().iterator().next();

    private static final float simplificationStrength = 0.9f;
    private static final int nExpectedEntries = Math.round(traj.size() * (1 - simplificationStrength));

    @Test
    public void simplifyDRTA() throws Exception {

        DRTA algo = new DRTA();
        SpatioCompositeTrajectory simplifiedTraj = algo.simplify(traj, simplificationStrength);
        Assert.assertEquals(nExpectedEntries, simplifiedTraj.size());

    }

    @Test
    public void simplifyDRDP() throws Exception {

        DRPD algo = new DRPD();
        SpatioCompositeTrajectory simplifiedTraj = algo.simplify(traj, simplificationStrength);
        Assert.assertEquals(nExpectedEntries, simplifiedTraj.size());

    }

    @Test
    public void simplifySTDRSED() throws Exception {

        STDRSED algo = new STDRSED();
        SpatioCompositeTrajectory simplifiedTraj = algo.simplify(traj, simplificationStrength);
        Assert.assertEquals(nExpectedEntries, simplifiedTraj.size());

    }

    @Test
    public void simplifySPLDP() throws Exception {

        SPLPD algo = new SPLPD();
        SpatioCompositeTrajectory simplifiedTraj = algo.simplify(traj, simplificationStrength);
        Assert.assertEquals(nExpectedEntries, simplifiedTraj.size());

    }

    @Test
    public void simplifySPLSED() throws Exception {

        STSPLSED algo = new STSPLSED();
        SpatioCompositeTrajectory simplifiedTraj = algo.simplify(traj, simplificationStrength);
        Assert.assertEquals(nExpectedEntries, simplifiedTraj.size());

    }

}