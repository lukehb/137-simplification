package onethreeseven.simplification.view;

import javafx.stage.Stage;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.view.*;

/**
 * Supply simplification to top menu bar
 * @author Luke Bermingham
 */
public class SimplificationMenuSupplier implements MenuSupplier {

    @Override
    public void supplyMenus(AbstractMenuBarPopulator populator, BaseTrajSuiteProgram program, Stage primaryStage) {

        TrajSuiteMenu preprocessing  = new TrajSuiteMenu("Pre-processing",3);

        TrajSuiteMenuItem simplifyTrajs = new TrajSuiteMenuItem("Simplify trajectories", ()->
                ViewUtil.loadUtilityView(SimplificationMenuSupplier.class, primaryStage,
                "Trajectory Simplification",
                "/onethreeseven/simplification/view/simplification.fxml"));

        preprocessing.addChild(simplifyTrajs);
        populator.addMenu(preprocessing);


    }

}
