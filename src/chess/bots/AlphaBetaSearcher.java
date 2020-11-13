package chess.bots;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.List;

public class AlphaBetaSearcher<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B> {
    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        BestMove<M> best = alphabeta(this.evaluator, board, new BestMove<M>(-evaluator.infty()), new BestMove<M>(evaluator.infty()), ply);
        return best.move;
    }

    public static <M extends Move<M>, B extends Board<M, B>> BestMove<M> alphabeta(Evaluator<B> evaluator, B board, BestMove<M> alpha, BestMove<M> beta, int depth) {
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

        for (M move : moves) {
            board.applyMove(move);
            BestMove<M> bm = alphabeta(evaluator, board, new BestMove<M>(-beta.value), new BestMove<M>(-alpha.value), depth - 1).negate();
            board.undoMove();
            if (bm.value > alpha.value) {
                alpha = bm;
                alpha.move = move;
            }

            if (alpha.value >= beta.value) {
                return alpha;
            }
        }

        return alpha;
    }
}