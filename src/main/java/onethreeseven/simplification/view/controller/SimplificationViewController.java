package onethreeseven.simplification.view.controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import onethreeseven.jclimod.CLIProgram;
import onethreeseven.simplification.algorithm.*;
import onethreeseven.simplification.command.SimplifyTrajectories;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * View controller for simplification view.
 * @author Luke Bermingham
 */
public class SimplificationViewController {

    @FXML
    public Button simplifyBtn;
    @FXML
    public Label nSelectedSpatialTrajsLabel;
    @FXML
    public Label nSelectedSpatioTemporalTrajsLabel;
    @FXML
    public Label feedbackLabel;
    @FXML
    public ChoiceBox<AbstractTrajectorySimplifier> algoChoiceBox;
    @FXML
    public Spinner<Integer> simplificationPercentSpinner;
    @FXML
    public ProgressBar progressBar;

    private SimplifyTrajectories command = new SimplifyTrajectories();
    private CLIProgram prog = new CLIProgram();

    public void initialize(){

        prog.addCommand(command);

        //add out algorithm choices
        algoChoiceBox.getItems().addAll(new DRPD(), new DRTA(), new SPLPD(), new STSPLSED(), new STDRSED());
        algoChoiceBox.getSelectionModel().selectFirst();

        //spinner between 0 and 100%
        simplificationPercentSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50));

        BaseTrajSuiteProgram.getInstance().getLayers().numEditedEntitiesProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(this::refreshSelectedTrajs);
        });
        algoChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> refreshSelectedTrajs());

        refreshSelectedTrajs();

        //add an updater for the progress bar
        command.setProgressReporter(progress -> Platform.runLater(()-> progressBar.setProgress(progress)));

    }

    private boolean isSTAlgo() {
        AbstractTrajectorySimplifier algo = algoChoiceBox.getValue();
        return algo instanceof STDRSED || algo instanceof STSPLSED;
    }

    private void refreshSelectedTrajs(){

        int nSpatialTrajs = command.getAllSelectedSpatialTrajs().size();
        int nSTTrajs = command.getAllSelectedSTTrajs().size();

        nSelectedSpatialTrajsLabel.setText(String.valueOf(nSpatialTrajs));
        nSelectedSpatioTemporalTrajsLabel.setText(String.valueOf(nSTTrajs));

        if(nSpatialTrajs < 1){
            simplifyBtn.setDisable(true);
            feedbackLabel.setText("Please select some trajectories to simplify.");
        }
        else if(nSTTrajs < 1 && isSTAlgo()){
            simplifyBtn.setDisable(true);
            feedbackLabel.setText("Please select some spatio-temporal trajectories to simplify.");
        }else{
            simplifyBtn.setDisable(false);
            feedbackLabel.setText("");
        }

    }

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private void cancel(){
        command.stop();
        simplifyBtn.setText("Simplify");
        progressBar.setProgress(0);
        progressBar.setDisable(true);
    }

    private String[] makeCommandString(){
        String algoName = algoChoiceBox.getValue().simpleName();
        String simplificationPercent = String.valueOf(simplificationPercentSpinner.getValue());
        return new String[]{
                "simplify",
                "-a", algoName,
                "-s", simplificationPercent
        };
    }

    @FXML
    public void onSimplifyClicked(ActionEvent actionEvent) {

        if(isRunning.get()){
            cancel();
        }
        else{

            isRunning.set(true);
            simplifyBtn.setText("Cancel");
            progressBar.setDisable(false);
            progressBar.setProgress(0);

            CompletableFuture.runAsync(()->{
                prog.doCommand(makeCommandString());
                isRunning.set(false);
            }).handle((aVoid, throwable) -> {
                //success
                if (throwable == null) {
                    Platform.runLater(() -> {
                        ((Stage) simplifyBtn.getScene().getWindow()).close();
                    });
                }
                //failure
                else {
                    throwable.printStackTrace();
                    Platform.runLater(() -> {
                        simplifyBtn.setText("Simplify");
                        feedbackLabel.setText(throwable.getMessage());
                    });
                }
                return null;
            });

        }

    }
}
