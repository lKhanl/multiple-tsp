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
        int[] depotIndexes;
        if (mTSP.DEPOT_NUMBERS == 1 && mTSP.ROUTE_NUMBERS > 1) {
            depotIndexes = generateRandomNumber(0, mTSP.DEPOT_NUMBERS, false);
        } else if (mTSP.DEPOT_NUMBERS > 1 && mTSP.ROUTE_NUMBERS == 1) {
            depotIndexes = generateRandomNumber(0, mTSP.DEPOT_NUMBERS, false);
        } else {
            depotIndexes = generateRandomNumber(0, mTSP.DEPOT_NUMBERS, true);
        }
        int[] routeIndexes = generateRandomNumber(0, mTSP.ROUTE_NUMBERS, false);
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

    protected void updateTwoEdges() {

        int depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
        int routeIndex = (int) (Math.random() * mTSP.ROUTE_NUMBERS);
        int counter = 0;

        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {

            if (counter == depotIndex) {
                if (entry.getValue()[routeIndex].size() > 2) {
                    int[] numbers = generateRandomNumber(0, entry.getValue()[routeIndex].size(), false);

                    if (Math.abs(numbers[0] - numbers[1]) == 1) {
                        updateTwoEdges();
                    } else if (Math.abs(numbers[0] - numbers[1]) == 2) {
                        if (numbers[0] > numbers[1]) {
                            Collections.swap(entry.getValue()[routeIndex], numbers[1] + 1, numbers[0]);
                        } else {
                            Collections.swap(entry.getValue()[routeIndex], numbers[0] + 1, numbers[1]);
                        }

                    } else {

                        ArrayList<Integer> array = new ArrayList<>();
                        if (numbers[0] > numbers[1]) {

                            for (int i = numbers[1] + 1; i <= numbers[0]; i++) {
                                array.add(entry.getValue()[routeIndex].get(i));

                            }
                            int betweenNodeNumber = numbers[0] - numbers[1] + 1;

                            while (betweenNodeNumber != 1) {
                                entry.getValue()[routeIndex].remove(numbers[1] + 1);
                                betweenNodeNumber--;
                            }
                            int count = array.size() - 1;


                            for (int i = numbers[1] + 1; i <= numbers[0]; i++) {
                                entry.getValue()[routeIndex].add(i, array.get(count));
                                count--;
                            }


                        } else {

                            ArrayList<Integer> array1 = new ArrayList<>();

                            if (numbers[1] > numbers[0]) {

                                for (int i = numbers[0] + 1; i <= numbers[1]; i++) {
                                    array1.add(entry.getValue()[routeIndex].get(i));

                                }
                                int betweenNodeNumber = numbers[1] - numbers[0] + 1;

                                while (betweenNodeNumber != 1) {
                                    entry.getValue()[routeIndex].remove(numbers[0] + 1);
                                    betweenNodeNumber--;
                                }
                                int count = array1.size() - 1;


                                for (int i = numbers[0] + 1; i <= numbers[1]; i++) {
                                    entry.getValue()[routeIndex].add(i, array1.get(count));
                                    count--;
                                }

                            }
                        }
                    }
                }
            }
            counter++;
        }
        mTSP.cost = mTSP.calculateCost();
    }

    protected void updateThreeEdges() {

        int depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
        int routeIndex = (int) (Math.random() * mTSP.ROUTE_NUMBERS);
        int counter = 0;

        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : mTSP.solution.entrySet()) {

            if (counter == depotIndex) {
                if (entry.getValue()[routeIndex].size() > 3) {
                    int[] numbers = generateRandomNumber(0, entry.getValue()[routeIndex].size(), false); // size 3 olursa double benziyo
                    int[] Numbers = new int[3];
                    Numbers[0] = numbers[0];
                    Numbers[1] = numbers[1];

                    while (true) {

                        Numbers[2] = (int) (Math.random() * entry.getValue()[routeIndex].size());
                        if (Numbers[0] != Numbers[2] && Numbers[1] != Numbers[2]) {
                            break;
                        }
                    }

                    ArrayList<Integer> nums = new ArrayList<>();
                    nums.add(0, Numbers[0]);
                    nums.add(1, Numbers[1]);
                    nums.add(2, Numbers[2]);

                    Collections.sort(nums);

                    if (nums.get(2) - nums.get(1) == 1 && nums.get(1) - nums.get(0) == 1) {
                        updateThreeEdges();

                    } else if (nums.get(2) - nums.get(1) > 1 && nums.get(1) - nums.get(0) > 1 ) {

                        ArrayList<Integer> array = new ArrayList<>();
                        int choose = (int)(Math.random() * 3);
                        if (choose == 0) {

                            for (int i = nums.get(1) + 1; i < nums.get(2) + 1; i++) {
                                array.add(entry.getValue()[routeIndex].get(i));

                            }
                            Collections.reverse(array);
                            for (int i = nums.get(0) + 1; i < nums.get(1) + 1; i++) {
                                array.add(entry.getValue()[routeIndex].get(i));

                            }
                            for (int i = 0; i < nums.get(2) - nums.get(0); i++) {
                                entry.getValue()[routeIndex].remove(nums.get(0) + 1);
                            }

                            int count = 1;
                            for (int i = 0; i < array.size(); i++) {
                                entry.getValue()[routeIndex].add(nums.get(0) + count, array.get(i));
                                count++;
                            }
                        }

                        if (choose == 1) {
                            ArrayList<Integer> array1 = new ArrayList<>();
                            for (int i = nums.get(1) + 1; i < nums.get(2) + 1; i++) {
                                array1.add(entry.getValue()[routeIndex].get(i));

                            }
                            for (int i = nums.get(0) + 1; i < nums.get(1) + 1; i++) {
                                array1.add(entry.getValue()[routeIndex].get(i));

                            }
                            for (int i = 0; i < nums.get(2) - nums.get(0); i++) {
                                entry.getValue()[routeIndex].remove(nums.get(0) + 1);

                            }

                            int count = 0;
                            for (int i = 0; i < array1.size(); i++) {
                                entry.getValue()[routeIndex].add(nums.get(0) + count, array1.get(i));
                                count++;

                            }
                        }
                        if (choose == 2) {
                            ArrayList<Integer> array2 = new ArrayList<>();
                            for (int i = nums.get(0) + 1; i < nums.get(1) + 1; i++) {
                                array2.add(entry.getValue()[routeIndex].get(i));

                            }
                            Collections.reverse(array2);


                            for (int i = nums.get(2); i > nums.get(1) ; i--) {
                                array2.add(entry.getValue()[routeIndex].get(i));

                            }
                            for (int i = 0; i < nums.get(2) - nums.get(0); i++) {
                                entry.getValue()[routeIndex].remove(nums.get(0) + 1);

                            }

                            int count = 1;
                            for (int i = 0; i < array2.size(); i++) {
                                entry.getValue()[routeIndex].add(nums.get(0) + count, array2.get(i));
                                count++;

                            }
                        }
                    }else if(nums.get(2) - nums.get(1) > 1 && nums.get(1) - nums.get(0) == 1){


                        ArrayList<Integer> array = new ArrayList<>();

                        for (int i = nums.get(1) + 1; i < nums.get(2) + 1; i++) {
                            array.add(entry.getValue()[routeIndex].get(i));

                        }

                        array.add(entry.getValue()[routeIndex].get(nums.get(1)));


                        for (int i = 0; i < nums.get(2) - nums.get(0); i++) {
                            entry.getValue()[routeIndex].remove(nums.get(0) + 1);
                        }

                        int count = 1;
                        for (int i = 0; i < array.size(); i++) {
                            entry.getValue()[routeIndex].add(nums.get(0) + count, array.get(i));
                            count++;
                        }
                    }

                    else if(nums.get(2) - nums.get(1) ==1  && nums.get(1) - nums.get(0) > 1){
                        ArrayList<Integer> array = new ArrayList<>();
                        array.add(entry.getValue()[routeIndex].get(nums.get(2)));

                        for (int i = nums.get(0) + 1; i < nums.get(1) + 1; i++) {
                            array.add(entry.getValue()[routeIndex].get(i));

                        }

                        for (int i = 0; i < nums.get(2) - nums.get(0); i++) {
                            entry.getValue()[routeIndex].remove(nums.get(0) + 1);
                        }

                        int count = 1;
                        for (int i = 0; i < array.size(); i++) {
                            entry.getValue()[routeIndex].add(nums.get(0) + count, array.get(i));
                            count++;
                        }
                    }
                }
            }
            counter++;
        }
        mTSP.cost = mTSP.calculateCost();

    }
}
