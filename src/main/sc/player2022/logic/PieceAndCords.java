package sc.player2022.logic;

import sc.plugin2022.Coordinates;
import sc.plugin2022.Piece;

public class PieceAndCords {
    private final Piece piece;
    private final Coordinates cords;

    public PieceAndCords(Piece piece, Coordinates cords) {
        this.piece = piece;
        this.cords = cords;
    }
    public Piece getPiece() {
        return piece;
    }
    public Coordinates getCords() {
        return cords;
    }
}
