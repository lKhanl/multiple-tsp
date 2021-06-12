package edu.anadolu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("ALL")
public class mTSP {
    protected final int DEPOT_NUMBERS;
    protected final int ROUTE_NUMBERS;
    protected int cost;
    Operations op = new Operations(this);

    protected LinkedHashMap<Integer, ArrayList<Integer>[]> solution = new LinkedHashMap<>();

    public mTSP(int depot_numbers, int route_numbers) {
        int x = depot_numbers * route_numbers + depot_numbers;
        if (x > 81 || depot_numbers <= 0 || route_numbers <= 0) {
            throw new RuntimeException("illegal inputs!");
        }
        DEPOT_NUMBERS = depot_numbers;
        ROUTE_NUMBERS = route_numbers;

    }

    public mTSP(mTSP original) {
        DEPOT_NUMBERS = original.DEPOT_NUMBERS;
        ROUTE_NUMBERS = original.ROUTE_NUMBERS;
        solution = copy(original.solution);
        cost = original.cost;
    }

    public int cost() {
        return cost;
    }

    protected int calculateCost() {
        int cost = 0;
        int depot = 0;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
            int beginning;
            int city = 0;
            for (int i = 0; i < ROUTE_NUMBERS; i++) {
                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    if (j == 0) {
                        beginning = entry.getKey();
                        depot = entry.getKey();
                    } else {
                        beginning = entry.getValue()[i].get(j - 1);
                    }
                    city = entry.getValue()[i].get(j);
                    cost += TurkishNetwork.distance[beginning][city];
                }
                cost += TurkishNetwork.distance[city][depot];
            }
        }
        return cost;
    }

    private LinkedHashMap<Integer, ArrayList<Integer>[]> copy(LinkedHashMap<Integer, ArrayList<Integer>[]> map) {
        LinkedHashMap<Integer, ArrayList<Integer>[]> new_map = new LinkedHashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
            ArrayList<Integer>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
            for (int i = 0; i < entry.getValue().length; i++) {
                ArrayList<Integer> copyRoutes = new ArrayList<>();
                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    copyRoutes.add(entry.getValue()[i].get(j));
                }
                arrayLists[i] = copyRoutes;
            }
            new_map.put(entry.getKey(), arrayLists);
        }
        return new_map;
    }

    public void randomSolution() {
        int lowerBound = ((81 - DEPOT_NUMBERS) / (DEPOT_NUMBERS * ROUTE_NUMBERS));
        int extra = (81 - DEPOT_NUMBERS) % (DEPOT_NUMBERS * ROUTE_NUMBERS);
        int sum = 0;
        LinkedHashMap<Integer, ArrayList<Integer>[]> map = new LinkedHashMap<>();
        LinkedList<Integer> cities = new LinkedList<>();
        for (int i = 0; i < 81; i++) {
            cities.add(i);
        }
        Collections.shuffle(cities);

        /** Depo şehirleri seçimi */
        for (int i = 0; i < DEPOT_NUMBERS; i++) {
            ArrayList<Integer>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
            int rnd_depot = cities.remove(0);
            map.put(rnd_depot, arrayLists);
            sum += 1;
        }

        /** Route şehirleri seçimi */
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
            for (int j = 0; j < ROUTE_NUMBERS; j++) {
                ArrayList<Integer> route_cities = new ArrayList<>();

                for (int i = 0; i < lowerBound; i++) {
                    int rnd_route = cities.remove(0);
                    route_cities.add(rnd_route);
                }
                entry.getValue()[j] = route_cities;
            }
        }

        int counter = 0;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
            if (counter == extra) {
                break;
            }
            for (int j = 0; j < ROUTE_NUMBERS; j++) {
                ArrayList<Integer> route_cities = entry.getValue()[j];
                int rnd_route = cities.remove(0);
                route_cities.add(rnd_route);
                counter++;
                if (cities.size() == 0) {
                    break;
                }
            }
        }
        solution = copy(map);
        cost = calculateCost();

    }

    public void NNSolution(int nn) {
        LinkedList<Integer> dis = new LinkedList<>();
        dis.add(nn);

        int secIndex = nn;
        while (dis.size() != 81) {
            int second = Integer.MAX_VALUE;
            nn = dis.getLast();
            for (int i = 0; i < 81; i++) {
                if (TurkishNetwork.distance[nn][i] < second && TurkishNetwork.distance[nn][i] != 0 && !dis.contains(i)) {
                    second = TurkishNetwork.distance[nn][i];
                    secIndex = i;
                }
            }
            dis.add(secIndex);
        }
        int lowerBound = 0;
        if (App.isBalance)
            lowerBound = ((81 - DEPOT_NUMBERS) / (DEPOT_NUMBERS * ROUTE_NUMBERS)) + 1;
        else
            lowerBound = ((81 - DEPOT_NUMBERS) / (DEPOT_NUMBERS * ROUTE_NUMBERS));

        double extra = (81 - DEPOT_NUMBERS) % (DEPOT_NUMBERS * ROUTE_NUMBERS);

        int sum = 0;
        LinkedHashMap<Integer, ArrayList<Integer>[]> map = new LinkedHashMap<>();

        if (App.isBalance) {
            boolean flag = false;
            int stop = (int) (Math.ceil(extra / ROUTE_NUMBERS) + 1);
            for (int i = 0; i < DEPOT_NUMBERS; i++) {
                ArrayList<Integer>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
                int desired_depot;
                if (i < stop) {
                    desired_depot = dis.remove(i * (ROUTE_NUMBERS * lowerBound));
                } else {
                    int c = 1;
                    if (stop == i) {
                        lowerBound--;
                        flag = true;
                    }
                    desired_depot = dis.remove((stop - 1) * (ROUTE_NUMBERS * (lowerBound + 1)) + c * (ROUTE_NUMBERS * lowerBound));
                    c++;
                }
                map.put(desired_depot, arrayLists);
                sum += 1;
            }
            if (flag)
                lowerBound++;
            int counter = 0;
            for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
                for (int j = 0; j < ROUTE_NUMBERS; j++) {
                    ArrayList<Integer> route_cities = new ArrayList<>();
                    if (counter == extra) {
                        lowerBound--;
                    }
                    for (int i = 0; i < lowerBound; i++) {
                        int desired_route = dis.remove(0);
                        route_cities.add(desired_route);
                    }
                    entry.getValue()[j] = route_cities;
                    counter++;
                }
            }

        } else {
            for (int i = 0; i < DEPOT_NUMBERS; i++) {
                ArrayList<Integer>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
                int desired_depot = dis.remove(i * (ROUTE_NUMBERS * lowerBound));
                map.put(desired_depot, arrayLists);
                sum += 1;
            }

            for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
                for (int j = 0; j < ROUTE_NUMBERS; j++) {
                    ArrayList<Integer> route_cities = new ArrayList<>();
                    for (int i = 0; i < lowerBound; i++) {
                        int desired_route = dis.remove(0);
                        route_cities.add(desired_route);
                    }
                    entry.getValue()[j] = route_cities;
                }
            }

            int counter = 0;
            for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
                if (counter == DEPOT_NUMBERS - 1) {
                    entry.getValue()[ROUTE_NUMBERS - 1].addAll(dis);
                }
                counter++;
            }
        }


        solution = copy(map);
        cost = calculateCost();
    }

    public int improveSolution() {
        int rnd = (int)(Math.random()*7);

        switch (rnd) {
            case 0:
                op.swapNodesInRoute();
                break;
            case 1:
                op.swapHubWithNodeInRoute();
                break;
            case 2:
                op.swapNodesBetweenRoutes();
                break;
            case 3:
                op.insertNodeInRoute();
                break;
            case 4:
                if (DEPOT_NUMBERS >= 2) {
                    op.insertNodeBetweenRoutes();
                }
                break;
            case 5:
                op.updateTwoEdges();
                break;
            case 6:
                op.updateThreeEdges();
                break;


        }
        return rnd;
    }

    public void writeJSONFILE() {

        JSONObject obj = new JSONObject();
        JSONArray list = new JSONArray();

        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {

            JSONObject Obj = new JSONObject();
            // JSONObject route = new JSONObject();
            obj.put("solution", list);
            Obj.put("depot", entry.getKey().toString());
            JSONArray array = new JSONArray();
            for (int i = 0; i < entry.getValue().length; i++) {

                JSONObject object = new JSONObject();
                String temp = "";

                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    if (entry.getValue()[i].size() - 1 == j) {

                        temp += entry.getValue()[i].get(j);

                    } else {
                        temp += entry.getValue()[i].get(j) + " ";

                    }

                }

                array.put(temp);

            }
            Obj.put("route", array);

            list.put(Obj);

        }

        Path path = Paths.get("solution_" + "d" + DEPOT_NUMBERS + "s" + ROUTE_NUMBERS + ".json");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(obj.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void print(boolean verbose) {
        if (verbose) {
            LinkedHashMap<String, ArrayList<String>[]> verboseMap = new LinkedHashMap<>();
            for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
                ArrayList<String>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
                for (int i = 0; i < entry.getValue().length; i++) {
                    ArrayList<String> copyRoutes = new ArrayList<>();
                    for (int j = 0; j < entry.getValue()[i].size(); j++) {
                        copyRoutes.add(TurkishNetwork.cities[entry.getValue()[i].get(j)]);
                    }
                    arrayLists[i] = copyRoutes;
                }
                verboseMap.put(TurkishNetwork.cities[entry.getKey()], arrayLists);
            }
            int counter = 1;
            for (Map.Entry<String, ArrayList<String>[]> depot : verboseMap.entrySet()) {
                System.out.println("Depot" + counter + ": " + depot.getKey());
                int routeNumber = 1;
                for (int i = 0; i < depot.getValue().length; i++) {
                    System.out.print("    Route" + routeNumber + ": ");
                    for (int j = 0; j < depot.getValue()[i].size(); j++) {
                        System.out.print(depot.getValue()[i].get(j));
                        if (j != depot.getValue()[i].size() - 1) {
                            System.out.print(",");
                        }
                    }
                    System.out.println();
                    routeNumber++;
                }
                counter++;
            }

        } else {
            int counter = 1;
            for (Map.Entry<Integer, ArrayList<Integer>[]> depot : solution.entrySet()) {
                System.out.println("Depot" + counter + ": " + depot.getKey());
                int routeNumber = 1;
                for (int i = 0; i < depot.getValue().length; i++) {
                    System.out.print("    Route" + routeNumber + ": ");
                    for (int j = 0; j < depot.getValue()[i].size(); j++) {
                        System.out.print(depot.getValue()[i].get(j));
                        if (j != depot.getValue()[i].size() - 1) {
                            System.out.print(",");
                        }
                    }
                    System.out.println();
                    routeNumber++;
                }
                counter++;
            }
        }
    }

    /**
     * Junite dönecekler
     */
    public boolean integrityTest() {
        Set<Integer> set = new HashSet<>();
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
            set.add(entry.getKey());
            for (int i = 0; i < entry.getValue().length; i++) {
                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    set.add(entry.getValue()[i].get(j));
                }
            }
        }

        return (set.size() == 81);
    }

    public boolean routeSizeTest() {

        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
            if (entry.getKey() == null) {
                return false;
            }
            for (int i = 0; i < entry.getValue().length; i++) {
                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    if (entry.getValue()[i].size() < 1) {
                        return false;
                    }
                }
            }
        }
        return true;

    }

    private void swapNodesInRoute() {

        int depotIndex = (int) (Math.random() * DEPOT_NUMBERS);
        int routeIndex = (int) (Math.random() * ROUTE_NUMBERS);
        int counter = 0;

        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {

            if (counter == depotIndex) {
                if (entry.getValue()[routeIndex].size() != 1) {
                    int[] numbers = generateRandomNumber(0, entry.getValue()[routeIndex].size(), false);
                    Collections.swap(entry.getValue()[routeIndex], numbers[0], numbers[1]);
                } else {
                    depotIndex = (int) (Math.random() * DEPOT_NUMBERS);
                    routeIndex = (int) (Math.random() * ROUTE_NUMBERS);
                    counter = -1;
                }
            }
            counter++;
        }
        cost = calculateCost();
    }

    private void swapHubWithNodeInRoute() {
        int depotIndex = (int) (Math.random() * DEPOT_NUMBERS);
        int depotIndex1 = (int) (Math.random() * DEPOT_NUMBERS);
        int routeIndex = (int) (Math.random() * ROUTE_NUMBERS);
        int counter = 0;
        int counterTemp = 0;
        int temp = -1;
        int temp1 = -1;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
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
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
            if (depotCounter == depotIndex1) {
                entry.getValue()[routeIndex].set(counterTemp, temp1);
            }
            depotCounter++;
        }
        ArrayList<Integer>[] array = new ArrayList[solution.get(temp1).length];
        for (int i = 0; i < array.length; i++) {
            array[i] = solution.get(temp1)[i];
        }
        solution.remove(temp1);
        solution.put(temp, array);
        cost = calculateCost();
    }

    private void swapNodesBetweenRoutes() {
        int[] depotIndexes = generateRandomNumber(0, DEPOT_NUMBERS, true);
        int[] routeIndexes;
        if (depotIndexes[0] == depotIndexes[1]) {
            routeIndexes = generateRandomNumber(0, ROUTE_NUMBERS, false);
        } else
            routeIndexes = generateRandomNumber(0, ROUTE_NUMBERS, true);
        int[] nodeIndexes = new int[2];
        int n1 = 0;
        int n2 = 0;
        int depot1 = 0;
        int depot2 = 0;
        //Find 2 nodes in random node and keep as n1,n2
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
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
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
            if (depotIndexes[0] == depot1) {
                entry.getValue()[routeIndexes[0]].set(nodeIndexes[0], n2);
            }
            if (depotIndexes[1] == depot2) {
                entry.getValue()[routeIndexes[1]].set(nodeIndexes[1], n1);
            }
            depot1++;
            depot2++;
        }
        cost = calculateCost();
    }

    private void insertNodeInRoute() {
        int depotIndex = (int) (Math.random() * DEPOT_NUMBERS);
        int counter = 0;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
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
                    depotIndex = (int) (Math.random() * DEPOT_NUMBERS);
                    counter = 0;

                }
            }
            counter++;
        }
        cost = calculateCost();
    }

    private void insertNodeBetweenRoutes() {
        int[] depotIndexes = generateRandomNumber(0, DEPOT_NUMBERS, false);
        int depot1 = 0;
        int depot2 = 0;
        int deleted = -1;
        int route1Index = 0;
        int route2Index;
        ArrayList<Integer> route1;
        ArrayList<Integer> route2;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {

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
        cost = calculateCost();
    }

    private int[] generateRandomNumber(int lowerBound, int upperBound, boolean canSame) {
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
