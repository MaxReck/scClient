package sc.player2022.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.player.IGameHandler;
import sc.plugin2022.GameState;
import sc.plugin2022.Move;
import sc.shared.GameResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class Logic implements IGameHandler {
    private static final Logger log = LoggerFactory.getLogger(Logic.class);

    /**
     * Aktueller Spielstatus.
     */
    private GameState gameState;

    public void onGameOver(GameResult data) {
        log.info("Das Spiel ist beendet, Ergebnis: {}", data);
    }


    @Override
    public Move calculateMove() {
        long startTime = System.currentTimeMillis();
        log.info("Es wurde ein Zug von {} angefordert.", gameState.getCurrentTeam());
        NewNewEvaluate.setPlayerTeam(gameState.getCurrentTeam());
        AtomicInteger depth = new AtomicInteger(1);
        AtomicReference<PosMove> posMove = new AtomicReference<>();
        Thread bababui = new Thread(() -> {
            while (true) {
                posMove.set(miniMax(depth.get(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, this.gameState));
                depth.getAndAdd(2);
                if(posMove.get().getGameState() ==  null) {
                    System.out.println(posMove.get() + " on depth= " + depth.get());
                }
            }
        });
        bababui.start();
        while (System.currentTimeMillis() - startTime < 1850) {
        }
        bababui.stop();
        log.info(depth + " Tiefe");
        Move move = posMove.get().getGameState().getLastMove();
        log.info("Sende {} nach {}ms. evals are" + posMove.get().getRating(), move, System.currentTimeMillis() - startTime);
//        System.out.println(Evaluate.eval);

        return move;
    }

    @Override
    public void onUpdate(IGameState gameState) {
        this.gameState = (GameState) gameState;
        log.info("Zug: {} Dran: {}", gameState.getTurn(), gameState.getCurrentTeam());
    }

    @Override
    public void onError(String error) {
        log.warn("Fehler: {}", error);
    }

    //get all game states for current game state
    public List<GameState> getGameStates(GameState gameState) {
        GameState temp;
        List<GameState> nextLvlGameStates = new ArrayList<>();
        for (Move move : gameState.getPossibleMoves()) {
            temp = gameState.clone();
            temp.performMove(move); // perform move to create a different game state
            nextLvlGameStates.add(temp);
        }
        return nextLvlGameStates;
    }

    public PosMove miniMax( int depth, double alpha, double beta, boolean Maximum, GameState gameState)  {
        if(depth <= 0) {
            return new PosMove(gameState,NewNewEvaluate.evaluateGameState(gameState));
        }
        if(Maximum) {
            PosMove maxMove = new PosMove(null, Double.NEGATIVE_INFINITY );
            for(GameState move: getGameStates(gameState)) {
                PosMove posMove = miniMax(depth-1, alpha, beta, false, move);
                if(posMove.getRating() > maxMove.getRating()) {
                    maxMove.setRating(posMove.getRating());
                    maxMove.setGameState(move);
                }
                alpha = Math.max(alpha, posMove.getRating());
                if(beta <= alpha) {
                    break;
                }
            }
            return maxMove;
        } else {
            PosMove minMove = new PosMove(null, Double.POSITIVE_INFINITY);
            for(GameState move : getGameStates(gameState)) {
                if(move == null) {
                    continue;
                }
                PosMove posMove = miniMax(depth-1, alpha, beta, true, move);
                if(minMove.getRating() > posMove.getRating()) {
                    minMove.setRating(posMove.getRating());
                    minMove.setGameState(move);
                }
                beta = Math.min(beta, posMove.getRating());
                if(beta <= alpha) {
                    break;
                }
            }
            return minMove;
        }
    }
}
