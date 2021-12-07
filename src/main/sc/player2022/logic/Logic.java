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
        PosMove posMove= minMax(5,true,Float.MIN_VALUE, Float.MAX_VALUE, gameState);
        Move move = posMove.getGameState().getLastMove();
        log.info("Sende {} nach {}ms.", move, System.currentTimeMillis() - startTime);
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
        if (gameState == null) {
            return null;
        }
        GameState temp;
        List<GameState> nextLvlGameStates = new ArrayList<>();
        for (Move move : gameState.getPossibleMoves()) {
            temp = gameState.clone();
            temp.performMove(move); // perform move to create a different game state
            nextLvlGameStates.add(temp);
        }
        return nextLvlGameStates;
    }


    public PosMove minMax(int depth, boolean Maximise, float alpha, float beta, GameState gameState) {
        if (depth == 0) {
            return new PosMove(gameState, Evaluate.rateGameState(gameState, gameState.getStartTeam()));
        }
        // max player turn (friendly team)
        if (Maximise) {
            PosMove maxValue = new PosMove(null, Float.MIN_VALUE);
            for (GameState possibleGameState : getGameStates(gameState)) {
                PosMove posMove = new PosMove(gameState, minMax(depth - 1, false, alpha, beta, possibleGameState).getRating());
                if (posMove.getRating() > maxValue.getRating()) {
                    maxValue.setRating(posMove.getRating());
                }
                alpha = Math.max(posMove.getRating(), alpha);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxValue;
        } else {
            // min players turn
            PosMove minValue = new PosMove(null, Float.MAX_VALUE);
            for (GameState possibleGameState : getGameStates(gameState)) {
                PosMove posMove = new PosMove(gameState, minMax(depth - 1, false, alpha, beta, possibleGameState).getRating());
                if (posMove.getRating() < minValue.getRating()) {
                    minValue.setRating(posMove.getRating());
                }
                beta = Math.max(posMove.getRating(), beta);
                if (beta <= alpha) {
                    break;
                }
            }
            return minValue;
        }
    }
}
