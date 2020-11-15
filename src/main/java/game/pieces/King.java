package game.pieces;

import game.enums.PieceColor;

public class King extends Piece {
    public King(PieceColor color) {
        super(color, 100);
    }

    @Override
    public String getPieceUnicode() {
        if (color == PieceColor.WHITE) {
            return "\u2654";
        } else {
            return "\u265A";
        }
    }

    @Override
    public Piece copy() {
        return new King(color);
    }
}
