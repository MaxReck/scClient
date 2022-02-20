package sc.player2022.logic;

import sc.plugin2022.GameState;

public class PosMove {
    private GameState gameState;
    private Float rating;

    public PosMove(GameState gameState, Float rating) {
        this.gameState = gameState;
        this.rating = rating;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

}
