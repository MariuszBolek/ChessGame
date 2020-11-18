package game.pieces;

import game.enums.PieceColor;

public abstract class Piece {
    final protected PieceColor color;
    final protected int value;

    public Piece(PieceColor color, int value) {
        this.color = color;
        this.value = value;
    }

    //Each chess piece is represented as unicode, so we can search for it instead of loading images to represent pieces
    abstract public String getPieceUnicode();

    public PieceColor getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
    public String toString() {
        return this.color + " " + this.getClass().getSimpleName();
    }

    public abstract Piece copy();

}
