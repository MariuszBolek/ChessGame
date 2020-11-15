package game.pieces;

import game.enums.PieceColor;

public class Bishop extends Piece {
    public Bishop(PieceColor color) {
        super(color, 3);
    }

    @Override
    public String getPieceUnicode() {
        if (color == PieceColor.WHITE) {
            return "\u2657";
        } else {
            return "\u265D";
        }
    }

    @Override
    public Piece copy() {
        return new Bishop(color);
    }
}
