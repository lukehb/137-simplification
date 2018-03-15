package onethreeseven.simplification.experiments;

import onethreeseven.simplification.algorithm.AbstractTrajectorySimplifier;
import onethreeseven.simplification.algorithm.DRTA;
import onethreeseven.simplification.algorithm.SPLPD;
import onethreeseven.common.util.FileUtil;
import onethreeseven.datastructures.data.STTrajectoryParser;
import onethreeseven.datastructures.data.resolver.*;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.geo.projection.ProjectionEquirectangular;
import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Calculate the running time for some real trajectory data.
 * @author Luke Bermingham
 */
public class RunningTimeReal {

    private static final File trajectoryFile = new File(FileUtil.makeAppDir("traj"), "geolife_179.txt");

    //geo-life parser
    private static final STTrajectoryParser parser = new STTrajectoryParser(
                new ProjectionEquirectangular(),
                new IdFieldResolver(0),
                new LatFieldResolver(1),
                new LonFieldResolver(2),
                new TemporalFieldResolver(6,7),
                true);

    private static final float simplificationStrength = 0.975f;
    private static final int nRuns = 3;

    private static final AbstractTrajectorySimplifier[] algos = new AbstractTrajectorySimplifier[]{
            new DRTA(),
            new SPLPD()
    };

    public static void main(String[] args) {

        StringBuilder output = new StringBuilder();

        output.append("NEntries,");
        output.append("Simplification(%),");
        for (AbstractTrajectorySimplifier algo : algos) {
            output.append(algo.getClass().getSimpleName());
            output.append("(ms),");
        }
        output.append("\n");

        Iterator<Map.Entry<String, STTrajectory>> iter = parser.iterator(trajectoryFile);

        long[] totalAlgoRunningTimes = new long[algos.length];
        int totalEntries = 0;

        while(iter.hasNext()){

            STTrajectory traj = iter.next().getValue();
            totalEntries += traj.size();

            output.append(totalEntries);
            output.append(",");
            output.append(simplificationStrength);
            output.append(",");

            for (int j = 0; j < algos.length; j++) {
                AbstractTrajectorySimplifier algo = algos[j];

                System.out.println("Computing running time for algo: " + algo.getClass().getSimpleName() +
                        " (size: " + totalEntries + ").");

                long trajAlgoRunningTime = 0;
                for (int i = 0; i < nRuns; i++) {
                    long startTime = System.currentTimeMillis();
                    algo.simplify(traj, simplificationStrength);
                    long endTime = System.currentTimeMillis();
                    trajAlgoRunningTime += (endTime - startTime);
                }
                trajAlgoRunningTime = trajAlgoRunningTime / nRuns;
                totalAlgoRunningTimes[j] += trajAlgoRunningTime;
                //output
                output.append(totalAlgoRunningTimes[j]);
                output.append(",");
            }
            output.append("\n");
        }

        System.out.println(output.toString());

    }

}
