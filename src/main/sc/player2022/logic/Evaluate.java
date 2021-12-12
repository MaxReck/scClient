package sc.player2022.logic;

import sc.api.plugins.ITeam;
import sc.plugin2022.Coordinates;
import sc.plugin2022.GameState;
import sc.plugin2022.Piece;
import sc.plugin2022.PieceType;

import java.util.Map;

public  class Evaluate {
    public static long eval;
    public static float rateGameState(GameState gameState, ITeam team) {
        eval++;
        float rating = 0.0f;
        rating += (gameState.getPointsForTeam(team) *5 - gameState.getPointsForTeam(gameState.getOtherTeam()) *5);
        rating += getPointsForPieces(gameState, team);

        return rating;
    }

    private static float getPointsForPieces(GameState gameState, ITeam team) {
        float rating = 0.0f;
        Map<Coordinates, Piece> teamPieces = gameState.getCurrentPieces();
        for(Coordinates cords: teamPieces.keySet()) {
//            System.out.println(cords.getX());
            if(gameState.getStartTeam() == team) {
                rating += cords.getX()*0.5;
//                System.out.println("r team 1 added r is " + cords.getX()*0.5);
            } else {
                rating += (cords.getX()-7)*(-1)*0.5;
//                System.out.print("r team 2 added r is ");
//                System.out.println( (cords.getX()-7)*(-1)*0.2);

            }

            Piece piece = teamPieces.get(cords);
            if(piece.getTeam() != gameState.getCurrentTeam()) {
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
                rating += 4.0f;
            }
        }
        return rating;
    }
}
