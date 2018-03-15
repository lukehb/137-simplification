package onethreeseven.simplification.view;

import javafx.stage.Stage;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.view.AbstractMenuBarPopulator;
import onethreeseven.trajsuitePlugin.view.MenuSupplier;
import onethreeseven.trajsuitePlugin.view.TrajSuiteMenu;
import onethreeseven.trajsuitePlugin.view.TrajSuiteMenuItem;

/**
 * Supply simplification to top menu bar
 * @author Luke Bermingham
 */
public class SimplificationMenuSupplier implements MenuSupplier {

    @Override
    public void supplyMenus(AbstractMenuBarPopulator populator, BaseTrajSuiteProgram program, Stage primaryStage) {

        TrajSuiteMenu preprocessing  = new TrajSuiteMenu("Pre-processing",3);
        TrajSuiteMenuItem simplifyTrajs = new TrajSuiteMenuItem("Simplify trajectories", ()->{
            //todo: load some view for simplification.
        });
        preprocessing.addChild(simplifyTrajs);
        populator.addMenu(preprocessing);


    }

}
