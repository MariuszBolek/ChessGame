package game.pieces;

import game.enums.PieceColor;

public class Queen extends Piece {
    public Queen(PieceColor color) {
        super(color, 10);
    }

    @Override
    public String getPieceUnicode() {
        if (color == PieceColor.WHITE) {
            return "\u2655";
        } else {
            return "\u265B";
        }
    }

    @Override
    public Piece copy() {
        return new Queen(color);
    }
}
