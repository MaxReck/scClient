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

/**
 * Das Herz des Clients:
 * Eine sehr simple Logik, die ihre Zuege zufaellig waehlt,
 * aber gueltige Zuege macht.
 * <p>
 * Ausserdem werden zum Spielverlauf Konsolenausgaben gemacht.
 */
public class Logic implements IGameHandler {
  private static final Logger log = LoggerFactory.getLogger(Logic.class);

  /** Aktueller Spielstatus. */
  private GameState gameState;

  public void onGameOver(GameResult data) {
    log.info("Das Spiel ist beendet, Ergebnis: {}", data);
  }

  @Override
  public Move calculateMove() {
    long startTime = System.currentTimeMillis();
    log.info("Es wurde ein Zug von {} angefordert.", gameState.getCurrentTeam());

    List<Move> possibleMoves = gameState.getPossibleMoves();
    Move move = possibleMoves.get((int) (Math.random() * possibleMoves.size()));
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
    if(gameState == null) {
      return null;
    }
    GameState temp;
    List<GameState> nextLvlGameStates = new ArrayList<>();
    for(Move move: gameState.getPossibleMoves()) {
        temp = gameState.clone();
        temp.performMove(move); // perform move to create a different game state
        nextLvlGameStates.add(temp);

    }
    return nextLvlGameStates;

    public GaemState minMax(int depth, boolean Maximise, int alpha, int beta, GameState gameState) {
      if(depth == 0) {
        return Evalutae.rateGameState(gameState, getCurrentTeam)
      }
      // max player turn (friendly team)
      if(Maximise) {
        float maxValue = Float.MinValue;
        for(GameState possibleGameStates: GameState.possibleMoves()) {
          float rating = minMax(depth-1, false, alpha, beta, possibleGameStates)
          if(rating > MaxValue) {
            MaxValue = rating;
          } 
        }
        return maxValue;
        // min players turn 
      } else{
        float minValue = float.maxValue;
        for(GameState gaemState: GameState.possibleMoves()) {
          float rating = Evalute.rateGameState(gameState, getCurrentTeam);
          if(rating < minValue) {
            minValue = rating;
          }

        }
      }
    }
  }
