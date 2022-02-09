package sc.player2022.logic;

import sc.api.plugins.ITeam;
import sc.plugin2022.*;
import sc.plugin2022.Vector;

import java.util.*;


public class NewEvaluate {

    private static final double[][] ratingNormal = {
            {3, 1.8, 1.25, 1, 0.75, 0, 0},
            {3, 2, 3, 1.75, 1.5, 1, 0, 0 },
            {3, 2, 3, 1.75, 1.5, 1, 0, 0 },
            {3, 2, 3, 1.75, 1.5, 1, 0, 0 },
            {3, 2, 3, 1.75, 1.5, 1, 0, 0 },
            {3, 2, 3, 1.75, 1.5, 1, 0, 0 },
            {3, 1.8, 1.25, 1, 0.75, 0, 0}};

    private static final double[][] ratingRobbe = {
            {0, 0, 0, 0, 0, 0 ,0},
            {0, 1, 1, 1, 1, 0.25, 0},
            {0, 1, 2, 2, 2, 0.5, 0},
            {0, 1, 2, 2, 2, 0.5, 0},
            {0, 1, 2, 2, 2, 0.5, 0},
            {0, 1, 1, 1, 1, 0.25, 0},
            {0, 0, 0, 0, 0, 0 ,0}};

    /*
    ziel ist es 2 bernsteine zu bekommen.
    so weiter eien figut ist desto besser ist es
    bernsteine sind am wichtigsten
     */

    // To do player does not know that if he gets an point to stack to 3 aka get a point the piece can't be attacked anymore. cousese some realy strange choice of play, important fix!
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
            } else if(gameState.getTurn() == 60){
                System.out.println("neutral");
            } else {
                System.out.println("loosing gameState");
                return Float.MIN_VALUE;
            }
        }
        rating += checkIfAPieceCanAttack(gameState);

        rating += checkIfPieceCanGetAttacked(gameState);

        rating += pointsForDistanceFromStart(gameState);

//        rating += pieceEndOfBoard(gameState);


        return rating;
    }

    private static float pieceEndOfBoard(GameState gameState) {
        float rating = 0.0f;
        //get all pieces on the board
        Board board = gameState.getBoard();
        List<PieceAndCords> friendly = new ArrayList<>();
        List<PieceAndCords> enemy = new ArrayList<>();
        for(Coordinates cords: board.keySet()) {
            Piece piece = board.get(cords);
            if(piece.getType() == PieceType.Robbe) {
                continue;
            }
            if(piece.getTeam() == playerTeam) {
                friendly.add(new PieceAndCords(piece, cords));
            } else {
                enemy.add(new PieceAndCords(piece, cords));
            }
        }
        for(PieceAndCords piece: friendly) {
            if(playerTeam == gameState.getStartTeam()) {
                if(piece.getCords().getX() >=5) {
                    rating += (float) Math.pow(1.3, piece.getCords().getX());
                }
            } else {
                if(piece.getCords().getX() <=2) {
                    rating += (float) 6.274 - piece.getCords().getX();
                }
            }
        }
        for(PieceAndCords piece: enemy) {
            if(playerTeam == gameState.getStartTeam()) {
                if(piece.getCords().getX() >=6) {
                    rating += Math.pow(2, piece.getCords().getX());
                }
            } else {
                if(piece.getCords().getX() <=3) {
                    rating += 7 - piece.getCords().getX();
                }
            }
        }
        return rating;
    };


    private static float checkIfAPieceCanAttack(GameState gameState) {
        float rating = 0;
        Board board = gameState.getBoard();
        List<Coordinates> allPieces = getAllPieces(gameState);

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


    private static float checkIfPieceCanGetAttacked(GameState gameState) {
        float rating = 0;
        Board board = gameState.getBoard();
        List<Coordinates> allPieces = getAllPieces(gameState);
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


    private static float pointsForDistanceFromStart(GameState gameState) {
        float rating = 0.0f;
        List<Coordinates> allCords = getAllPieces(gameState);
        for (Coordinates cords : allCords) {
            Piece piece = Objects.requireNonNull(gameState.getBoard().get(cords));
            if (piece.getTeam() != playerTeam) {
                continue;
            }
                if (gameState.getStartTeam() == playerTeam) {
                    rating += piece.getType() == PieceType.Robbe ? ratingRobbe[cords.getY()][7-cords.getX()] : ratingNormal[cords.getY()][cords.getX()];
                } else {
                    rating += piece.getType() == PieceType.Robbe ? ratingRobbe[cords.getY()][cords.getX()] : ratingNormal[cords.getY()][cords.getX()];
                }
            rating += 10;
//            switch (piece.getType()) {
//                case Herzmuschel:
//                    rating += 2;
//                    break;
//                case Moewe:
//                    rating += 2;
//                    break;
//                case Seestern:
//                    rating += 2;
//                    break;
//                case Robbe:
//                    rating += 3.0;
//                    break;
//            }
            if (piece.isAmber()) {
                rating += 16.0f; // vorher einmal 6 und einmal 10
            }
            if (piece.getCount() > 1) {
                rating += piece.getCount() * 7.0f;
            }
        }
        return rating;
    }



    private static List<Coordinates>  getAllPieces(GameState gameState) {
        List<Coordinates> allPieces = new ArrayList<>();
        Board board = gameState.getBoard();
        Set<Coordinates> allCords = board.keySet();
        for(Coordinates cords : allCords) {
            Piece piece = board.get(cords);
            if(piece.getCount() >= 3) {
                continue;
            }
            allPieces.add(cords);
        }
        return allPieces;
    }
}
