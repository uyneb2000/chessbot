package experiments;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.*;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;
import experiments.ModifiedTestGame;

import java.util.List;

public class TestPosition {
    public static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final String MIDDLE_POSITION = "r3k3/ppp3Bp/1nn5/4p3/1PP3q1/4P3/P1P4P/R4RK1 w - -";
    public static final String END_POSITION = "6k1/7p/p4n2/P1p1p3/4P2P/2p1K3/8/8 w - -";
    public static long totalTime = 0;
    public static final int WARMUP = 25;
    public static int trials = 0;

    public static ArrayMove getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());

        return searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

    public static void printMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        String botName = searcher.getClass().toString().split(" ")[1].replace("chess.bots.", "");
        getBestMove(fen, searcher, depth, cutoff);
    }

    public static void main(String[] args) {
        Searcher<ArrayMove, ArrayBoard> searcher = new JamboreeSearcher<>();
        for (int i = 0; i < 100; i++) {
            printMove(STARTING_POSITION, searcher, 5, 0);
        }
        System.out.println("Average time: " + totalTime / 75.0);
        totalTime = 0;
        for (int i = 0; i < 100; i++) {
            printMove(MIDDLE_POSITION, searcher, 5, 0);
        }
        System.out.println("Average time: " + totalTime / 75.0);
        totalTime = 0;
        for (int i = 0; i < 100; i++) {
            printMove(END_POSITION, searcher, 5, 0);
        }
        System.out.println("Average time: " + totalTime / 75.0);
    }
}
