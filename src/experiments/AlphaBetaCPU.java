package experiments;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;

public class AlphaBetaCPU<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> implements TimeableSearcher<M, B> {
    public AlphaBetaCPU(int CPUs) {
    }

    @Override
    public M getBestMove(B board, int myTime, int opTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBestMoveTime(B board, int myTime, int opTime) {
        long start = System.currentTimeMillis();
        BestMove<M> best = chess.bots.AlphaBetaSearcher.alphabeta(this.evaluator, board, new BestMove<M>(-evaluator.infty()), new BestMove<M>(evaluator.infty()), ply);
        return System.currentTimeMillis() - start;
    }
}