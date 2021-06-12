package edu.anadolu;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SuppressWarnings("ALL")
public class App {
    static boolean random;
    static boolean isBalance;

    public static void main(String[] args) throws IOException {

        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }
        if (params.getNN() && params.getR())
            throw new RuntimeException("You should select nearest neighborhood or random!");
        random = params.getR();
        isBalance = params.getBalance();
        mTSP best = null;

        int nn = params.firstNode() - 1;

        ConcurrentLinkedQueue<mTSP> solutions = new ConcurrentLinkedQueue<>();

        long startTime = System.nanoTime();
        if (params.getNN()) {
            mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());
            if (nn <= 0 || nn >= 82)
                throw new RuntimeException("Wrong interval!");
            mTSP.NNSolution(nn);
            best = mTSP;

        } else if (params.getR()) {
            IntStream.range(1, 100_000)
                    .boxed()
                    .parallel()
                    .forEach(integer -> {
                                mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());
                                mTSP.randomSolution();
                                assert !mTSP.integrityTest() : new RuntimeException("integrityTest");
                                assert !mTSP.routeSizeTest() : new RuntimeException("routeSizeTest");
                                solutions.add(mTSP);
                            }
                    );
            best = solutions.stream().min(Comparator.comparingInt(mTSP::cost)).get();
        } else
            throw new RuntimeException("You should select nearest neighborhood or random!");

        long estimatedTime = System.nanoTime() - startTime;
        double convert = (double) estimatedTime / 1_000_000_000;
        System.out.println("Finding best time --> " + convert + " seconds");


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
