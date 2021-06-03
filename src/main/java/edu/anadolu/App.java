package edu.anadolu;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Hello world!
 */
@SuppressWarnings("ALL")
public class App {
    public static void main(String[] args) throws IOException {

        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }
//        System.out.println(TurkishNetwork.distance[28][3]);
        if (params.getNN() != -1 && params.getR())
            throw new RuntimeException("birinin seçilmesi lazım");

        mTSP best = null;

        LinkedList<mTSP> solutions = new LinkedList<>();

        int nn = params.getNN() - 1;

        if (nn != -2) {
            mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());
            if (nn <= 0 || nn >= 82)
                throw new RuntimeException("yanlış aralık!");
            mTSP.NNSolution(nn);
            best = mTSP;

        } else if (params.getR()) {
            for (int i = 0; i < 100_000; i++) {
                mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());
                mTSP.randomSolution();
                final int cost = mTSP.cost();
                solutions.add(mTSP);
            }
            best = solutions.stream().min(Comparator.comparingInt(mTSP::cost)).get();
        } else
            throw new RuntimeException("u should select alg");


        /** Finding best */
        /*mTSP best = null;

        LinkedList<mTSP> solutions = new LinkedList<>();


        for (int i = 0; i < 100_000; i++) {

            mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());

            int nn = params.getNN();
            if (nn != -1) {
                if (nn <= 0 || nn >= 82)
                    throw new RuntimeException("yanlış aralıké");
                mTSP.NNSolution(nn);
            } else if (params.getR())
                mTSP.randomSolution();
            else
                throw new RuntimeException("u should select alg");
            final int cost = mTSP.cost();
            solutions.add(mTSP);

        }
        best = solutions.stream().min(Comparator.comparingInt(mTSP::cost)).get();
*/
        if (best != null) {
            best.print(params.getVerbose());
            System.out.println("**Total cost is " + best.cost());
        }

        System.out.println("**************************************************************************");

        mTSP copy;
        Counters counter = new Counters();
        /** Second Part */
        for (int i = 0; i < 5_000_000; i++) {
            copy = new mTSP(best);

            int result = copy.improveSolution();

            if (copy.cost() < best.cost()) {
                best = new mTSP(copy);
                counter.increment(result);

            } else {
                copy = new mTSP(best);
            }

        }

        best.print(params.getVerbose());
        System.out.println("**Total cost is " + best.cost());
        counter.printCounters();
        best.writeJSONFILE();

    }
}
