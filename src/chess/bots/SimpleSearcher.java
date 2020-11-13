package chess.bots;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.List;

/**
 * This class should implement the minimax algorithm as described in the
 * assignment handouts.
 */
public class SimpleSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        BestMove<M> best = minimax(this.evaluator, board, ply);
        return best.move;
    }

    public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(Evaluator<B> evaluator, B board, int depth) {
        if (depth <= 0) {
            return new BestMove<>(evaluator.eval(board));
        }

        List<M> moves = board.generateMoves();

        if (moves.isEmpty()) {
            if (board.inCheck()) {
                return new BestMove<>(-evaluator.mate() - depth);
            } else {
                return new BestMove<>(-evaluator.stalemate());
            }
        }

        BestMove<M> bestMove = new BestMove<>(-evaluator.infty());

        for (M move : moves) {
            board.applyMove(move);
            BestMove<M> bm = minimax(evaluator, board, depth - 1).negate();
            board.undoMove();
            if (bm.value > bestMove.value) {
                bestMove = bm;
                bestMove.move = move;
            }
        }

        return bestMove;
    }
}