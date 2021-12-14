package sc.player2022.logic;

import sc.api.plugins.ITeam;
import sc.plugin2022.Coordinates;
import sc.plugin2022.GameState;
import sc.plugin2022.Piece;
import sc.plugin2022.PieceType;

import java.util.Objects;
import java.util.Set;

public  class Evaluate {
    public static long eval;
    private static ITeam playerTeam;

    public static void setPlayerTeam(ITeam playerTeam) {
        Evaluate.playerTeam = playerTeam;
    }

    public static float rateGameState(GameState gameState) {
        eval++;
        float rating = 0.0f;
        rating += (gameState.getPointsForTeam(playerTeam) *20 - gameState.getPointsForTeam(playerTeam.opponent()) *20);
        rating += getPointsForPieces(gameState);

        return rating;
    }

    private static float getPointsForPieces(GameState gameState) {
        float rating = 0.0f;
        Set<Coordinates> keySet = gameState.getBoard().getKeys();
        for(Coordinates cords: keySet) {

            if(Objects.requireNonNull(gameState.getBoard().get(cords)).getTeam() == playerTeam) {

                if(gameState.getStartTeam() == playerTeam) {
                    rating += cords.getX()*0.5;
                } else {
                    rating += (cords.getX()-7)*(-1)*0.5;
                }
            }
            Piece piece = gameState.getBoard().get(cords);
            assert piece != null;
            if(piece.getTeam() != playerTeam) {
                continue;
            }
            if(piece.getType() == PieceType.Herzmuschel) {
                rating += 1;
            }
            if(piece.getType() == PieceType.Moewe) {
                rating += 0.9;
            }
            if(piece.getType() == PieceType.Seestern) {
                rating += 1.4;
            }
            if(piece.getType() == PieceType.Robbe) {
                rating += 2.0f;
            }
            if(piece.isAmber()) {
                rating += 6.0f;
            }
            if(piece.getCount() >1) {
                rating += piece.getCount()*4.0f;
            }
            if(piece.isAmber()) {
                rating += 10;
            }
        }
        return rating;
    }
}
