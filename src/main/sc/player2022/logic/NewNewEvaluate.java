package sc.player2022.logic;

import sc.api.plugins.ITeam;
import sc.plugin2022.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NewNewEvaluate {

    //points for the pieces at specific Coordinates format used = [y][x].
    //rating for all pieces except the Pieces "Robe".
    private static final double[][] ratingNormal = {
            {15, 6, 1.25, 1.11, 1, 0.75, 0.5, 0},
            {15, 6, 2, 1.75, 1.5, 1, 0.5, 0 },
            {15, 6, 2, 1.75, 1.5, 1, 0.5, 0 },
            {15, 6, 2, 1.75, 1.5, 1, 0.5, 0 },
            {15, 6, 2, 1.75, 1.5, 1, 0.5, 0 },
            {15, 6, 2, 1.75, 1.5, 1, 0.5, 0 },
            {15, 6, 2, 1.75, 1.5, 1, 0.5, 0 },
            {15, 6, 1.25, 1.11, 1, 0.75, 0, 0}};

    //rating for the piece "Robe".
    private static final double[][] ratingHeavy = {
            {0, 0, 0, 0, 0, 0, 0 ,0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}};

    private static ITeam playerTeam;

    //tempVariables
    private static final List<PieceAndCords> allFriendlyPieces = new ArrayList<>();
    private static final List<PieceAndCords> allEnemyPieces = new ArrayList<>();
    private static Board board;

    public static void setPlayerTeam(ITeam team) {
        playerTeam = team;
    }

    public static double evaluateGameState(GameState gameState) {
        double rating = (gameState.getPointsForTeam(playerTeam) - gameState.getPointsForTeam(playerTeam.opponent())) * 75;
        if(gameState.isOver()) {
            if(gameState.getPointsForTeam(playerTeam) >= 2) {
                //this is a gameState where playerTeam wins
                rating += 250;
            } else {
                //this is a gameState where playerTeam loses so a bad gameState
                // hence return the min most value.
                return Double.MIN_VALUE;
            }
        }
        setUpTempVariables(gameState);

        rating += (potentialAttack(true) + potentialAttack(false))*2;

        rating += 5*(distance(gameState, true) + distance(gameState, false));

        rating += rateIndividualPieces()*10;

        //reset all temp variables
        board = null;
        allEnemyPieces.clear();
        allFriendlyPieces.clear();
        return rating;
    }

    private static double rateIndividualPieces() {
        double rating = 0.0d;

        //Enemy Team
        for(PieceAndCords friendly: allEnemyPieces) {
            switch (friendly.getPiece().getType()) {
                case Herzmuschel:
                    rating -= 1;
                    break;
                case Moewe:
                    rating -= 1.5;
                    break;
                case Seestern:
                    rating -= 1.4;
                    break;
                case Robbe:
                    rating -= 2.0;
                    break;
            }
//            if(friendly.getPiece().isAmber()) {
//                rating -= 16.0f;
//            }
            if(friendly.getPiece().getCount() >1) {
                rating -= friendly.getPiece().getCount()*6.0f;
            }
        }

        //Friendly Team
        for(PieceAndCords friendly: allFriendlyPieces) {
            switch (friendly.getPiece().getType()) {
                case Herzmuschel:
                    rating += 1;
                    break;
                case Moewe:
                    rating += 1.5;
                    break;
                case Seestern:
                    rating += 1.4;
                    break;
                case Robbe:
                    rating += 2.0;
                    break;
            }
//            if(friendly.getPiece().isAmber()) {
//                rating += 16.0f;
//            }
            if(friendly.getPiece().getCount() >1) {
                rating += friendly.getPiece().getCount()*6.0f;
            }
        }

        return rating;
    }

    //checks if any Piece has the potential to attack.
    // if ratedTeam true the function looks at friendlyPieces if false the other team.
    private static double potentialAttack(boolean ratedTeam) {
        double rating = 0.0d;
        if(ratedTeam) {
            for(PieceAndCords friendly: allFriendlyPieces) {
                List<Vector> possibleMoves = friendly.getPiece().getPossibleMoves();
                for(Vector vector: possibleMoves) {
                    Coordinates possibleCoordinate = friendly.getCords().plus(vector);
                    for(PieceAndCords enemy: allEnemyPieces) {
                        //check if the new Coordinate matches any of the known enemy locations
                        if(enemy.getCords().equals(possibleCoordinate)) {
                            //if enemy has more than one Piece stacked reward more Points
                            rating += enemy.getPiece().getCount() > 1 ? 4 : 1.5;
                        }
                    }
                }

            }
        } else {
            for(PieceAndCords friendly: allEnemyPieces) {
                List<Vector> possibleMoves = friendly.getPiece().getPossibleMoves();
                for(Vector vector: possibleMoves) {
                    Coordinates possibleCoordinate = friendly.getCords().plus(vector);
                    for(PieceAndCords enemy: allFriendlyPieces) {
                        //check if the new Coordinate matches any of the known enemy locations
                        if(enemy.getCords().equals(possibleCoordinate)) {
                            //if enemy has more than one Piece stacked reward more Points
                            rating -= enemy.getPiece().getCount() > 1 ? 4 : 1.5;
                        }
                    }
                }
            }
        }
        return rating;
    }

    // rate the pieces on the board based on distance from start.
    // if ratedTeam true the function looks at friendlyPieces if false the other team.
    private static double distance(GameState gameState, boolean ratedTeam) {
        double rating = 0.0d;

        if (ratedTeam) {
            //Rating FriendlyTeam
            if (playerTeam == gameState.getStartTeam()) {
                for (PieceAndCords cords : allFriendlyPieces) {
                            if(cords.getPiece().getType() == PieceType.Robbe) {
                                rating += ratingHeavy[cords.getCords().getY()][7 - cords.getCords().getX()]*0.3;
                            } else {
                                rating += ratingNormal[cords.getCords().getY()][7 - cords.getCords().getX()];
                            }
                }
            } else {
                for (PieceAndCords cords : allFriendlyPieces) {
                    if(cords.getPiece().getType() == PieceType.Robbe) {
                        rating += ratingHeavy[cords.getCords().getY()][7 - cords.getCords().getX()]*0.3;
                    } else {
                        rating += ratingNormal[cords.getCords().getY()][7 - cords.getCords().getX()];
                    }
                }
            }
        } else {
            //Rating Enemy
            if (playerTeam == gameState.getStartTeam()) {
                for (PieceAndCords cords : allEnemyPieces) {
                    if (cords.getPiece().getType() == PieceType.Robbe) {
                        rating += ratingHeavy[cords.getCords().getY()][cords.getCords().getX()] * 0.3;
                    } else {
                        rating += ratingNormal[cords.getCords().getY()][cords.getCords().getX()];
                    }
                }
            } else {
                for (PieceAndCords cords : allEnemyPieces) {
                    if (cords.getPiece().getType() == PieceType.Robbe) {
                        rating += ratingHeavy[cords.getCords().getY()][cords.getCords().getX()] * 0.3;
                    } else {
                        rating += ratingNormal[cords.getCords().getY()][cords.getCords().getX()];
                    }
                }
            }
        }
        return rating;
    }

    // setting up commonly used Variables/Lists
    private static void setUpTempVariables(GameState gameState) {

        board = gameState.getBoard();
        Set<Coordinates> allPieces = board.getKeys();

        for(Coordinates coordinate: allPieces) {
            Piece piece = board.get(coordinate);
            if(piece == null)   {
                continue;
            }
            if(piece.getTeam() == playerTeam) {
                allFriendlyPieces.add(new PieceAndCords(piece, coordinate));
            }else {
                allEnemyPieces.add(new PieceAndCords(piece, coordinate));
            }
        }
    }
}
