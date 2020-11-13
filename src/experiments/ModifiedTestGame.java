package experiments;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.AlphaBetaSearcher;
import chess.bots.SimpleSearcher;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;

import java.util.ArrayList;
import java.util.List;

public class ModifiedTestGame {
    public Searcher<ArrayMove, ArrayBoard> whitePlayer;
    public Searcher<ArrayMove, ArrayBoard> blackPlayer;
    public static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private ArrayBoard board;

    public static void main(String[] args) {
        ModifiedTestGame game = new ModifiedTestGame();
        game.play();
    }

    public ModifiedTestGame() {
        setupWhitePlayer(new SimpleSearcher<ArrayMove, ArrayBoard>(), 3, 3);
        setupBlackPlayer(new AlphaBetaSearcher<ArrayMove, ArrayBoard>(), 3, 3);
    }

    public void play() {
        this.board = ArrayBoard.FACTORY.create().init(STARTING_POSITION);
        Searcher<ArrayMove, ArrayBoard> currentPlayer = this.blackPlayer;

        int turn = 0;

        /* Note that this code does NOT check for stalemate... */
        while (!board.generateMoves().isEmpty() && turn <= 100) {
            currentPlayer = currentPlayer.equals(this.whitePlayer) ? this.blackPlayer : this.whitePlayer;
            System.out.printf("%3d: " + board.fen() + "\n", turn);
            this.board.applyMove(currentPlayer.getBestMove(board, 1000, 1000));
            turn++;
        }
    }

    public Searcher<ArrayMove, ArrayBoard> setupPlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());
        return searcher;
    }
    public void setupWhitePlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        this.whitePlayer = setupPlayer(searcher, depth, cutoff);
    }
    public void setupBlackPlayer(Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        this.blackPlayer = setupPlayer(searcher, depth, cutoff);
    }
}
