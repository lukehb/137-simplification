package onethreeseven.simplification.command;

import com.beust.jcommander.Parameter;
import onethreeseven.common.util.ColorUtil;
import onethreeseven.datastructures.graphics.TrajectoryGraphic;
import onethreeseven.datastructures.model.CompositePt;
import onethreeseven.datastructures.model.STPt;
import onethreeseven.datastructures.model.SpatioCompositeTrajectory;
import onethreeseven.jclimod.CLICommand;
import onethreeseven.simplification.algorithm.*;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;
import onethreeseven.trajsuitePlugin.model.WrappedEntity;
import onethreeseven.trajsuitePlugin.transaction.AddEntitiesTransaction;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * Command for simplifying trajectories. It simplifies all selected trajectories.
 * @author Luke Bermingham
 */
public class SimplifyTrajectories extends CLICommand {

    @Parameter(names = {"-s", "--simplificationStrength"}, description = "The percentage of the trajectory to simplify " +
            "away, between 0 and 1. Where -s 90 means to remove 90% of the trajectory.")
    private int simplificationPercent = 50;

    @Parameter(names = {"-a", "--algorithm"}, description = "The name of the simplification algorithm to use.")
    private String algoName;

    private final Map<String, SpatioCompositeTrajectory<? extends CompositePt>> trajs;
    private String layername;
    private AbstractTrajectorySimplifier algo;

    private final Map<String, AbstractTrajectorySimplifier> algoMap = new HashMap<>();

    private Consumer<Double> progressReporter = null;

    public SimplifyTrajectories() {
        algoMap.put(new DRPD().simpleName(), new DRPD());
        algoMap.put(new DRTA().simpleName(), new DRTA());
        algoMap.put(new SPLPD().simpleName(), new SPLPD());
        algoMap.put(new STDRSED().simpleName(), new STDRSED());
        algoMap.put(new STSPLSED().simpleName(), new STSPLSED());
        trajs = new HashMap<>();
    }

    public void setProgressReporter(Consumer<Double> progressReporter) {
        this.progressReporter = progressReporter;
    }

    @Override
    protected String getUsage() {
        return "simplify -s 90 -a drdp";
    }

    @Override
    protected boolean parametersValid() {
        if(simplificationPercent < 0 || simplificationPercent > 100){
            System.err.println("Simplification strength must be between 0% and 100%.");
            return false;
        }

        if(algoName == null || !algoMap.containsKey(algoName)){
            System.err.println("Simplification algorithm name must be one of:");
            for (String validAlgoname : algoMap.keySet()) {
                System.err.println(validAlgoname);
            }
            System.err.println("Instead -a was set to: " + algoName);
            return false;
        }

        /////////////////////////////////
        //Get trajs we wish to process
        /////////////////////////////////

        //clear any old ones first
        trajs.clear();

        algo = algoMap.get(algoName);
        if(algo instanceof STDRSED || algo instanceof STSPLSED){
            trajs.putAll(getAllSelectedSTTrajs());
            layername = trajs.size() + " Simplified Spatial Trajectories (" + simplificationPercent + "%)";
        }
        else{
            trajs.putAll(getAllSelectedSpatialTrajs());
            layername = trajs.size() + " Simplified Spatio-Temporal Trajectories (" + simplificationPercent + "%)";
        }

        if(trajs.isEmpty()){
            System.err.println("No trajectories selected.");
            return false;
        }

        return true;
    }

    public Map<String, SpatioCompositeTrajectory<? extends CompositePt>> getAllSelectedSpatialTrajs(){

        Map<String, SpatioCompositeTrajectory<? extends CompositePt>> out = new HashMap<>();

        ServiceLoader<EntitySupplier> services = ServiceLoader.load(EntitySupplier.class);
        for (EntitySupplier service : services) {

            Map<String, WrappedEntity> entities = service.supplyAllMatching(wrappedEntity -> {
                if(!wrappedEntity.isSelectedProperty().get()){
                    return false;
                }
                Object modelObj = wrappedEntity.getModel();
                return modelObj instanceof SpatioCompositeTrajectory;
            });

            for (Map.Entry<String, WrappedEntity> entry : entities.entrySet()) {
                out.put(entry.getKey(), (SpatioCompositeTrajectory<? extends CompositePt>) entry.getValue().getModel());
            }
        }
        return out;
    }

    public Map<String, SpatioCompositeTrajectory<? extends CompositePt>> getAllSelectedSTTrajs(){
        Map<String, SpatioCompositeTrajectory<? extends CompositePt>> out = new HashMap<>();

        ServiceLoader<EntitySupplier> services = ServiceLoader.load(EntitySupplier.class);
        for (EntitySupplier service : services) {

            Map<String, WrappedEntity> entities = service.supplyAllMatching(wrappedEntity -> {
                if (!wrappedEntity.isSelectedProperty().get()) {
                    return false;
                }
                Object modelObj = wrappedEntity.getModel();
                if (!(modelObj instanceof SpatioCompositeTrajectory)) {
                    return false;
                }
                SpatioCompositeTrajectory traj = (SpatioCompositeTrajectory) modelObj;
                return traj.size() >= 1 && traj.iterator().next() instanceof STPt;
            });

            for (Map.Entry<String, WrappedEntity> entry : entities.entrySet()) {
                out.put(entry.getKey(), (SpatioCompositeTrajectory<? extends CompositePt>) entry.getValue().getModel());
            }
        }
        return out;
    }


    @Override
    protected boolean runImpl() {


        final float simplificationFactor = simplificationPercent / 100.0f;
        final AddEntitiesTransaction transaction = new AddEntitiesTransaction();
        final int nTrajs = trajs.size();;
        final java.awt.Color[] colors = ColorUtil.generateNColors(nTrajs);

        int i = 0;
        for (Map.Entry<String, SpatioCompositeTrajectory<? extends CompositePt>> entry : trajs.entrySet()) {
            if(!isRunning.get()){
                return false;
            }

            SpatioCompositeTrajectory<? extends CompositePt> simplifiedTraj =
                    algo.simplify(entry.getValue(), simplificationFactor);

            String entityName = "simplified_" + simplificationPercent + "_" + entry.getKey();
            TrajectoryGraphic graphic = new TrajectoryGraphic(simplifiedTraj);
            graphic.fallbackColor.setValue(colors[i]);

            transaction.add(layername, entityName, simplifiedTraj, graphic);

            double progress = i / (double)nTrajs;
            if(progressReporter != null){
                progressReporter.accept(progress);
            }

            i++;
        }

        ServiceLoader<TransactionProcessor> services = ServiceLoader.load(TransactionProcessor.class);
        for (TransactionProcessor service : services) {
            service.process(transaction);
        }

        return true;
    }

    @Override
    public boolean shouldStoreRerunAlias() {
        return false;
    }

    @Override
    public String generateRerunAliasBasedOnParams() {
        return null;
    }

    @Override
    public String getCategory() {
        return "Pre-processing";
    }

    @Override
    public String getCommandName() {
        return "simplify";
    }

    @Override
    public String[] getOtherCommandNames() {
        return new String[]{"st"};
    }

    @Override
    public String getDescription() {
        return "Simplifies all selected trajectories by the given simplification amount.";
    }
}
