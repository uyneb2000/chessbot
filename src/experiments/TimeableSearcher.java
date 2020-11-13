package experiments;

import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;

public interface TimeableSearcher<M extends Move<M>, B extends Board<M, B>> extends Searcher<M, B> {
    public long getBestMoveTime(B board, int myTime, int opTime);
}
