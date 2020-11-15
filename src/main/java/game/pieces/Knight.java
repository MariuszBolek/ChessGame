package game.pieces;

import game.enums.PieceColor;

public class Knight extends Piece {
    public Knight(PieceColor color) {
        super(color, 3);
    }

    @Override
    public String getPieceUnicode() {
        if (color == PieceColor.WHITE) {
            return "\u2658";
        } else {
            return "\u265E";
        }
    }

    @Override
    public Piece copy() {
        return new Knight(color);
    }
}
