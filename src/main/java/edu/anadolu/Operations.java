package edu.anadolu;

import java.util.*;

public class Operations {
    mTSP mTSP;

    public Operations(mTSP mTSP) {
        this.mTSP = mTSP;
    }

    protected void swapNodesInRoute() {

        int depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
        int routeIndex = (int) (Math.random() * mTSP.ROUTE_NUMBERS);
        int counter = 0;

        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {

            if (counter == depotIndex) {
                if (entry.getValue()[routeIndex].size() != 1) {
                    int[] numbers = generateRandomNumber(0, entry.getValue()[routeIndex].size(), false);
                    Collections.swap(entry.getValue()[routeIndex], numbers[0], numbers[1]);
                } else {
                    depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
                    routeIndex = (int) (Math.random() * mTSP.ROUTE_NUMBERS);
                    counter = -1;
                }
            }
            counter++;
        }
        mTSP.cost = mTSP.calculateCost();
    }

    protected void swapHubWithNodeInRoute() {
        int depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
        int depotIndex1 = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
        int routeIndex = (int) (Math.random() * mTSP.ROUTE_NUMBERS);
        int counter = 0;
        int counterTemp = 0;
        int temp = -1;
        int temp1 = -1;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {
            if (counter == depotIndex) {
                temp1 = entry.getKey();
            }
            if (depotIndex1 == counter) {
                counterTemp = (int) (Math.random() * entry.getValue()[routeIndex].size());
                temp = entry.getValue()[routeIndex].get(counterTemp);
            }
            if (temp != -1 && temp1 != -1) {
                break;
            }
            counter++;
        }
        int depotCounter = 0;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {
            if (depotCounter == depotIndex1) {
                entry.getValue()[routeIndex].set(counterTemp, temp1);
            }
            depotCounter++;
        }
        ArrayList<Integer>[] array = new ArrayList[mTSP.solution.get(temp1).length];
        for (int i = 0; i < array.length; i++) {
            array[i] = mTSP.solution.get(temp1)[i];
        }
        mTSP.solution.remove(temp1);
        mTSP.solution.put(temp, array);
        mTSP.cost = mTSP.calculateCost();
    }

    protected void swapNodesBetweenRoutes() {
        int[] depotIndexes = generateRandomNumber(0, mTSP.DEPOT_NUMBERS, true);
        int[] routeIndexes;
        if (depotIndexes[0] == depotIndexes[1]) {
            routeIndexes = generateRandomNumber(0, mTSP.ROUTE_NUMBERS, false);
        } else
            routeIndexes = generateRandomNumber(0, mTSP.ROUTE_NUMBERS, true);
        int[] nodeIndexes = new int[2];
        int n1 = 0;
        int n2 = 0;
        int depot1 = 0;
        int depot2 = 0;
        //Find 2 nodes in random node and keep as n1,n2
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {
            if (depotIndexes[0] == depot1) {
                int rnd = (int) (Math.random() * entry.getValue()[routeIndexes[0]].size());
                nodeIndexes[0] = rnd;
                n1 = entry.getValue()[routeIndexes[0]].get(rnd);
            }
            if (depotIndexes[1] == depot2) {
                int rnd = (int) (Math.random() * entry.getValue()[routeIndexes[1]].size());
                nodeIndexes[1] = rnd;
                n2 = entry.getValue()[routeIndexes[1]].get(nodeIndexes[1]);
            }
            depot1++;
            depot2++;
        }
        depot1 = 0;
        depot2 = 0;
        //Swap n1 and n2
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {
            if (depotIndexes[0] == depot1) {
                entry.getValue()[routeIndexes[0]].set(nodeIndexes[0], n2);
            }
            if (depotIndexes[1] == depot2) {
                entry.getValue()[routeIndexes[1]].set(nodeIndexes[1], n1);
            }
            depot1++;
            depot2++;
        }
        mTSP.cost = mTSP.calculateCost();
    }

    protected void insertNodeInRoute() {
        int depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
        int counter = 0;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {
            if (counter == depotIndex) {

                int routeIndex = (int) (Math.random() * entry.getValue().length);
                if (entry.getValue()[routeIndex].size() != 1) {
                    int[] nodesIndexes = generateRandomNumber(0, entry.getValue()[routeIndex].size(), false);
                    int rndIndex1 = nodesIndexes[0];//silinecek olan node'un indexi
                    int rndIndex2 = nodesIndexes[1];//yanına alınacak olan node'un indexi
                    int number1 = entry.getValue()[routeIndex].get(rndIndex1);//silinecek olan node
                    entry.getValue()[routeIndex].set(rndIndex1, -1);//silmek yerine -1 koydum
                    entry.getValue()[routeIndex].add(rndIndex2 + 1, number1);
                    entry.getValue()[routeIndex].remove((Integer) (-1));//???????????
                    break;
                } else {
                    depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
                    counter = 0;

                }
            }
            counter++;
        }
        mTSP.cost = mTSP.calculateCost();
    }

    protected void insertNodeBetweenRoutes() {
        int[] depotIndexes = generateRandomNumber(0, mTSP.DEPOT_NUMBERS, false);
        int depot1 = 0;
        int depot2 = 0;
        int deleted = -1;
        int route1Index = 0;
        int route2Index;
        ArrayList<Integer> route1;
        ArrayList<Integer> route2;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {

            if (depotIndexes[0] == depot1) {
                if (entry.getValue()[route1Index].size() == 1) {
                    break;
                } else {
                    route1 = entry.getValue()[route1Index];
                    int node = (int) (Math.random() * route1.size());
                    deleted = entry.getValue()[route1Index].remove(node);
                }
            }
            if (depotIndexes[1] == depot2) {
                if (deleted == -1) {
                    depot1++;
                    continue;
                } else {
                    route2Index = (int) (Math.random() * entry.getValue().length);
                    route2 = entry.getValue()[route2Index];


                    int node = (int) (Math.random() * route2.size());
                    entry.getValue()[route2Index].add(node + 1, deleted);

                    break;

                }
            }
            depot1++;
            depot2++;
        }
        mTSP.cost = mTSP.calculateCost();
    }

    protected int[] generateRandomNumber(int lowerBound, int upperBound, boolean canSame) {
        int[] arr = new int[2];
        if (lowerBound == upperBound) {
            throw new RuntimeException("Alt ve üst sınır aynı olamaz!");
        }
        if (!canSame) {
            while (true) {
                int rnd = (int) (Math.random() * (upperBound - lowerBound) + lowerBound);
                int rnd1 = (int) (Math.random() * (upperBound - lowerBound) + lowerBound);
                if (rnd != rnd1) {
                    arr[0] = rnd;
                    arr[1] = rnd1;
                    break;
                }
            }
        } else {
            arr[0] = (int) (Math.random() * (upperBound - lowerBound) + lowerBound);
            arr[1] = (int) (Math.random() * (upperBound - lowerBound) + lowerBound);
        }
        return arr;
    }
}
