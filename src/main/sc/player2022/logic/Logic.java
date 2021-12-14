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
        Evaluate.setPlayerTeam(this.gameState.getCurrentTeam());
        int depth = 1;
        PosMove posMove;
//        do {
//            posMove= miniMax(depth, Float.MIN_VALUE, Float.MAX_VALUE, true, this.gameState);
//            System.out.println("depth= " +  depth);
//            System.out.println("eval= " + Evaluate.eval);
//            System.out.println("time= " + (System.currentTimeMillis() - startTime));
//            depth++;
//            Evaluate.eval = 0;
//
//        } while( System.currentTimeMillis() - startTime <600);
        posMove= miniMax(4, Float.MIN_VALUE, Float.MAX_VALUE, true, this.gameState);

        Move move = posMove.getGameState().getLastMove();
        log.info("Sende {} nach {}ms. evals are {} rating is " + posMove.getRating(), move, System.currentTimeMillis() - startTime, Evaluate.eval);
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
        if (gameState == null) {
            System.out.println("whyyyyy");
            throw(new NullPointerException("Null"));
        }
        GameState temp;
        List<GameState> nextLvlGameStates = new ArrayList<>();
        for (Move move : gameState.getPossibleMoves()) {
            temp = gameState.clone();
            temp.performMove(move); // perform move to create a different game state
            nextLvlGameStates.add(temp);
        }
//        System.out.println("first move is " + nextLvlGameStates.get(0).getLastMove());
        return nextLvlGameStates;
    }


    public PosMove miniMax( int depth, float alpha, float beta, boolean Maximum, GameState gameState)  {
        if(depth <= 0) {
            return new PosMove(gameState, Evaluate.rateGameState(gameState));
        }
        if(Maximum) {
            PosMove maxMove = new PosMove(null, Float.MIN_VALUE);
            for(GameState move: getGameStates(gameState)) {
                PosMove posMove = miniMax(depth-1, alpha, beta, false, move);
                if(posMove.getRating() > maxMove.getRating()) {
                    maxMove.setRating(posMove.getRating());
                    maxMove.setGameState(move);
//                    log.info("new best max move rating: {} move: {} depth is " + depth, maxMove.getRating(), maxMove.getGameState().getLastMove());
                }
                alpha = Math.max(alpha, posMove.getRating());
                if(beta <= alpha) {
                    break;
                }
            }
            return maxMove;
        } else {
            PosMove minMove = new PosMove(null, Float.MAX_VALUE);
            for(GameState move : getGameStates(gameState)) {
                PosMove posMove = miniMax(depth-1, alpha, beta, true, move);
                if(minMove.getRating() > posMove.getRating()) {
                    minMove.setRating(posMove.getRating());
                    minMove.setGameState(posMove.getGameState());
//                    log.info("new best min move rating: {} move: {} depth is " + depth, minMove.getRating(), minMove.getGameState().getLastMove());
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
