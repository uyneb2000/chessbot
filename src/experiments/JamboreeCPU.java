package experiments;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class JamboreeCPU<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> implements TimeableSearcher<M, B> {
    public static final double PERCENTAGE_SEQUENTIAL = 0.5;
    public static final int DIVIDE_CUTOFF = 4;
    public static ForkJoinPool POOL = null;

    public JamboreeCPU(int CPUs) {
        if(POOL == null || POOL.getParallelism() != CPUs) {
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
        chess.bots.JamboreeSearcher.AlphaBetaTask<M, B> task = new chess.bots.JamboreeSearcher.AlphaBetaTask<M, B>(0, moves.size(), ply, this.cutoff, moves,
                board, evaluator, new BestMove<M>(-evaluator.infty()), new BestMove<M>(evaluator.infty()));
        BestMove<M> best = POOL.invoke(task);
        return System.currentTimeMillis()-start;
    }
}