package onethreeseven.simplification.command;

import com.beust.jcommander.JCommander;
import onethreeseven.jclimod.AbstractCommandsListing;
import onethreeseven.jclimod.CLICommand;

/**
 * Commands for this module
 * @author Luke Bermingham
 */
public class SimplificationCommandListing extends AbstractCommandsListing {
    @Override
    protected CLICommand[] createCommands(JCommander jc, Object... args) {
        return new CLICommand[]{
                new SimplifyTrajectories()
        };
    }
}
