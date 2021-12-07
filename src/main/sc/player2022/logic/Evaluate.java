package sc.player2022.logic;

import sc.api.plugins.ITeam;
import sc.plugin2022.Coordinates;
import sc.plugin2022.GameState;
import sc.plugin2022.Piece;
import sc.plugin2022.PieceType;

import java.util.Map;

public  class Evaluate {
    public static float rateGameState(GameState gameState, ITeam team) {
        float rating = 0.0f;
        rating += gameState.getPointsForTeam(team) - gameState.getPointsForTeam(gameState.getOtherTeam());
        rating += getPointsForPieces(gameState.getCurrentTeam(), gameState) - getPointsForPieces(gameState.getOtherTeam(), gameState);
        return rating;
    }

    private static float getPointsForPieces(ITeam team, GameState gameState) {
        Map<Coordinates, Piece> allpices = gameState.getCurrentPieces();
        float rating = 0.0f;
        for (Piece pice : allpices.values()) {
            if (pice.getTeam() == team) continue;
            if (pice.getType() == PieceType.Herzmuschel) rating += 2;
            else if (pice.getType() == PieceType.Moewe) rating += 2;
            else if (pice.getType() == PieceType.Seestern) rating += 2.3;
            else if (pice.getType() == PieceType.Robbe) rating += 3;
        }
        return rating;
    }         
}
