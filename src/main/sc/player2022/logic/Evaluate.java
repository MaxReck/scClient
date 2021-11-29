package sc.player2022.logic;

import sc.api.plugins.Team;

import sc.plugin2022.GameState;

public class Evaluate {
    public static float rateGameState(GameState gameState, Team team) {
        float rating = 0.0f;
        rating += gameState.getPointsForTeam(team) - gameState.getPointsForTeam(gameState.getOtherTeam());



        return rating;
    }
}
