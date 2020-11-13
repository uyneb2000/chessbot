package chess.bots;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    public static final ForkJoinPool POOL = new ForkJoinPool();
    public static final int DIVIDE_CUTOFF = 5;

    public static class BestMoveTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
        private int lo, hi, depth, cutoff;
        private List<M> moves;
        private B board;
        private Evaluator<B> evaluator;

        public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> sequentialDivide(int lo, int hi, int cutoff, List<M> moves, B board, Evaluator<B> evaluator, int depth) {
            if (depth <= cutoff) {
                BestMove<M> best = new BestMove<>(-evaluator.infty());
                for (int i = lo; i < hi; i++) {
                    board.applyMove(moves.get(i));
                    BestMove<M> move = SimpleSearcher.minimax(evaluator, board, depth - 1).negate();
                    board.undoMove();

                    if (move.value > best.value) {
                        best = move;
                        best.move = moves.get(i);
                    }
                }

                return best;
            } else {
                if (moves.isEmpty()) {
                    throw new IllegalStateException();
                }

                // Start threads
                List<BestMoveTask<M, B>> threads = new ArrayList<>();
                for (int i = lo; i < hi; i++) {
                    B boardCopy = board.copy();
                    boardCopy.applyMove(moves.get(i));
                    List<M> newMoves = boardCopy.generateMoves();
                    BestMoveTask<M, B> forkTask = new BestMoveTask<>(0, newMoves.size(),
                            cutoff, newMoves, boardCopy, evaluator, depth - 1);
                    forkTask.fork();
                    threads.add(forkTask);
                }

                // Now join them
                BestMove<M> best = new BestMove<>(-evaluator.infty());
                for (int i = lo; i < hi; i++) {
                BestMove<M> move = threads.get(i - lo).join().negate();
                    if (move.value > best.value) {
                        best = move;
                        best.move = moves.get(i);
                    }
                }

                return best;
            }
        }

        public BestMoveTask(int lo, int hi, int cutoff, List<M> moves, B board,
                            Evaluator<B> evaluator, int depth) {
            this.lo = lo;
            this.hi = hi;
            this.cutoff = cutoff;
            this.moves = moves;
            this.board = board;
            this.evaluator = evaluator;
            this.depth = depth;
        }

        @Override
        protected BestMove<M> compute() {
            if (moves.isEmpty()) {
                if (board.inCheck()) {
                    return new BestMove<>(-evaluator.mate() - depth);
                } else {
                    return new BestMove<>(-evaluator.stalemate());
                }
            }

            if (hi - lo < DIVIDE_CUTOFF) {
                return sequentialDivide(lo, hi, cutoff, moves, board, evaluator, depth);
            }

            int mid = lo + (hi - lo) / 2;
            BestMoveTask<M, B> left = new BestMoveTask<>(lo, mid, cutoff, moves, board.copy(),
                    evaluator, depth);
            BestMoveTask<M, B> right = new BestMoveTask<>(mid, hi, cutoff, moves, board,
                    evaluator, depth);

            right.fork();

            BestMove<M> leftBest = left.compute();
            BestMove<M> rightBest = right.join();

            if (leftBest.value >= rightBest.value) {
                return leftBest;
            } else {
                return rightBest;
            }
        }
    }

    public M getBestMove(B board, int myTime, int opTime) {
        List<M> moves = board.generateMoves();
        BestMoveTask<M, B> a = new BestMoveTask<>(0, moves.size(), this.cutoff, moves, board,
                this.evaluator, ply);
        return POOL.invoke(a).move;
    }
}