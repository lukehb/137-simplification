package onethreeseven.simplification.experiments;

import onethreeseven.simplification.algorithm.AbstractTrajectorySimplifier;
import onethreeseven.simplification.algorithm.DRTA;
import onethreeseven.simplification.algorithm.SPLPD;
import onethreeseven.datastructures.model.STTrajectory;
import onethreeseven.datastructures.util.DataGeneratorUtil;

/**
 * Benchmark the running time of various simplification algorithms.
 * @author Luke Bermingham
 */
public class RunningTimeSynthetic {

    private static final int minEntries = 30000000;
    private static final int nStepsSize = 1750000;
    private static final int maxEntries = 30000001;
    private static final int nTrajs = 100;
    private static final int nRuns = 3;

    private static final float simplificationStrength = 0.975f;

    private static final AbstractTrajectorySimplifier[] algos = new AbstractTrajectorySimplifier[]{
            new DRTA(),
            new SPLPD()
    };

    public static void main(String[] args) {

        final StringBuilder output = new StringBuilder();

        output.append("NEntries,");
        output.append("Simplification(%),");
        for (AbstractTrajectorySimplifier algo : algos) {
            output.append(algo.getClass().getSimpleName());
            output.append("(ms),");
        }
        output.append("\n");

        for (int totalEntries = minEntries; totalEntries < maxEntries; totalEntries += nStepsSize) {

            output.append(totalEntries);
            output.append(",");
            output.append(simplificationStrength);
            output.append(",");

            for (AbstractTrajectorySimplifier algo : algos) {
                System.out.println("Computing running time for algo: " + algo.getClass().getSimpleName() +
                        " (size: " + totalEntries + ").");

                double totalRunningTime = 0;
                for (int i = 0; i < nRuns; i++) {
                    totalRunningTime += measureRunningTimeAtThisSize(algo, simplificationStrength, totalEntries);
                }
                totalRunningTime /= nRuns;
                output.append(totalRunningTime);
                output.append(",");
            }
            output.append("\n");

        }
        System.out.println(output.toString());
    }

    private static double measureRunningTimeAtThisSize(AbstractTrajectorySimplifier algo,
                                                       float simplificationStrength,
                                                       int totalEntries){
        long totalTime = 0;
        int size = totalEntries/nTrajs;

        for (int i = 0; i < nTrajs; i++) {
            STTrajectory traj = DataGeneratorUtil.generateRandomSTTrajectory(size);
            long startTime = System.currentTimeMillis();
            algo.simplify(traj, simplificationStrength);
            long endTime = System.currentTimeMillis();
            totalTime += endTime - startTime;
        }
        return totalTime;
    }

}
