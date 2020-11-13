package experiments;

import chess.bots.ParallelSearcher;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ParallelMinMaxCPU<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> implements TimeableSearcher<M, B> {
    public static ForkJoinPool POOL;
    public static final int DIVIDE_CUTOFF = 5;

    public ParallelMinMaxCPU(int CPUs) {
        if (POOL == null || POOL.getParallelism() != CPUs) {
            POOL = new ForkJoinPool(CPUs);
        }
    }

    @Override
    public M getBestMove(B board, int myTime, int opTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBestMoveTime(B board, int myTime, int opTime) {
        long start = System.currentTimeMillis();
        List<M> moves = board.generateMoves();
        ParallelSearcher.BestMoveTask<M, B> a = new ParallelSearcher.BestMoveTask<>(0, moves.size(), this.cutoff, moves, board,
                this.evaluator, ply);
        POOL.invoke(a);
        return System.currentTimeMillis() - start;
    }
}
