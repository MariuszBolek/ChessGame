package game.pieces;

import game.enums.PieceColor;

public class Rook extends Piece {
    public Rook(PieceColor color) {
        super(color, 5);
    }

    @Override
    public String getPieceUnicode() {
        if (color == PieceColor.WHITE) {
            return "\u2656";
        } else {
            return "\u265C";
        }
    }

    @Override
    public Piece copy() {
        return new Rook(color);
    }
}
