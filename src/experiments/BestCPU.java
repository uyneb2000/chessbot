package experiments;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;

import java.util.Scanner;

public class BestCPU {
    public static final int CUTOFF = 3;
    public static final int DEPTH = 5;
    public static long totalTime = 0;

    public static long printMove(String fen, TimeableSearcher<ArrayMove, ArrayBoard> searcher, int depth,
                                 int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());

        return searcher.getBestMoveTime(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

    public static void main(String[] args) {
        System.out.println("How many cpu's would you like to use?");
        Scanner input = new Scanner(System.in);
        int CPUs = input.nextInt();
        TimeableSearcher<ArrayMove, ArrayBoard> searcher = new ParallelMinMaxCPU<>(CPUs);
        System.out.println("Using " + CPUs + " cpu's");

        for (int i = 0; i < 100; i++) {
            long runTime = printMove(TestPosition.STARTING_POSITION, searcher, DEPTH, CUTOFF);
            if (i >= 25) {
                totalTime += runTime;
            }
        }
        System.out.println("Average time for STARTING_POSITION: " + totalTime / 75.0);
        totalTime = 0;
        for (int i = 0; i < 100; i++) {
            long runTime = printMove(TestPosition.MIDDLE_POSITION, searcher, DEPTH, CUTOFF);
            if (i >= 25) {
                totalTime += runTime;
            }
        }
        System.out.println("Average time for MIDDLE_POSITION: " + totalTime / 75.0);
        totalTime = 0;
        for (int i = 0; i < 100; i++) {
            long runTime = printMove(TestPosition.END_POSITION, searcher, DEPTH, CUTOFF);
            if (i >= 25) {
                totalTime += runTime;
            }
        }
        System.out.println("Average time for END_POSITION: " + totalTime / 75.0);
    }
}