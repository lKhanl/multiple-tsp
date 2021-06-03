package edu.anadolu;

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

    public int improveSolution() {
        int rnd = (int) (Math.random() * 5);

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
        int lowerBound = ((81 - DEPOT_NUMBERS) / (DEPOT_NUMBERS * ROUTE_NUMBERS)) + 1;
        double extra = (81 - DEPOT_NUMBERS) % (DEPOT_NUMBERS * ROUTE_NUMBERS);

        int sum = 0;
        LinkedHashMap<Integer, ArrayList<Integer>[]> map = new LinkedHashMap<>();

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
        dis.forEach(e -> System.out.println(e));

        solution = copy(map);
        cost = calculateCost();
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
