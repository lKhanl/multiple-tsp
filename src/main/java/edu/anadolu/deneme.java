package edu.anadolu;

import java.util.LinkedList;

public class deneme {
    public static void main(String[] args) {
        int kod = 19;
        LinkedList<Integer> dis = new LinkedList<>();
        dis.add(kod);

        int secIndex = kod;
        while (dis.size() != 81) {
            int second = Integer.MAX_VALUE;
            kod = dis.getLast();
            for (int i = 0; i < 81; i++) {
                if (TurkishNetwork.distance[kod][i] < second && TurkishNetwork.distance[kod][i] != 0 && !dis.contains(i)) {
                    second = TurkishNetwork.distance[kod][i];
                    secIndex = i;
                }
            }
            dis.add(secIndex);
        }


        for (int i = 0; i < dis.size(); i++) {
            System.out.print(dis.get(i) + " ");
        }
    }

}
