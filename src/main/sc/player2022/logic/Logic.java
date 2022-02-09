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
                posMove.set(miniMax(depth.get(), Float.MIN_VALUE, Float.MAX_VALUE, true, this.gameState));
                depth.getAndIncrement();
            }
        });
        bababui.start();
        while (System.currentTimeMillis() - startTime < 1900) {
        }
        bababui.stop();
//        do {
//            posMove= miniMax(depth, Float.MIN_VALUE, Float.MAX_VALUE, true, this.gameState);
//            System.out.println("depth= " +  depth);
//            System.out.println("eval= " + Evaluate.eval);
//            System.out.println("time= " + (System.currentTimeMillis() - startTime));
//            depth++;
//            Evaluate.eval = 0;
//
//        } while( System.currentTimeMillis() - startTime <600);
        //posMove.set(miniMax(10, Float.MIN_VALUE, Float.MAX_VALUE, true, this.gameState));

        log.info(depth + " Tiefe");
        // Fix für den Null Fehler sehr schlecht
        if (posMove.get().getGameState() == null) {
            GameState emergencyGameState = getGameStates(this.gameState).get(0);
            posMove.get().setGameState(emergencyGameState);
        }
        Move move = posMove.get().getGameState().getLastMove();
        log.info("Sende {} nach {}ms. evals are {} rating is " + posMove.get().getRating(), move, System.currentTimeMillis() - startTime, Evaluate.eval);
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
//        System.out.println("first move is " + nextLvlGameStates.get(0).getLastMove());
        return nextLvlGameStates;
    }




    public PosMove miniMax( int depth, float alpha, float beta, boolean Maximum, GameState gameState)  {
        if(depth <= 0) {
            return new PosMove(gameState,(float) NewNewEvaluate.evaluateGameState(gameState));
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
