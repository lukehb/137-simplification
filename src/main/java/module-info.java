import onethreeseven.jclimod.AbstractCommandsListing;
import onethreeseven.simplification.command.SimplificationCommandListing;
import onethreeseven.simplification.view.SimplificationMenuSupplier;
import onethreeseven.trajsuitePlugin.model.EntitySupplier;
import onethreeseven.trajsuitePlugin.model.TransactionProcessor;
import onethreeseven.trajsuitePlugin.view.MenuSupplier;

module onethreeseven.simplification {

    requires onethreeseven.trajsuitePlugin;
    requires onethreeseven.common;
    requires onethreeseven.datastructures;
    requires onethreeseven.collections;
    requires onethreeseven.jclimod;
    requires jcommander;
    requires java.desktop;

    //for javafx to work
    exports onethreeseven.simplification to javafx.graphics;
    exports onethreeseven.simplification.algorithm;
    exports onethreeseven.simplification.view;
    opens onethreeseven.simplification.view;
    exports onethreeseven.simplification.view.controller to javafx.fxml;
    opens onethreeseven.simplification.view.controller to javafx.fxml;

    //commands to work
    exports onethreeseven.simplification.command;
    opens onethreeseven.simplification.command to jcommander, onethreeseven.jclimod;

    //using and supplying other module data
    provides MenuSupplier with SimplificationMenuSupplier;
    provides AbstractCommandsListing with SimplificationCommandListing;

    //for inter-module data sharing
    uses TransactionProcessor;
    uses EntitySupplier;

}