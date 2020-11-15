package game.pieces;

import game.enums.PieceColor;

public class ColorSwap {
    private ColorSwap() {
    }

    public static PieceColor swap(PieceColor color) {
        if (color == PieceColor.WHITE) {
            return PieceColor.BLACK;
        } else {
            return PieceColor.WHITE;
        }
    }
}
