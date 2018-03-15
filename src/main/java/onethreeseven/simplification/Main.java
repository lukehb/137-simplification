package onethreeseven.simplification;


import javafx.stage.Stage;
import onethreeseven.trajsuitePlugin.model.BaseTrajSuiteProgram;
import onethreeseven.trajsuitePlugin.view.BasicFxApplication;

public class Main extends BasicFxApplication{
    @Override
    protected BaseTrajSuiteProgram preStart(Stage stage) {
        BaseTrajSuiteProgram prog = BaseTrajSuiteProgram.getInstance();
        prog.getCLI().startListeningForInput();
        System.out.println("Type lc to view commands");

        prog.getCLI().doCommand(new String[]{
                "gt",
                "-ne", "1000",
                "-nt", "50"
        });

        return prog;
    }

    @Override
    public String getTitle() {
        return "Simplification";
    }

    @Override
    public int getStartWidth() {
        return 640;
    }

    @Override
    public int getStartHeight() {
        return 480;
    }
}
