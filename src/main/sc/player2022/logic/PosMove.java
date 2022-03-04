package sc.player2022.logic;

import sc.plugin2022.GameState;

public class PosMove {
    private GameState gameState;
    private double rating;

    public PosMove(GameState gameState, double rating) {
        this.gameState = gameState;
        this.rating = rating;
    }

    public GameState getGameState() {
        return gameState;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

}
