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

    @Option(description = "Nearest Neighborhood",shortName = "nn",longName = "nearestNeighborhood",defaultValue = "-1")
    int getNN();

    @Option(description = "asd",shortName = "r",longName = "random")
    boolean getR();


}
