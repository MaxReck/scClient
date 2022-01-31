package sc.player2022.logic;

import sc.api.plugins.ITeam;
import sc.plugin2022.*;
import sc.plugin2022.Vector;

import java.util.*;


public class NewEvaluate {

    /*
    ziel ist es 2 bernsteine zu bekommen.
    so weiter eien figut ist desto besser ist es
    bernsteine sind am wichtigsten
     */
    private static ITeam playerTeam;

    public static void setPlayerTeam(ITeam playerTeam) {
        NewEvaluate.playerTeam = playerTeam;
    }


    public static float evaluatePosition(GameState gameState) {
        float rating = (float) (gameState.getPointsForTeam(playerTeam) - (gameState.getPointsForTeam(playerTeam.opponent()) * 1.2));
        if (gameState.isOver()) {
            if(gameState.getPointsForTeam(playerTeam) >= 2) {
                System.out.println("winning game state");
                rating += 100;
            } else {
                System.out.println("loosing gameState");
                return Float.MIN_VALUE;
            }
        }
        rating += checkIfAPieceCanAttack(gameState);

        rating += checkIfPieceCanGetAttacked(gameState);

        rating += pointsForDistanceFromStart(gameState);

        rating += (gameState.getPointsForTeam(playerTeam) * 20 - gameState.getPointsForTeam(playerTeam.opponent()) * 20);

        return rating;
    }


    public static float checkIfAPieceCanAttack(GameState gameState) {
        float rating = 0;
        Board board = gameState.getBoard();
        Set<Coordinates> allPieces = board.keySet();

        //split the 2 teams Pieces in to 2 list;
        List<PieceAndCords> friendlyPieces = new LinkedList<>();
        List<Coordinates> enemyPieces = new LinkedList<>();
        for (Coordinates cords : allPieces) {
            Piece piece = board.get(cords);
            if (piece.getTeam() == playerTeam) {
                friendlyPieces.add(new PieceAndCords(piece, cords));
            } else {
                enemyPieces.add(cords);
            }
        }
        //calculate the new Coordinates by combining the known position with the vector given by the getPossibleMoves() function
        for (PieceAndCords friendly : friendlyPieces) {
            List<Vector> a = friendly.getPiece().getPossibleMoves();
            for (Vector vector : a) {
                Coordinates oldCords = friendly.getCords();
                Coordinates newCords = oldCords.plus(vector);
                //check if the calculated cords of for the move match any of the known friendly cords if so deduct 6 points;
                for (Coordinates cords : enemyPieces) {
                    if (newCords.equals(cords)) {
                        rating += board.get(cords).getCount() > 1 ? 4 : 1.5;
                    }
                }
            }
        }
        return rating;
    }


    public static float checkIfPieceCanGetAttacked(GameState gameState) {
        float rating = 0;
        Board board = gameState.getBoard();
        Set<Coordinates> allPieces = board.keySet();
        //split the 2 teams Pieces in to 2 list;
        List<PieceAndCords> enemyPieces = new LinkedList<>();
        List<Coordinates> friendlyPieces = new LinkedList<>();

        for (Coordinates cords : allPieces) {
            Piece piece = board.get(cords);
            if (piece.getTeam() != playerTeam) {
                enemyPieces.add(new PieceAndCords(piece, cords));
            } else {
                friendlyPieces.add(cords);
            }
        }
        //calculate the new Coordinates by combining the known position with the vector given by the getPossibleMoves() function
        for (PieceAndCords enemy : enemyPieces) {
            List<Vector> a = enemy.getPiece().getPossibleMoves();
            for (Vector vector : a) {
                Coordinates oldCords = enemy.getCords();
                Coordinates newCords = oldCords.plus(vector);
                //check if the calculated cords of for the move match any of the known friendly cords if so deduct 6 points;
                for (Coordinates cords : friendlyPieces) {
                    if (newCords == cords) {
                        rating -= board.get(cords).getCount() > 1 ? 10 : 1;
                    }
                }
            }
        }
        return rating;
    }


    public static float pointsForDistanceFromStart(GameState gameState) {
        float rating = 0.0f;
        Set<Coordinates> keySet = gameState.getBoard().getKeys();
        for (Coordinates cords : keySet) {
            Piece piece = Objects.requireNonNull(gameState.getBoard().get(cords));
            if (piece.getTeam() == playerTeam) {
                if (gameState.getStartTeam() == playerTeam) {
                    rating += cords.getX() * (piece.getType() != PieceType.Robbe ? 0.5 : 0.2);
                } else {
                    rating += (cords.getX() - 7) * (-1) * (piece.getType() != PieceType.Robbe ? 0.5 : 0.2);
                }
            }
            if (piece.getTeam() != playerTeam) {
                continue;
            }
            switch (piece.getType()) {
                case Herzmuschel:
                    rating += 1;
                    break;
                case Moewe:
                    rating += 1;
                    break;
                case Seestern:
                    rating += 1;
                    break;
                case Robbe:
                    rating += 2.0;
                    break;
            }
            if (piece.isAmber()) {
                rating += 16.0f; // vorher einmal 6 und einmal 10
            }
            if (piece.getCount() > 1) {
                rating += piece.getCount() * 4.0f;
            }
        }
        return rating;
    }


//    private static float checkIfAnPieceCanGetPointWithReachingTheOtherSide(GameState gameState) {
//        Set<Coordinates> keySet = gameState.getBoard().getKeys();
//
//    }
}
