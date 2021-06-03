package edu.anadolu;

public class Counters {

    private int swapNodesInRoute;
    private int swapHubWithNodeInRoute;
    private int swapNodesBetweenRoutes;
    private int insertNodeInRoute;
    private int insertNodeBetweenRoutes;

    public void increment(int rnd) {
        switch (rnd) {
            case 0:
                this.swapNodesInRoute++;
                break;
            case 1:
                this.swapHubWithNodeInRoute++;
                break;
            case 2:
                this.swapNodesBetweenRoutes++;
                break;
            case 3:
                this.insertNodeInRoute++;
                break;
            case 4:
                this.insertNodeBetweenRoutes++;
                break;
        }
    }

    public void printCounters() {
        System.out.println("swapNodesInRoute: " + swapNodesInRoute);
        System.out.println("swapHubWithNodeInRoute: " + swapHubWithNodeInRoute);
        System.out.println("swapNodesBetweenRoutes: " + swapNodesBetweenRoutes);
        System.out.println("insertNodeInRoute: " + insertNodeInRoute);
        System.out.println("insertNodeBetweenRoutes: " + insertNodeBetweenRoutes);
    }
}


