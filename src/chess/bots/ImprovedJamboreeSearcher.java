package chess.bots;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ImprovedJamboreeSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    public static final double PERCENTAGE_SEQUENTIAL = 0.5;
    public static final int DIVIDE_CUTOFF = 4;
    public static final ForkJoinPool POOL = new ForkJoinPool();

    public M getBestMove(B board, int myTime, int opTime) {
        BestMove<M> prevBest = null;
        BestMove<M> best = new BestMove<>(-evaluator.infty());
        List<M> allMoves = board.generateMoves();
        for (int i = this.ply; i >= this.cutoff; i--) {
            List<M> moves = new ArrayList<>();
            if (prevBest != null) {
                moves.add(prevBest.move);
            }
            moves.addAll(allMoves);
            AlphaBetaTask<M, B> task = new AlphaBetaTask<M, B>(0, moves.size(), ply, i, moves,
                    board, evaluator, new BestMove<M>(-evaluator.infty()), new BestMove<M>(evaluator.infty()));
            prevBest = POOL.invoke(task);
            if (prevBest.value > best.value) {
                best = prevBest;
            }
        }
        reportNewBestMove(best.move);
        return best.move;
    }

    public static class AlphaBetaTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
        private int depth, cutoff, lo, hi;
        private List<M> moves;
        private B board;
        private Evaluator<B> evaluator;
        BestMove<M> alpha, beta;

        public AlphaBetaTask(int lo, int hi, int depth, int cutoff, List<M> moves, B board, Evaluator<B> evaluator, BestMove<M> alpha, BestMove<M> beta) {
            this.lo = lo;
            this.hi = hi;
            this.depth = depth;
            this.cutoff = cutoff;
            this.moves = moves;
            this.board = board;
            this.evaluator = evaluator;
            this.alpha = alpha;
            this.beta = beta;
        }

        public BestMove<M> compute() {
            if (depth <= cutoff) {
                return AlphaBetaSearcher.alphabeta(evaluator, board, alpha, beta, depth);
            }

            List<M> moves = board.generateMoves();
            if (moves.isEmpty()) {
                if (board.inCheck()) {
                    return new BestMove<>(-evaluator.mate() - depth);
                } else {
                    return new BestMove<>(-evaluator.stalemate());
                }
            }

            reorderMoves();
            // board = board.copy();
            int numMovesSequential = (int) (PERCENTAGE_SEQUENTIAL * moves.size());

            for (int i = 0; i < numMovesSequential; i++) {
                M move = moves.get(i);
                board.applyMove(move);
                List<M> moveList = board.generateMoves();
                AlphaBetaTask<M, B> task = new AlphaBetaTask<>(0, moveList.size(), depth - 1, cutoff, moveList, board,
                        evaluator, new BestMove<M>(-beta.value), new BestMove<M>(-alpha.value));
                BestMove<M> bm = task.compute();
                bm.negate();
                board.undoMove();
                if (bm.value > alpha.value) {
                    alpha = bm;
                    alpha.move = move;
                }

                if (alpha.value >= beta.value) {
                    return alpha;
                }
            }

            JamboreeTask<M, B> parallel = new JamboreeTask<>(depth, cutoff, numMovesSequential,
                    moves.size(), moves, board, evaluator, alpha, beta);

            BestMove<M> parallelRes = parallel.compute();

            if (alpha.value >= parallelRes.value) {
                return alpha;
            } else {
                return parallelRes;
            }
        }

        public void reorderMoves() {
            List<M> reorderedMoves = new ArrayList<M>();
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).isCapture() || moves.get(i).isPromotion()) {
                    reorderedMoves.add(moves.get(i));
                    moves.remove(i);
                    i--;
                }
            }
            reorderedMoves.addAll(moves);
            moves = reorderedMoves;
        }
    }

    public static class JamboreeTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
        private int depth, cutoff, lo, hi;
        private List<M> moves;
        private B board;
        private Evaluator<B> evaluator;
        BestMove<M> alpha, beta;

        public JamboreeTask(int depth, int cutoff, int lo, int hi, List<M> moves, B board,
                            Evaluator<B> evaluator, BestMove<M> alpha, BestMove<M> beta) {
            this.depth = depth;
            this.cutoff = cutoff;
            this.lo = lo;
            this.hi = hi;
            this.moves = moves;
            this.board = board;
            this.evaluator = evaluator;
            this.alpha = alpha;
            this.beta = beta;
        }

        private BestMove<M> sequential() {
            if (moves.isEmpty()) {
                throw new IllegalStateException();
            }

            List<AlphaBetaTask<M, B>> threads = new ArrayList<>();
            for (int i = lo; i < hi; i++) {
                B boardCopy = board.copy();
                boardCopy.applyMove(moves.get(i));
                List<M> newMoves = boardCopy.generateMoves();
                AlphaBetaTask<M, B> forkTask = new AlphaBetaTask<M, B>(0, newMoves.size(), depth - 1, cutoff, newMoves,
                        boardCopy, evaluator, new BestMove<M>(-beta.value), new BestMove<M>(-alpha.value));
                forkTask.fork();
                threads.add(forkTask);
            }

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

        @Override
        public BestMove<M> compute() {
            if (hi - lo <= DIVIDE_CUTOFF) {
                return sequential();
            }

            int mid = lo + (hi - lo) / 2;
            JamboreeTask<M, B> left = new JamboreeTask<>(depth, cutoff, lo, mid, moves, board,
                    evaluator, alpha, beta);
            JamboreeTask<M, B> right = new JamboreeTask<>(depth, cutoff, mid, hi, moves, board,
                    evaluator, alpha, beta);

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
}