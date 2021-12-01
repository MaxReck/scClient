package sc.player2022.logic;

import sc.api.plugins.Team;

import sc.plugin2022.GameState;

public class Evaluate {
    public static float rateGameState(GameState gameState, Team team) {
        float rating = 0.0f;
        rating += gameState.getPointsForTeam(team) - gameState.getPointsForTeam(gameState.getOtherTeam());
        


        return rating;
    }

    private static int getPointsForPieces(Team team, GameState gameState) {
        float rating = 0;
        Map<Piece> pices = new ArrayList(gameState.getPices(team));
        for(Pices pice: pcies) {
            //String name= pice.getName();
            //if (name == möwe) rating += 
        } 

        
    }         
}
