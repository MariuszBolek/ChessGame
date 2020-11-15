package game.pieces;

import game.enums.PieceColor;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color, 1);
    }

    @Override
    public String getPieceUnicode() {
        if (color == PieceColor.WHITE) {
            return "\u2659";
        } else {
            return "\u265F";
        }
    }

    @Override
    public Piece copy() {
        return new Pawn(color);
    }
}
