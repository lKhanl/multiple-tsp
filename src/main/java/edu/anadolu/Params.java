package edu.anadolu;

import com.lexicalscope.jewel.cli.Option;

public interface Params {

    @Option(description = "number of depots", shortName = "d", longName = "depots", defaultValue = "5")
    int getNumDepots();

    @Option(description = "number of salesmen per depot", shortName = {"s"}, longName = {"salesmen", "vehicles"}, defaultValue = "2")
    int getNumSalesmen();

    @Option(description = "use city names when displaying/printing", shortName = "v", longName = "verbose")
    boolean getVerbose();

    @Option(helpRequest = true, description = "display help", shortName = "h")
    boolean getHelp();

    @Option(description = "Nearest Neighborhood",shortName = "nn",longName = "nearestNeighborhood")
    boolean getNN();

    @Option(description = "First node of the output",shortName = "f",longName = "firstNode",defaultValue = "38")
    int firstNode();

    @Option(description = "random generator",shortName = "r",longName = "random")
    boolean getR();

    @Option(description = "exactly balanced",shortName = "b",longName = "balance")
    boolean getBalance();

}
