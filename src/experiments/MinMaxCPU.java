package experiments;

import chess.bots.BestMove;
import chess.bots.SimpleSearcher;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;

public class MinMaxCPU<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> implements TimeableSearcher<M, B>  {
    public MinMaxCPU(int CPUs) {
    }

    @Override
    public M getBestMove(B board, int myTime, int opTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBestMoveTime(B board, int myTime, int opTime) {
        long start = System.currentTimeMillis();
        BestMove<M> best = SimpleSearcher.minimax(this.evaluator, board, ply);
        return System.currentTimeMillis() - start;
    }


}
